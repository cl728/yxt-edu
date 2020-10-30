package com.yixuetang.user.service.impl;

import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yixuetang.entity.request.auth.LoginUser;
import com.yixuetang.entity.request.user.EmailUser;
import com.yixuetang.entity.request.user.PasswordUser;
import com.yixuetang.entity.request.user.RegisterUser;
import com.yixuetang.entity.request.user.UpdateUser;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
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
import com.yixuetang.utils.user.*;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final String LOGIN_KEY_PREFIX = "user:code:login:";
    private static final String REGISTER_KEY_PREFIX = "user:code:register:";
    private static final String MODIFY_KEY_PREFIX = "user:code:modify:";
    private static final String CHANGE_KEY_PREFIX = "user:code:change:";
    private final List<Integer> SEND_TYPE = new ArrayList<>(Arrays.asList(1, 2));
    private final List<Integer> CODE_TYPE = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private SchoolMapper schoolMapper;
    @Autowired
    private MailUtils mailUtils;
    @Autowired
    private SmsUtils smsUtils;
    @Autowired
    private SmsConfig smsConfig;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public QueryResponse findOneUser(long id) {
        User user = this.userMapper.findOneUser(id);
        List<User> users = Collections.singletonList(user);
        return new QueryResponse(CommonCode.SUCCESS, new QueryResult<>(users, users.size()));
    }

    @Override
    public QueryResponse findByPage(long currentPage, long pageSize) {
        List<User> users = this.userMapper.findByPage(new Page<>(currentPage, pageSize));
        return new QueryResponse(CommonCode.SUCCESS, new QueryResult<>(users, users.size()));
    }

    @Override
    public QueryResponse findRolesByPage(long currentPage, long pageSize) {
        Page<Role> page = this.roleMapper.selectPage(new Page<>(currentPage, pageSize), null);
        return new QueryResponse(CommonCode.SUCCESS, new QueryResult<>(page.getRecords(), (int) page.getTotal()));
    }

    @Override
    public QueryResponse findAllSchools() {
        List<School> schools = this.schoolMapper.selectList(null);
        return new QueryResponse(CommonCode.SUCCESS, new QueryResult<>(schools, schools.size()));
    }

    @Override
    public QueryResponse findSchoolsByPage(long currentPage, long pageSize) {
        Page<School> page = this.schoolMapper.selectPage(new Page<>(currentPage, pageSize), null);
        return new QueryResponse(CommonCode.SUCCESS, new QueryResult<>(page.getRecords(), (int) page.getTotal()));
    }

    @Override
    public CommonResponse sendCode(LoginUser loginUser, int sendType, int codeType) {
        // 登录实体类或者 sendType 、codeType 为非法参数
        if (loginUser == null || !SEND_TYPE.contains(sendType) || !CODE_TYPE.contains(codeType)) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        User user;

        // 生成六位数字验证码
        String code = NumberUtils.generateCode(6);

        switch (sendType) {
            case 1: // 发送到手机
                // phone 为非法参数
                if (!ParamCheckUtils.checkPhone(loginUser.getPhone())) {
                    return new CommonResponse(CommonCode.INVALID_PARAM);
                }
                user = this.userMapper.selectOne(new QueryWrapper<User>().eq("phone", loginUser.getPhone()));

                // 如果是因登录和修改密码要求发送验证码到手机，先确认该账号已经注册过
                if ((codeType == 1 || codeType == 3) && user == null) {
                    return new CommonResponse(UserCode.PHONE_NOT_REGISTERED);
                }

                // 如果是因注册和换绑手机要求发送验证码到手机，先确认该账号尚未注册过
                if ((codeType == 2 || codeType == 4) && user != null) {
                    return new CommonResponse(UserCode.PHONE_HAS_BEEN_REGISTERED);
                }
                // 调用工具类发送验证码到手机
                try {
                    smsUtils.sendSms(loginUser.getPhone(), code, smsConfig.getSignName(), smsConfig.getVerifyCodeTemplate());
                } catch (ClientException e) {
                    LOGGER.error("发送验证码到用户手机异常！异常原因：{}", e);
                    ExceptionThrowUtils.cast(CommonCode.SERVER_ERROR);
                }
                break;
            case 2: // 发送到邮箱
                // email 为非法参数
                if (!ParamCheckUtils.checkEmail(loginUser.getEmail())) {
                    return new CommonResponse(CommonCode.INVALID_PARAM);
                }
                user = this.userMapper.selectOne(new QueryWrapper<User>().eq("email", loginUser.getEmail()));

                // 如果是因登录和修改密码要求发送验证码到邮箱，先确认该账号已经注册过
                if ((codeType == 1 || codeType == 3) && user == null) {
                    return new CommonResponse(UserCode.EMAIL_NOT_REGISTERED);
                }

                // 如果是因注册和换绑邮箱要求发送验证码到邮箱，先确认该账号尚未注册过
                if ((codeType == 2 || codeType == 4) && user != null) {
                    return new CommonResponse(UserCode.EMAIL_HAS_BEEN_REGISTERED);
                }

                try {
                    // 发送验证码
                    mailUtils.sendMail(loginUser.getEmail(), "【益学堂】验证码",
                            "【益学堂】您的验证码是" + code + "，用于验证身份、修改密码等，该验证码5分钟内有效，请勿向他人泄露。");
                } catch (Exception e) {
                    LOGGER.error("发送验证码到用户邮箱地址异常！异常原因：{}", e);
                    ExceptionThrowUtils.cast(CommonCode.SERVER_ERROR);
                }
        }

        // 将验证码存入 redis ，并设置过期时间为 5 分钟
        String key = sendType == 1 ? loginUser.getPhone() : loginUser.getEmail();
        switch (codeType) {
            case 1: // 登录验证码
                this.redisTemplate.opsForValue().set(LOGIN_KEY_PREFIX + key, code, 5, TimeUnit.MINUTES);
                break;
            case 2: // 注册验证码
                this.redisTemplate.opsForValue().set(REGISTER_KEY_PREFIX + key, code, 5, TimeUnit.MINUTES);
                break;
            case 3: // 修改密码验证码
                this.redisTemplate.opsForValue().set(MODIFY_KEY_PREFIX + key, code, 5, TimeUnit.MINUTES);
                break;
            case 4: // 换绑邮箱/手机验证码
                this.redisTemplate.opsForValue().set(CHANGE_KEY_PREFIX + key, code, 5, TimeUnit.MINUTES);
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
    public CommonResponse updateUser(long id, UpdateUser updateUser) {

        // 1.参数验证
        if (updateUser == null) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        // 2. 角色名称校验
        Role role = null;
        if (StringUtils.isNoneBlank(updateUser.getRoleName())) {
            role = this.roleMapper.selectOne(new QueryWrapper<Role>().eq("r_name", updateUser.getRoleName()));
            if (role == null) {
                return new CommonResponse(UserCode.UPDATE_FAIL_ROLE_NAME_NOT_FOUND);
            }
        }

        // 3. 学/工号唯一性检验
        if (StringUtils.isNoneBlank(updateUser.getTsNo())) {
            User oldUser = this.userMapper.selectOne(new QueryWrapper<User>().eq("id", id));
            User foundUser = this.userMapper.selectOne(new QueryWrapper<User>().eq("ts_no", updateUser.getTsNo()));
            if ((foundUser != null) && (!StringUtils.equals(foundUser.getTsNo(), oldUser.getTsNo()))) { //若存在该学工号，判断是否是未修改前当前用户的学工号
                return new CommonResponse(UserCode.UPDATE_FAIL_TSNO_CONFLICT);
            }
        }

        // 4. 更新最后一次修改个人信息时间
        updateUser.setUpdateTime(new Date());

        // 5. 更新用户信息
        this.userMapper.updateUser(id, updateUser);

        // 6. 更新用户的角色id信息
        this.userMapper.UpdateRoleIdById(Objects.requireNonNull(role).getId(), id);

        return new CommonResponse(CommonCode.SUCCESS);
    }

    @Override
    public CommonResponse updatePassword(long id, PasswordUser passwordUser) {

        // 1.参数验证
        if (passwordUser == null) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        // 2. 根据id查询当前用户密码，进行旧密码检验
        User oldUser = this.userMapper.selectOne(new QueryWrapper<User>().eq("id", id));
        if (!StringUtils.equals(oldUser.getPassword(), passwordUser.getOldPassword())) {
            return new CommonResponse(UserCode.UPDATE_PASSWORD_FAIL_OLD_PASSWORD_WRONG);
        }

        // 3. 验证码校验
        if (!StringUtils.equals(passwordUser.getCode(), this.redisTemplate.opsForValue().get(MODIFY_KEY_PREFIX + passwordUser.getEmail()))) {
            return new CommonResponse(UserCode.UPDATE_PASSWORD_FAIL_CODE_WRONG);
        }

        // 4. 更新最后一次修改个人信息时间
        passwordUser.setUpdateTime(new Date());

        // 5. 将新密码与更新时间信息更新至用户表中
        this.userMapper.updatePasswordById(id, passwordUser);

        return new CommonResponse(CommonCode.SUCCESS);
    }

    @Override
    public CommonResponse updateEmail(long id, EmailUser emailUser) {

        // 1. 参数验证
        if (emailUser == null) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        // 2. 新邮箱校验
        if (this.userMapper.selectOne(new QueryWrapper<User>().eq("email", emailUser.getEmail())) != null) {
            return new CommonResponse(UserCode.UPDATE_EMAIL_FAIL_EMAIL_ALREADY_EXISTS);
        }

        // 3. 验证码检验
        if (!StringUtils.equals(emailUser.getCode(), this.redisTemplate.opsForValue().get(CHANGE_KEY_PREFIX + emailUser.getEmail()))) {
            return new CommonResponse(UserCode.UPDATE_EMAIL_FAIL_CODE_WRONG);
        }

        // 0. 更新最后一次修改个人信息的时间
        emailUser.setUpdateTime(new Date());

        // 0. 更新邮箱信息
        this.userMapper.updateEmail(id, emailUser);

        return new CommonResponse(CommonCode.SUCCESS);
    }
}
