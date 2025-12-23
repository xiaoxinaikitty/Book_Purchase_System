package com.bookmanager.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bookmanager.entity.Favorite;

/**
 * 收藏服务接口
 */
public interface FavoriteService extends IService<Favorite> {
    
    /**
     * 添加收藏
     */
    boolean addFavorite(Long userId, Long bookId);
    
    /**
     * 取消收藏
     */
    boolean removeFavorite(Long userId, Long bookId);
    
    /**
     * 获取用户收藏列表（分页）
     */
    IPage<Favorite> getFavoriteList(Long userId, Integer page, Integer size);
    
    /**
     * 检查是否已收藏
     */
    boolean isFavorite(Long userId, Long bookId);
    
    /**
     * 获取用户收藏数量
     */
    Integer getFavoriteCount(Long userId);
}

