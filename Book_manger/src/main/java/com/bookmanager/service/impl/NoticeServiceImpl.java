package com.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookmanager.entity.Notice;
import com.bookmanager.entity.NoticeRead;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.mapper.NoticeMapper;
import com.bookmanager.mapper.NoticeReadMapper;
import com.bookmanager.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 公告服务实现类
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Autowired
    private NoticeReadMapper noticeReadMapper;

    @Override
    public IPage<Notice> getAdminNoticePage(Integer page, Integer size, String keyword, Integer status) {
        Page<Notice> pageParam = new Page<>(page, size);
        return this.baseMapper.selectAdminNoticePage(pageParam, keyword, status);
    }

    @Override
    public IPage<Notice> getUserNoticePage(Long userId, Integer page, Integer size, String keyword) {
        Page<Notice> pageParam = new Page<>(page, size);
        if (userId == null) {
            return pageParam;
        }
        return this.baseMapper.selectUserNoticePage(pageParam, userId, keyword);
    }

    @Override
    public boolean markRead(Long userId, Long noticeId) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Notice notice = this.getById(noticeId);
        if (notice == null || notice.getStatus() == null || notice.getStatus() != 1) {
            throw new BusinessException("公告不存在或已下线");
        }
        LambdaQueryWrapper<NoticeRead> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NoticeRead::getNoticeId, noticeId)
                .eq(NoticeRead::getUserId, userId);
        if (noticeReadMapper.selectCount(wrapper) > 0) {
            return true;
        }
        NoticeRead read = new NoticeRead();
        read.setNoticeId(noticeId);
        read.setUserId(userId);
        return noticeReadMapper.insert(read) > 0;
    }

    @Override
    public Long getUnreadCount(Long userId) {
        if (userId == null) {
            return 0L;
        }
        Long count = this.baseMapper.countUnread(userId);
        return count == null ? 0L : count;
    }
}
