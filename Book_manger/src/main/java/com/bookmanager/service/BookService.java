package com.bookmanager.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bookmanager.entity.Book;

import java.util.List;

/**
 * 图书服务接口
 */
public interface BookService extends IService<Book> {
    
    /**
     * 分页查询图书
     */
    IPage<Book> getBookPage(Integer page, Integer size, Long categoryId, String keyword);
    
    /**
     * 获取图书详情
     */
    Book getBookDetail(Long id);
    
    /**
     * 获取热门图书
     */
    List<Book> getHotBooks(Integer limit);
    
    /**
     * 获取新书推荐
     */
    List<Book> getNewBooks(Integer limit);
    
    /**
     * 添加图书
     */
    boolean addBook(Book book);
    
    /**
     * 更新图书
     */
    boolean updateBook(Book book);
    
    /**
     * 删除图书
     */
    boolean deleteBook(Long id);
    
    /**
     * 更新图书库存
     */
    boolean updateStock(Long bookId, Integer quantity);
    
    /**
     * 增加销量
     */
    boolean increaseSales(Long bookId, Integer quantity);
}

