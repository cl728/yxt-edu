package com.yixuetang.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yixuetang.entity.request.user.QueryPageRequestUser;
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
     * 根据用户id查询一条用户信息
     *
     * @param id 用户id
     * @return 一条用户信息的用户实体类
     */
    @ResultMap("userMap")
    @Select("select * from t_user where id = #{id}")
    User findById(long id);

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    @Results(id = "userMap", value = {
            @Result(column = "role_id", property = "role",
                    one = @One(select = "com.yixuetang.user.mapper.RoleMapper.findById", fetchType = FetchType.EAGER))
    })
    @Select("<script>" +
            "select id, username, email, phone, age, gender, role_id " +
            " from t_user <where>" +
            "<if test='request.gender != null and request.gender.length() == 1'> and gender = #{request.gender}</if>" +
            "<if test='request.roleId != null'> and role_id = #{request.roleId}</if>" +
            "<if test='request.username != null and request.username.length() > 0'> and username like #{request.username}</if>" +
            "<if test='request.realName != null and request.realName.length() > 0'> and real_name like #{request.realName}</if>" +
            "<if test='request.email != null and request.email.length() > 0'> and email like #{request.email}</if>" +
            "<if test='request.phone != null and request.phone.length() > 0'> and phone like #{request.phone}</if>" +
            "</where>" +
            " order by role_id" +
            "</script>")
    List<User> findByPage(@Param("page") Page<User> page, @Param("request") QueryPageRequestUser request);

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
     * 根据手机号码查询用户
     *
     * @param phone 手机号码
     * @return 用户实体类
     */
    @ResultMap("userMap")
    @Select("select id, username, real_name, avatar, role_id from t_user where phone = #{phone}")
    User findByPhone(String phone);

    @Select("select u.avatar, u.real_name from t_user u, t_course c where c.id = #{courseId} and u.id = c.teacher_id")
    User findByCourseId(long courseId);

    @ResultMap( "userMap" )
    @Select( "<script>" +
                "select id, role_id, username, avatar, ts_no, real_name " +
                " from t_user " +
                "<where>" +
                    "<if test='ids != null and ids.size() > 0'> " +
                        "<foreach collection='ids' open='and id in (' close=')' separator=', ' item='id'>" +
                            "#{id}" +
                        "</foreach>" +
                    "</if>" +
                    "<if test='search != null and search.length() > 0'> " +
                        "and real_name like #{search} or ts_no like #{search}" +
                    "</if>" +
                "</where>" +
            "</script>" )
    List<User> findPageByIds(@Param("page") Page<User> page, @Param("ids") List<Long> ids, @Param( "search" ) String search);

    @Select( "<script>" +
                "select id, ts_no, real_name from t_user " +
                    "<where>" +
                        " and id in (select student_id from t_hs where homework_id = #{homeworkId})" +
                        "<if test='search != null and search.length() > 0'>" +
                            "and real_name like #{search} or ts_no like #{search}" +
                        "</if>" +
                    "</where>" +
            "</script>" )
    List<User> findPageByHomeworkId(@Param("page") Page<User> page,
                                    @Param("homeworkId") long homeworkId,
                                    @Param( "search" ) String search);
}
