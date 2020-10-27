package com.yixuetang.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuetang.entity.request.user.UpdateUser;
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
    @Select("select * from t_user")
    List<User> findAll();

    /**
     * 根据用户名更改角色id
     * @param username 用户名
     * @param roleId 角色id
     */
    @Update("update t_user set role_id = #{roleId} where username = #{username}")
    void updateRoleIdByUsername(@Param("username") String username, @Param("roleId") Long roleId);

    /**
     * 更新用户信息
     * @param updateUser 更新用户实体类
     */
    @Update("update t_user set real_name = #{realName}, gender = #{gender}, age = #{age}, phone = #{phone}, school = #{school}, ts_no = #{tsNo}, update_time = #{updateTime} where id = #{id}")
    void updateUser(UpdateUser updateUser);

    /**
     * 根据id修改roleId
     * @param roleId 角色id
     * @param id 用户主键id
     */
    @Update("update t_user set role_id = #{roleId} where id = #{id}")
    void UpdateRoleIdById(@Param("roleId") Long roleId, @Param("id") Long id);
}
