package com.yixuetang.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuetang.entity.message.EventRemind;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Colin
 * @version 1.0.0
 * @description 事件提醒持久层接口
 * @date 2020/11/25 13:37
 */
@Mapper
public interface RemindMapper extends BaseMapper<EventRemind> {
}
