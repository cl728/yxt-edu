package com.yixuetang.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuetang.entity.request.user.EmailUser;
import com.yixuetang.entity.request.user.PasswordUser;
import com.yixuetang.entity.request.user.UpdateUser;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yixuetang.entity.user.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description 用户模块用户实体类持久层接口
 * @date 2020/10/23 14:50
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    @Results(id = "userMap", value = {
            @Result(column = "role_id", property = "role",
                    one = @One(select = "com.yixuetang.user.mapper.RoleMapper.findById", fetchType = FetchType.EAGER))
    })
    @Select("select id, username, email, phone, age, gender, role_id from t_user order by role_id")
    List<User> findByPage(Page<User> page);

    /**
     * 根据用户名和密码查询用户
     *
     * @param username 用户名
     * @param password 密码
     * @return 用户实体类
     */
    @ResultMap("userMap")
    @Select("select id, username, real_name, avatar, role_id from t_user where username = #{username} and password = #{password}")
    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    /**
     * 根据邮箱地址查询用户
     *
     * @param email 邮箱地址
     * @return 用户实体类
     */
    @ResultMap("userMap")
    @Select("select id, username, real_name, avatar,role_id from t_user where email = #{email}")
    User findByEmail(String email);

    /**
     * 根据用户名更改角色id
     *
     * @param username 用户名
     * @param roleId   角色id
     */
    @Update("update t_user set role_id = #{roleId} where username = #{username}")
    void updateRoleIdByUsername(@Param("username") String username, @Param("roleId") Long roleId);

    /**
     * 更新用户信息
     *
     * @param updateUser 更新用户实体类
     */
    @Update("update t_user set real_name = #{updateUser.realName}, gender = #{updateUser.gender}, age = #{updateUser.age}, phone = #{updateUser.phone}, school = #{updateUser.school}, ts_no = #{updateUser.tsNo}, update_time = #{updateUser.updateTime} where id = #{id}")
    void updateUser(@Param("id") Long id, @Param("updateUser") UpdateUser updateUser);

    /**
     * 根据id修改roleId
     *
     * @param roleId 角色id
     * @param id     用户主键id
     */
    @Update("update t_user set role_id = #{roleId} where id = #{id}")
    void UpdateRoleIdById(@Param("roleId") Long roleId, @Param("id") Long id);

    /**
     * 根据id修改密码与更新时间
     *
     * @param id           用户主键id
     * @param passwordUser 用户密码实体类
     */
    @Update("update t_user set password = #{passwordUser.newPassword}, update_time = #{passwordUser.updateTime} where id = #{id}")
    void updatePassworById(@Param("id") long id, @Param("passwordUser") PasswordUser passwordUser);

    /**
     * 根据id换绑邮箱
     *
     * @param id        用户主键id
     * @param emailUser 用户邮箱实体类
     */
    @Update("update t_user set email = #{emailUser.email} where id = #{id}")
    void updateEmail(@Param("id") long id, @Param("emailUser") EmailUser emailUser);
}
