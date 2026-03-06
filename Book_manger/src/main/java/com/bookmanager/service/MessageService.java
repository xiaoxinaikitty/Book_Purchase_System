package com.bookmanager.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bookmanager.entity.Message;

/**
 * 站内消息服务接口
 */
public interface MessageService extends IService<Message> {

    IPage<Message> getInboxPage(Long userId, Integer page, Integer size, Integer status);

    IPage<Message> getSentPage(Long userId, Integer page, Integer size);

    IPage<Message> getAdminSentPage(Long adminId, Integer page, Integer size, String keyword);

    boolean sendMessage(Long senderId, Long receiverId, String title, String content);

    int sendMessageToAll(Long senderId, String title, String content);

    boolean markRead(Long userId, Long messageId);

    Long getUnreadCount(Long userId);

    boolean deleteForUser(Long userId, Long messageId);
}
