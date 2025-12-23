package com.bookmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmanager.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 图书Mapper接口
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {
    
    /**
     * 分页查询图书（包含分类名称）
     */
    IPage<Book> selectBookPage(Page<Book> page, @Param("categoryId") Long categoryId, @Param("keyword") String keyword);
    
    /**
     * 根据ID查询图书详情（包含分类名称）
     */
    Book selectBookById(@Param("id") Long id);
    
    /**
     * 查询热门图书
     */
    @Select("SELECT * FROM book WHERE status = 1 ORDER BY sales DESC LIMIT #{limit}")
    List<Book> selectHotBooks(@Param("limit") Integer limit);
    
    /**
     * 查询用户购买过的图书ID列表
     */
    @Select("SELECT DISTINCT oi.book_id FROM order_item oi " +
            "INNER JOIN `order` o ON oi.order_id = o.id " +
            "WHERE o.user_id = #{userId} AND o.status IN (1, 2, 3)")
    List<Long> selectUserPurchasedBookIds(@Param("userId") Long userId);
}

