package com.yixuetang.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuetang.entity.message.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/11/23 23:42
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
