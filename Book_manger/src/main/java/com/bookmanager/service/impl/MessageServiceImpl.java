package com.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookmanager.entity.Message;
import com.bookmanager.entity.User;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.mapper.MessageMapper;
import com.bookmanager.mapper.UserMapper;
import com.bookmanager.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 站内消息服务实现类
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public IPage<Message> getInboxPage(Long userId, Integer page, Integer size, Integer status) {
        Page<Message> pageParam = new Page<>(page, size);
        if (userId == null) {
            return pageParam;
        }
        return this.baseMapper.selectInboxPage(pageParam, userId, status);
    }

    @Override
    public IPage<Message> getSentPage(Long userId, Integer page, Integer size) {
        Page<Message> pageParam = new Page<>(page, size);
        if (userId == null) {
            return pageParam;
        }
        return this.baseMapper.selectSentPage(pageParam, userId);
    }

    @Override
    public IPage<Message> getAdminSentPage(Long adminId, Integer page, Integer size, String keyword) {
        Page<Message> pageParam = new Page<>(page, size);
        if (adminId == null) {
            return pageParam;
        }
        return this.baseMapper.selectAdminSentPage(pageParam, adminId, keyword);
    }

    @Override
    public boolean sendMessage(Long senderId, Long receiverId, String title, String content) {
        if (senderId == null || receiverId == null) {
            throw new BusinessException("发送参数不完整");
        }
        if (!StringUtils.hasText(title) || !StringUtils.hasText(content)) {
            throw new BusinessException("标题和内容不能为空");
        }
        User receiver = userMapper.selectById(receiverId);
        if (receiver == null) {
            throw new BusinessException("接收用户不存在");
        }
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setTitle(title.trim());
        message.setContent(content.trim());
        message.setStatus(0);
        message.setDeleteBySender(0);
        message.setDeleteByReceiver(0);
        return this.save(message);
    }

    @Override
    public int sendMessageToAll(Long senderId, String title, String content) {
        if (senderId == null) {
            throw new BusinessException("发送者不能为空");
        }
        if (!StringUtils.hasText(title) || !StringUtils.hasText(content)) {
            throw new BusinessException("标题和内容不能为空");
        }
        List<Long> userIds = userMapper.selectUserIdsByRole(0);
        if (userIds == null || userIds.isEmpty()) {
            return 0;
        }
        List<Message> list = new ArrayList<>();
        for (Long userId : userIds) {
            Message message = new Message();
            message.setSenderId(senderId);
            message.setReceiverId(userId);
            message.setTitle(title.trim());
            message.setContent(content.trim());
            message.setStatus(0);
            message.setDeleteBySender(0);
            message.setDeleteByReceiver(0);
            list.add(message);
        }
        this.saveBatch(list);
        return list.size();
    }

    @Override
    public boolean markRead(Long userId, Long messageId) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Message message = this.getById(messageId);
        if (message == null) {
            throw new BusinessException("消息不存在");
        }
        if (!userId.equals(message.getReceiverId())) {
            throw new BusinessException(403, "无权限操作");
        }
        if (message.getStatus() != null && message.getStatus() == 1) {
            return true;
        }
        Message update = new Message();
        update.setId(messageId);
        update.setStatus(1);
        update.setReadTime(LocalDateTime.now());
        return this.updateById(update);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        if (userId == null) {
            return 0L;
        }
        Long count = this.baseMapper.countUnread(userId);
        return count == null ? 0L : count;
    }

    @Override
    public boolean deleteForUser(Long userId, Long messageId) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Message message = this.getById(messageId);
        if (message == null) {
            throw new BusinessException("消息不存在");
        }
        boolean isSender = userId.equals(message.getSenderId());
        boolean isReceiver = userId.equals(message.getReceiverId());
        if (!isSender && !isReceiver) {
            throw new BusinessException(403, "无权限操作");
        }
        Message update = new Message();
        update.setId(messageId);
        if (isSender) {
            update.setDeleteBySender(1);
        }
        if (isReceiver) {
            update.setDeleteByReceiver(1);
        }
        boolean updated = this.updateById(update);
        if (!updated) {
            return false;
        }
        Message latest = this.getById(messageId);
        if (latest != null
                && Integer.valueOf(1).equals(latest.getDeleteBySender())
                && Integer.valueOf(1).equals(latest.getDeleteByReceiver())) {
            this.removeById(messageId);
        }
        return true;
    }
}
