package com.yixuetang.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yixuetang.comment.mapper.CommentMapper;
import com.yixuetang.entity.comment.Comment;
import com.yixuetang.entity.homework.Homework;
import com.yixuetang.entity.message.EventRemind;
import com.yixuetang.entity.message.Message;
import com.yixuetang.entity.message.UserMessage;
import com.yixuetang.entity.message.UserMessageSetting;
import com.yixuetang.entity.notice.Notice;
import com.yixuetang.entity.request.message.QueryPageRequestMessage;
import com.yixuetang.entity.resource.Resource;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.code.message.MessageCode;
import com.yixuetang.entity.response.code.remind.RemindCode;
import com.yixuetang.entity.response.result.EventRemindResp;
import com.yixuetang.entity.response.result.QueryResult;
import com.yixuetang.entity.response.result.UnreadCountResp;
import com.yixuetang.entity.user.User;
import com.yixuetang.homework.mapper.HomeworkMapper;
import com.yixuetang.message.mapper.MessageMapper;
import com.yixuetang.message.mapper.RemindMapper;
import com.yixuetang.message.mapper.UserMessageMapper;
import com.yixuetang.message.mapper.UserMessageSettingMapper;
import com.yixuetang.message.service.MessageService;
import com.yixuetang.notice.mapper.NoticeMapper;
import com.yixuetang.resource.mapper.ResourceMapper;
import com.yixuetang.user.mapper.UserMapper;
import com.yixuetang.utils.exception.ExceptionThrowUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Colin
 * @version 1.0.0
 * @description 消息模块服务层接口实现类
 * @date 2020/11/23 10:42
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserMessageSettingMapper messageSettingMapper;

    @Autowired
    private UserMessageMapper userMessageMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private RemindMapper remindMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private HomeworkMapper homeworkMapper;

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Override
    @Transactional
    public CommonResponse updateMessageSetting(Long userId, UserMessageSetting setting) {

        if (this.userMapper.selectOne(
                new QueryWrapper<User>().eq("id", userId).select("id")) == null
                || ObjectUtils.isEmpty(setting)) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        Long id = this.messageSettingMapper.selectOne(
                new QueryWrapper<UserMessageSetting>().eq("user_id", userId).select("id")).getId();

        this.messageSettingMapper.updateById(UserMessageSetting.builder()
                .id(id).userId(userId).allRemind(setting.getAllRemind())
                .course(setting.getCourse()).reply(setting.getReply())
                .voteUp(setting.getVoteUp()).chat(setting.getChat()).build());

        return CommonResponse.SUCCESS();

    }

    @Override
    @Transactional
    public QueryResponse findMessageSettingByUserId(Long userId) {

        if (this.userMapper.selectOne(
                new QueryWrapper<User>().eq("id", userId).select("id")) == null) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        UserMessageSetting userMessageSetting = this.messageSettingMapper.selectOne(
                new QueryWrapper<UserMessageSetting>().eq("user_id", userId));

        // 如果用户第一次查询消息提醒设置，则默认全为 true
        // 并且将该记录插入到用户消息提醒表里
        UserMessageSetting setting = UserMessageSetting.builder().userId(userId)
                .allRemind(true).course(true).reply(true).voteUp(true).chat(true).build();

        if (ObjectUtils.isEmpty(userMessageSetting)) {    // 消息提醒表里没有相关记录
            this.messageSettingMapper.insert(setting);
            return new QueryResponse(CommonCode.SUCCESS,
                    new QueryResult<>(Collections.singletonList(setting), 1));
        }

        return new QueryResponse(CommonCode.SUCCESS,
                new QueryResult<>(Collections.singletonList(userMessageSetting), 1));
    }

    @Override
    public QueryResponse findMessageListByUserId(Long userId) {
        if (!ObjectUtils.allNotNull(userId,
                this.userMapper.selectOne(new QueryWrapper<User>().eq("id", userId)))) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        List<Message> messageList = new ArrayList<>();
        this.userMessageMapper
                .selectList(new QueryWrapper<UserMessage>().eq("receiver_id", userId))
                .stream()
                .map(UserMessage::getMessageId)
                .collect(Collectors.toList())
                .forEach(messageId -> {
                    messageList.add(this.messageMapper.selectById(messageId));
                    // 查询后，将未读的通知列表状态设置为已读
                    UserMessage userMessage = this.userMessageMapper.selectOne(
                            new QueryWrapper<UserMessage>().eq("message_id", messageId).eq("receiver_id", userId));
                    if (!userMessage.getStatus()) {
                        userMessage.setStatus(Boolean.TRUE);
                        this.userMessageMapper.updateById(userMessage);
                    }
                });

        return new QueryResponse(CommonCode.SUCCESS, new QueryResult<>(messageList, messageList.size()));
    }

    @Override
    public QueryResponse findMessageListByPage(long currentPage, long pageSize, QueryPageRequestMessage queryPageRequestMessage) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();

        this.screen(queryPageRequestMessage, queryWrapper);

        List<Message> messageList = this.messageMapper.findByPage(new Page<>(currentPage, pageSize), queryPageRequestMessage);

        // 如果接收者 id 不为 0， 此通知是发送给单一用户
        // 需要将其用户名查出
        messageList.forEach(message -> {
            if (message.getReceiverId() != 0) {
                message.setReceiver(
                        this.userMapper.selectOne(new QueryWrapper<User>().eq("id", message.getReceiverId())
                                .select("id", "username")));
            }
        });

        return new QueryResponse(CommonCode.SUCCESS, new QueryResult<>(messageList, this.messageMapper.selectCount(queryWrapper)));
    }

    @Override
    public QueryResponse findMessageById(long messageId) {
        return new QueryResponse(CommonCode.SUCCESS,
                new QueryResult<>(Collections.singletonList(
                        this.messageMapper.selectById(messageId)), 1));
    }

    @Transactional
    @Override
    public CommonResponse inputMessage(long adminId, Message message) {

        if (ObjectUtils.isEmpty(message)) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        // 判断通知的接收者类型
        int receiverType = message.getReceiverType();

        Message insertMessage = Message.builder().id(null).publisherId(adminId)
                .title(message.getTitle()).content(message.getContent()).receiverType(receiverType)
                .status(Boolean.FALSE).publishTime(new Date()).updateTime(new Date())
                .receiverId(receiverType == 0 ? 0L : message.getReceiverId()).build();

        this.messageMapper.insert(insertMessage);

        return CommonResponse.SUCCESS();

    }

    @Transactional
    @Override
    public CommonResponse editMessage(long adminId, long messageId, Message message) {

        if (ObjectUtils.isEmpty(message)) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        Message updateMessage = this.messageMapper.selectById(messageId);

        updateMessage.setUpdateTime(new Date());

        this.messageMapper.update(updateMessage,
                new UpdateWrapper<Message>()
                        .set(StringUtils.isNoneBlank(message.getTitle()), "title", message.getTitle())
                        .set(StringUtils.isNoneBlank(message.getContent()), "content", message.getContent())
                        .eq("id", messageId));

        return CommonResponse.SUCCESS();
    }

    @Transactional
    @Override
    public CommonResponse deleteMessageById(Long messageId) {

        // 1. 根据该messageId查询该message是否存在
        Message message = this.messageMapper.selectById(messageId);
        if (ObjectUtils.isEmpty(message)) {
            //查询不到此消息
            return new CommonResponse(MessageCode.MESSAGE_NOT_FOUND);
        }

        // 2. 判断t_user_message中是否存在message_id
        List<UserMessage> userMessages = this.userMessageMapper.selectList(new QueryWrapper<UserMessage>().eq("message_id", messageId));
        if (!CollectionUtils.isEmpty(userMessages)) {
            //存在则先删除t_user_message中记录
            this.userMessageMapper.delete(new QueryWrapper<UserMessage>().eq("message_id", messageId));
        }

        // 3. 删除messageId对应的消息
        this.messageMapper.deleteById(messageId);

        return CommonResponse.SUCCESS();
    }

    @Override
    public QueryResponse findEventRemindListByUserId(long userId) {

        if (ObjectUtils.isEmpty(this.userMapper.selectById(userId))) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        List<EventRemindResp> eventRemindRespList = new ArrayList<>();

        this.remindMapper.selectList(new QueryWrapper<EventRemind>()
                .eq("receiver_id", userId)
                .orderByDesc("remind_time"))
                .forEach(eventRemind -> {
                    EventRemindResp eventRemindResp = EventRemindResp.builder()
                            .id(eventRemind.getId())
                            .remindType(eventRemind.getRemindType())
                            .sender(this.userMapper.selectById(eventRemind.getSenderId()))
                            .action(eventRemind.getAction())
                            .sourceName(eventRemind.getSourceName())
                            .sourceContent(this.getSourceContent(eventRemind.getRemindType(), eventRemind.getSourceId(), eventRemind.getSourceName()))
                            .targetContent(this.getTargetContent(eventRemind.getRemindType(), eventRemind.getTargetId(), eventRemind.getSourceName()))
                            .url(eventRemind.getUrl())
                            .remindTime(eventRemind.getRemindTime())
                            .build();
                    eventRemindRespList.add(eventRemindResp);
                    // 将事件提醒设置为已读状态
                    eventRemind.setStatus(Boolean.TRUE);
                    this.remindMapper.updateById(eventRemind);
                });

        return new QueryResponse(CommonCode.SUCCESS,
                new QueryResult<>(eventRemindRespList, eventRemindRespList.size()));
    }

    @Transactional
    @Override
    public CommonResponse deleteRemindById(Long remindId, Long userId) {

        // 1. 根据remindId查询判断该remind是否存在
        EventRemind eventRemind = this.remindMapper.findById(remindId);
        if (ObjectUtils.isEmpty(eventRemind)) {
            return new CommonResponse(RemindCode.REMIND_NOT_FOUND);
        }

        // 2. 根据userId查询判断该user是否为该remind的receiver接收者
        if (ObjectUtils.notEqual(userId, eventRemind.getReceiverId())) {
            return new CommonResponse(RemindCode.REMIND_NOT_BELONGS_TO_THIS_USER);
        }

        // 3. 根据事件提醒id删除某一事件提醒
        this.remindMapper.deleteById(remindId);

        return CommonResponse.SUCCESS();
    }

    @Override
    public QueryResponse findUnreadCountByUserId(long userId) {

        UserMessageSetting setting =
                this.messageSettingMapper.selectOne(
                        new QueryWrapper<UserMessageSetting>().eq("user_id", userId));

        UnreadCountResp.UnreadCountRespBuilder builder = UnreadCountResp.builder();

        // 查询未读系统通知（系统通知不允许不接收提醒）
        builder.systemMessageCount(this.userMessageMapper.selectCount(
                new QueryWrapper<UserMessage>()
                        .eq("receiver_id", userId)
                        .eq("status", Boolean.FALSE)));

        // 课程、回复、点赞、私信提醒根据用户的具体设置决定是否将未读条数查出
        if (!setting.getAllRemind()) {
            // 用户关闭了消息提醒
            builder.courseMessageCount(0).replyMessageCount(0).voteUpMessageCount(0).chatMessageCount(0);
        } else {
            // 用户未关闭消息提醒，根据具体情况查询
            builder.courseMessageCount(setting.getCourse() ? this.getEventRemindCount(userId, 0) : 0)
                    .replyMessageCount(setting.getReply() ? this.getEventRemindCount(userId, 1) : 0)
                    .voteUpMessageCount(setting.getVoteUp() ? this.getEventRemindCount(userId, 2) : 0)
                    .chatMessageCount(setting.getChat() ? this.getEventRemindCount(userId, 3) : 0);
        }

        return new QueryResponse(CommonCode.SUCCESS,
                new QueryResult<>(Collections.singletonList(builder.build()), 1));
    }

    private void screen(QueryPageRequestMessage queryPageRequestMessage, QueryWrapper<Message> queryWrapper) {
        String receiverType = queryPageRequestMessage.getReceiverType();
        if (StringUtils.isNoneBlank(receiverType)) {
            int type = StringUtils.equals("全部用户", receiverType) ? 0
                    : StringUtils.equals("单一用户", receiverType) ? 1 : -1;
            queryPageRequestMessage.setType(type);
            queryWrapper.eq("receiver_type", type);
        }

        String queryStatus = queryPageRequestMessage.getStatus();
        if (StringUtils.isNoneBlank(queryStatus)) {
            Boolean status = StringUtils.equals("已拉取", queryStatus) ? Boolean.TRUE
                    : StringUtils.equals("未拉取", queryStatus) ? Boolean.FALSE : null;
            queryPageRequestMessage.setMessageStatus(status);
            queryWrapper.eq("status", status);
        }

        String title = queryPageRequestMessage.getTitle();
        if (StringUtils.isNoneBlank(title)) {
            queryPageRequestMessage.setTitle("%" + title + "%");
            queryWrapper.like("title", queryPageRequestMessage.getTitle());
        }
    }

    private int getEventRemindCount(long userId, int remindType) {
        return this.remindMapper.selectCount(
                new QueryWrapper<EventRemind>()
                        .eq("receiver_id", userId)
                        .eq("status", Boolean.FALSE)
                        .eq("remind_type", remindType));
    }

    private String getSourceContent(Integer remindType, Long sourceId, String sourceName) {
        String sourceContent = null;
        switch (remindType) { // 根据提醒类型，设置不同的 sourceContent
            case 0: // 课程相关提醒
                switch (sourceName) {
                    case "作业":  // 发布作业，查询新作业的标题
                        Homework homework = this.homeworkMapper.selectById(sourceId);
                        sourceContent = homework == null ? "[该作业已被删除]" : homework.getTitle();
                        break;
                    case "资源":  // 发布资源，查询新资源的名称:
                        Resource resource = this.resourceMapper.selectById(sourceId);
                        sourceContent = resource == null ? "[该资源已被删除]" : resource.getName();
                        break;
                    case "公告":  // 发布公告，查询新公告的标题
                        Notice notice = this.noticeMapper.selectById(sourceId);
                        sourceContent = notice == null ? "[该公告已被删除]" : this.noticeMapper.selectById(sourceId).getTitle();
                        break;
                }
                break;
            case 1: // 回复相关提醒，查询回复的评论内容
                Comment comment = this.commentMapper.selectById(sourceId);
                sourceContent = comment == null ? "[该评论已被删除]" : comment.getContent();
                break;
            default: // 点赞相关提醒，不用查询 sourceContent
                break;
        }
        return sourceContent;
    }

    private String getTargetContent(Integer remindType, Long targetId, String sourceName) {
        String targetContent = null;
        Comment comment = this.commentMapper.selectById(targetId);
        String commentTargetContent = comment == null ? "[该评论已被删除]" : comment.getContent();
        switch (remindType) { // 根据提醒类型，设置不同的 targetContent，目前只需要处理回复和点赞这两种提醒类型
            case 1: // 回复相关提醒，查询被回复的评论内容
                targetContent = commentTargetContent;
                break;
            case 2: // 点赞相关提醒，查询被点赞的评论内容
                targetContent = commentTargetContent;
                break;
            default: // 课程相关提醒，不用查询 targetContent
                break;
        }
        return targetContent;
    }
}
