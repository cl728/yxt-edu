package com.yixuetang.resource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuetang.entity.resource.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

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

    @Update("update t_resource set parent_resource_id = #{parentResourceId} where id = #{id}")
    void updateParentResourceIdById(@Param("parentResourceId") Long parentResourceId, @Param("id") Long id);

    @Select("<script>" +
                "select id, type, ext, name, location, update_time from t_resource " +
                    "<where>" +
                        " and id in (select resource_id from t_cr where course_id = #{courseId})" +
                            "<if test='parentResourceId != -1'>" +
                                "and parent_resource_id = #{parentResourceId}" +
                            "</if>" +
                            "<if test='parentResourceId == -1'>" +
                                "and parent_resource_id is null" +
                            "</if>" +
                    "</where>" +
                "order by type desc, update_time desc" +
            "</script>")
    List<Resource> findByCourseIdAndParentId(@Param( "courseId" ) Long courseId, @Param( "parentResourceId" ) Long parentResourceId);
}
