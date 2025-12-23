package com.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookmanager.entity.BrowseHistory;
import com.bookmanager.mapper.BrowseHistoryMapper;
import com.bookmanager.service.BrowseHistoryService;
import org.springframework.stereotype.Service;

/**
 * 浏览记录服务实现类
 */
@Service
public class BrowseHistoryServiceImpl extends ServiceImpl<BrowseHistoryMapper, BrowseHistory> implements BrowseHistoryService {

    @Override
    public void recordBrowse(Long userId, Long bookId) {
        // TODO: 实现记录浏览历史
    }

    @Override
    public IPage<BrowseHistory> getBrowseHistory(Long userId, Integer page, Integer size) {
        // TODO: 实现获取用户浏览记录
        return null;
    }

    @Override
    public boolean clearHistory(Long userId) {
        // TODO: 实现清空用户浏览记录
        return false;
    }

    @Override
    public boolean deleteHistory(Long id) {
        // TODO: 实现删除单条浏览记录
        return false;
    }
}

