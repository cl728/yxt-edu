package com.yixuetang.message.service;

import com.yixuetang.entity.message.Message;
import com.yixuetang.entity.message.UserMessageSetting;
import com.yixuetang.entity.request.message.QueryPageRequestMessage;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;

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

    /**
     * 查询某个用户的消息提醒设置
     *
     * @param userId 用户id
     * @return 响应结果实体类
     */
    QueryResponse findMessageSettingByUserId(Long userId);

    /**
     * 查询某个用户收到的通知
     *
     * @param userId 用户id
     * @return 响应结果实体类
     */
    QueryResponse findMessageListByUserId(Long userId);

    /**
     * 分页查询系统通知
     *
     * @param currentPage             当前页码数
     * @param pageSize                每页显示条数
     * @param queryPageRequestMessage 分页查询条件实体类
     * @return 响应结果实体类
     */
    QueryResponse findMessageListByPage(long currentPage, long pageSize, QueryPageRequestMessage queryPageRequestMessage);

    /**
     * 查询某个通知的详情
     *
     * @param messageId 通知id
     * @return 响应结果实体类
     */
    QueryResponse findMessageById(long messageId);

    /**
     * 管理员发布系统通知
     *
     * @param adminId 发布通知的管理员id
     * @param message 通知实体类
     * @return 响应结果实体类
     */
    CommonResponse inputMessage(long adminId, Message message);

    /**
     * 管理员修改系统通知
     *
     * @param adminId   修改通知的管理员id
     * @param messageId 被修改的通知id
     * @param message   通知实体类
     * @return 响应结果实体类
     */
    CommonResponse editMessage(long adminId, long messageId, Message message);

    /**
     * 查询用户未读的系统通知或事件提醒数量
     *
     * @param userId 用户id
     * @return 响应结果实体类
     */
    QueryResponse findUnreadCountByUserId(long userId);

    /**
     * 管理员删除系统通知
     *
     * @param messageId 被删除的通知id
     * @return 相应结果实体类
     */
    CommonResponse deleteMessageById(Long messageId);
}
