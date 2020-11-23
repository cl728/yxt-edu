package com.yixuetang.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuetang.entity.message.UserMessageSetting;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Colin
 * @version 1.0.0
 * @description 消息模块持久层接口
 * @date 2020/11/23 10:45
 */
@Mapper
public interface UserMessageSettingMapper extends BaseMapper<UserMessageSetting> {
}
