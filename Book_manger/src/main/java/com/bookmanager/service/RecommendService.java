package com.bookmanager.service;

import com.bookmanager.entity.Book;

import java.util.List;

/**
 * 推荐服务接口（KNN算法）
 */
public interface RecommendService {
    
    /**
     * 基于KNN的个性化推荐
     * @param userId 用户ID
     * @param k 相似用户数量
     * @param limit 推荐数量
     * @return 推荐图书列表
     */
    List<Book> personalRecommend(Long userId, int k, int limit);
    
    /**
     * 热门图书推荐
     * @param limit 推荐数量
     * @return 热门图书列表
     */
    List<Book> hotRecommend(int limit);
    
    /**
     * 相似图书推荐（基于当前图书）
     * @param bookId 当前图书ID
     * @param limit 推荐数量
     * @return 相似图书列表
     */
    List<Book> similarBookRecommend(Long bookId, int limit);
    
    /**
     * 猜你喜欢（综合推荐）
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐图书列表
     */
    List<Book> guessYouLike(Long userId, int limit);
    
    /**
     * 新书推荐
     * @param limit 推荐数量
     * @return 新书列表
     */
    List<Book> newBookRecommend(int limit);
}

