package com.bookmanager.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bookmanager.entity.BrowseHistory;

/**
 * 浏览记录服务接口
 */
public interface BrowseHistoryService extends IService<BrowseHistory> {
    
    /**
     * 记录浏览历史
     */
    void recordBrowse(Long userId, Long bookId);
    
    /**
     * 获取用户浏览记录（分页）
     */
    IPage<BrowseHistory> getBrowseHistory(Long userId, Integer page, Integer size);
    
    /**
     * 清空用户浏览记录
     */
    boolean clearHistory(Long userId);
    
    /**
     * 删除单条浏览记录
     */
    boolean deleteHistory(Long id);
}

