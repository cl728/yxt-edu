package com.yixuetang.resource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuetang.entity.resource.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author Colin
 * @version 1.0.0
 * @description 资源持久层接口
 * @date 2020/11/17 16:47
 */
@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {

    @Update("update t_resource set user_id = #{userId} where id = #{id}")
    void updateUserIdById(@Param("userId") Long userId, @Param("id") Long id);

    @Update("update t_resource set parent_resource_id = #{patentResourceId} where id = #{id}")
    void updateParentResourceIdById(@Param("parentResourceId") Long parentResourceId, @Param("id") Long id);
}
