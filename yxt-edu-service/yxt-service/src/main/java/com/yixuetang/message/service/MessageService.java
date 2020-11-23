package com.yixuetang.message.service;

import com.yixuetang.entity.message.UserMessageSetting;
import com.yixuetang.entity.response.CommonResponse;

/**
 * @author Colin
 * @version 1.0.0
 * @description 消息模块服务层接口
 * @date 2020/11/23 10:41
 */
public interface MessageService {

    /**
     * 修改用户消息提醒设置
     *
     * @param userId  用户id
     * @param setting 消息提醒实体类
     * @return 响应结果实体类
     */
    CommonResponse updateMessageSetting(Long userId, UserMessageSetting setting);

}
