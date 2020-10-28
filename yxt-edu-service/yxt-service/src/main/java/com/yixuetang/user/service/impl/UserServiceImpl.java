package com.yixuetang.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yixuetang.entity.request.user.PasswordUser;
import com.yixuetang.entity.request.user.RegisterUser;
import com.yixuetang.entity.request.user.UpdateUser;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.code.auth.AuthCode;
import com.yixuetang.entity.response.code.user.UserCode;
import com.yixuetang.entity.response.result.QueryResult;
import com.yixuetang.entity.user.Role;
import com.yixuetang.entity.user.School;
import com.yixuetang.entity.user.User;
import com.yixuetang.user.mapper.RoleMapper;
import com.yixuetang.user.mapper.SchoolMapper;
import com.yixuetang.user.mapper.UserMapper;
import com.yixuetang.user.service.UserService;
import com.yixuetang.utils.exception.ExceptionThrowUtils;
import com.yixuetang.utils.user.MailUtils;
import com.yixuetang.utils.user.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Colin
 * @version 1.0.0
 * @description 用户模块服务层接口实现类
 * @date 2020/10/23 16:09
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private SchoolMapper schoolMapper;

    @Autowired
    private MailUtils mailUtils;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final List<Integer> CODE_TYPE = new ArrayList<>(Arrays.asList(1, 2, 3));

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String LOGIN_KEY_PREFIX = "user:code:login:";
    private static final String REGISTER_KEY_PREFIX = "user:code:register:";
    private static final String MODIFY_KEY_PREFIX = "user:code:modify:";

    @Override
    public QueryResponse findAll() {
        List<User> users = this.userMapper.findAll();
        return new QueryResponse(CommonCode.SUCCESS, new QueryResult<>(users, users.size()));
    }

    @Override
    public QueryResponse findAllRoles() {
        List<Role> roles = this.roleMapper.selectList(null);
        return new QueryResponse(CommonCode.SUCCESS, new QueryResult<>(roles, roles.size()));
    }

    @Override
    public QueryResponse findAllSchools() {
        List<School> schools = this.schoolMapper.selectList(null);
        return new QueryResponse(CommonCode.SUCCESS, new QueryResult<>(schools, schools.size()));
    }

    @Override
    public CommonResponse sendCode(String email, int codeType) {
        // 邮箱地址或者 type 为非法参数
        if (StringUtils.isBlank(email) || !CODE_TYPE.contains(codeType)) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        User user = this.userMapper.selectOne(new QueryWrapper<User>().eq("email", email));

        // 如果是因登录和修改密码要求发送验证码，先确认该账号已经注册过
        if ((codeType == 1 || codeType == 3) && user == null) {
            return new CommonResponse(UserCode.EMAIL_NOT_REGISTERED);
        }

        // 如果是因注册要求发送验证码，先确认该账号尚未注册过
        if (codeType == 2 && user != null) {
            return new CommonResponse(UserCode.EMAIL_HAS_BEEN_REGISTERED);
        }

        // 生成六位数字验证码
        String code = NumberUtils.generateCode(6);
        try {
            // 发送验证码
            mailUtils.sendMail(email, "【益学堂】验证码",
                    "【益学堂】您的验证码是" + code + "，用于验证身份、修改密码等，该验证码5分钟内有效，请勿向他人泄露。");
        } catch (Exception e) {
            LOGGER.error("发送验证码异常！异常原因：{}", e);
            ExceptionThrowUtils.cast(CommonCode.SERVER_ERROR);
        }

        // 将验证码存入 redis ，并设置过期时间为 5 分钟
        switch (codeType) {
            case 1: // 登录验证码
                this.redisTemplate.opsForValue().set(LOGIN_KEY_PREFIX + email, code, 5, TimeUnit.MINUTES);
                break;
            case 2: // 注册验证码
                this.redisTemplate.opsForValue().set(REGISTER_KEY_PREFIX + email, code, 5, TimeUnit.MINUTES);
                break;
            case 3: // 修改密码验证码
                this.redisTemplate.opsForValue().set(MODIFY_KEY_PREFIX + email, code, 5, TimeUnit.MINUTES);
                break;
        }
        return new CommonResponse(CommonCode.SUCCESS);
    }

    @Override
    @Transactional
    public CommonResponse register(RegisterUser registerUser) {
        if (registerUser == null) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        // 0. 邮箱校验
        if (this.userMapper.selectOne(new QueryWrapper<User>().eq("email", registerUser.getEmail())) != null) {
            return new CommonResponse(UserCode.EMAIL_HAS_BEEN_REGISTERED);
        }
        // 1. 验证码校验
        if (!StringUtils.equals(registerUser.getCode(), this.redisTemplate.opsForValue().get(REGISTER_KEY_PREFIX + registerUser.getEmail()))) {
            return new CommonResponse(UserCode.REGISTER_FAIL_CODE_WRONG);
        }
        // 2. 用户名唯一性校验
        User foundUser = this.userMapper.selectOne(new QueryWrapper<User>().eq("username", registerUser.getUsername()));
        if (foundUser != null) {
            return new CommonResponse(UserCode.REGISTER_FAIL_USERNAME_CONFLICT);
        }
        // 3. 角色名称校验
        Role role = this.roleMapper.selectOne(new QueryWrapper<Role>().eq("r_name", registerUser.getRoleName()));
        if (role == null) {
            return new CommonResponse(UserCode.REGISTER_FAIL_ROLE_NAME_NOT_FOUND);
        }
        // 4. 将用户存入数据库
        User user = User.builder().id(null)
                .username(registerUser.getUsername())
                .email(registerUser.getEmail())
                .password(registerUser.getPassword())
                .realName(registerUser.getRealName())
                .school(registerUser.getSchool())
                .createTime(new Date())
                .updateTime(new Date())
                .build();
        this.userMapper.insert(user);
        // 5. 更新用户的角色id信息
        this.userMapper.updateRoleIdByUsername(user.getUsername(), role.getId());

        return new CommonResponse(CommonCode.SUCCESS);
    }

    @Override
    @Transactional
    public CommonResponse updateUser(UpdateUser updateUser) {

        //1.参数验证
        if (updateUser == null) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        // 2. 角色名称校验
        Role role = null;
        if (StringUtils.isNoneBlank(updateUser.getRoleName())){
            role =  this.roleMapper.selectOne(new QueryWrapper<Role>().eq("r_name", updateUser.getRoleName()));
            if (role == null) {
                return new CommonResponse(UserCode.UPDATE_FAIL_ROLE_NAME_NOT_FOUND);
            }
        }

        // 3. 学/工号唯一性检验
        if (updateUser.getTsNo() != null && !"".equals(updateUser.getTsNo())) {
            User oldUser = this.userMapper.selectOne(new QueryWrapper<User>().eq("id", updateUser.getId()));
            User foundUser = this.userMapper.selectOne(new QueryWrapper<User>().eq("ts_no", updateUser.getTsNo()));
            if ((foundUser != null) && (!StringUtils.equals(foundUser.getTsNo(), oldUser.getTsNo()))) { //若存在该学工号，判断是否是未修改前当前用户的学工号
                return new CommonResponse(UserCode.UPDATE_FAIL_TSNO_CONFLICT);
            }
        }

        // 4. 更新最后一次修改个人信息时间
        updateUser.setUpdateTime(new Date());

        // 5. 更新用户信息
        this.userMapper.updateUser(updateUser);

        // 6. 更新用户的角色id信息
        this.userMapper.UpdateRoleIdById(Objects.requireNonNull(role).getId(),updateUser.getId());

        return new CommonResponse(CommonCode.SUCCESS);
    }

    @Override
    public CommonResponse updatePassword(long id, PasswordUser passwordUser) {

        //1.参数验证
        if (passwordUser == null) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        // 2. 根据id查询当前用户密码，进行旧密码检验
        User oldUser = this.userMapper.selectOne(new QueryWrapper<User>().eq("id", id));
        if ( !StringUtils.equals(oldUser.getPassword(),passwordUser.getOldPassword()) ){
            return new CommonResponse(UserCode.PASSWORD_UPDATE_FAIL_OLD_PASSWORD_WRONG);
        }

        // 3. 验证码校验
        if (!StringUtils.equals(passwordUser.getCode(), this.redisTemplate.opsForValue().get(MODIFY_KEY_PREFIX + passwordUser.getEmail()))) {
            return new CommonResponse(UserCode.PASSWORD_UPDATE_FAIL_CODE_WRONG);
        }

        // 4. 更新最后一次修改个人信息时间
        passwordUser.setUpdateTime(new Date());

        // 5. 将新密码与更新时间信息更新至用户表中
        this.userMapper.updatePassworById(id,passwordUser);

        return new CommonResponse(CommonCode.SUCCESS);
    }
}
