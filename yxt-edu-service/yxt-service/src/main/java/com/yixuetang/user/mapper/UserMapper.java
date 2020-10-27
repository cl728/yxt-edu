package com.yixuetang.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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

    @Update("update t_user set role_id = #{roleId} where username = #{username}")
    void updateRoleId(@Param("username") String username, @Param("roleId") Long roleId);
}
