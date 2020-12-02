package com.yixuetang.entity.message;

/**
 * @author Colin
 * @version 1.0.0
 * @description 私信模块常量值
 * @date 2020/12/1 21:00
 */
public interface CommonConstant {

    /**
     * 推送至指定用户消息
     * 推送方 Session Key 前缀标识
     */
    String CHAT_FROM_PREFIX = "CHAT_FROM_";

    /**
     * 推送至指定用户消息
     * 接收方 Session Key 前缀标识
     */
    String CHAT_TO_PREFIX = "_TO_";

    /**
     * RedisTemplate 根据 Key 模糊匹配查询前缀
     */
    String REDIS_MATCH_PREFIX = "*";

}