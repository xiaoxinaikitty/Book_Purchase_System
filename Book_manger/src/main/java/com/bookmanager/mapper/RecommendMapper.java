package com.bookmanager.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 推荐行为数据Mapper
 */
@Mapper
public interface RecommendMapper {

    @Select("SELECT user_id, book_id, rating FROM review")
    List<Map<String, Object>> selectReviewScores();

    @Select("SELECT user_id, book_id FROM favorite")
    List<Map<String, Object>> selectFavorites();

    @Select("SELECT user_id, book_id, browse_count FROM browse_history")
    List<Map<String, Object>> selectBrowseHistory();

    @Select("SELECT o.user_id, oi.book_id FROM order_item oi " +
            "INNER JOIN `order` o ON oi.order_id = o.id " +
            "WHERE o.status IN (1, 2, 3)")
    List<Map<String, Object>> selectPurchases();
}
