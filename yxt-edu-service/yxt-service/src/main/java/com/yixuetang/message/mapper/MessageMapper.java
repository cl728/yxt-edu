package com.yixuetang.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yixuetang.entity.message.Message;
import com.yixuetang.entity.request.message.QueryPageRequestMessage;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/11/23 23:42
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    @Results(id = "messageMap", value = {
            @Result(column = "publisher_id", property = "publisher",
                    one = @One(select = "com.yixuetang.user.mapper.UserMapper.findPublisherById", fetchType = FetchType.EAGER))
    })
    @Select("<script>" +
            "select id, receiver_type, publisher_id, receiver_id, status, title, publish_time, update_time " +
            "from t_message " +
            "<where>" +
            "<if test='request.type != null and request.type != -1'> and receiver_type = #{request.type}</if>" +
            "<if test='request.messageStatus != null'> and status = #{request.messageStatus}</if>" +
            "<if test='request.title != null and request.title.length() > 0'> and title like #{request.title}</if>" +
            "</where>" +
            "order by update_time desc" +
            "</script>")
    List<Message> findByPage(@Param("page") Page<Message> page,
                             @Param("request") QueryPageRequestMessage request);
}
