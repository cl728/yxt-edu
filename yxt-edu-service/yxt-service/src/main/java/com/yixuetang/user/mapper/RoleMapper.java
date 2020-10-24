package com.yixuetang.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuetang.entity.user.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author Colin
 * @version 1.0.0
 * @description 用户模块角色实体类持久层接口
 * @date 2020/10/23 14:55
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    @Select("select * from t_role where id = #{id}")
    Role findById(Long id);

}
