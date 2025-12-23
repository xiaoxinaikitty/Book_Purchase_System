package com.bookmanager.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bookmanager.entity.Review;

/**
 * 图书评价服务接口
 */
public interface ReviewService extends IService<Review> {
    
    /**
     * 添加评价
     */
    boolean addReview(Review review);
    
    /**
     * 获取图书的评价列表（分页）
     */
    IPage<Review> getBookReviews(Long bookId, Integer page, Integer size);
    
    /**
     * 获取用户的评价列表
     */
    IPage<Review> getUserReviews(Long userId, Integer page, Integer size);
    
    /**
     * 删除评价
     */
    boolean deleteReview(Long id);
    
    /**
     * 计算图书的平均评分
     */
    Double getAverageRating(Long bookId);
}

