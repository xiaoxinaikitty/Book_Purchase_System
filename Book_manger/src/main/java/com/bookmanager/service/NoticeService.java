package com.bookmanager.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bookmanager.entity.Notice;

/**
 * 公告服务接口
 */
public interface NoticeService extends IService<Notice> {

    IPage<Notice> getAdminNoticePage(Integer page, Integer size, String keyword, Integer status);

    IPage<Notice> getUserNoticePage(Long userId, Integer page, Integer size, String keyword);

    boolean markRead(Long userId, Long noticeId);

    Long getUnreadCount(Long userId);
}
