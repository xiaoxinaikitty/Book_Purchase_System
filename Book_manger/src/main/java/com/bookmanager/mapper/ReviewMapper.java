package com.bookmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmanager.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 评价Mapper接口
 */
@Mapper
public interface ReviewMapper extends BaseMapper<Review> {
    
    /**
     * 分页查询图书评价（包含用户信息）
     */
    IPage<Review> selectReviewPage(Page<Review> page, @Param("bookId") Long bookId);
    
    /**
     * 获取用户评分矩阵（用于KNN推荐）
     */
    @Select("SELECT user_id, book_id, rating FROM review")
    List<Map<String, Object>> selectUserRatingMatrix();
}

