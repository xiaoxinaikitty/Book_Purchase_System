package com.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookmanager.entity.Book;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.mapper.BookMapper;
import com.bookmanager.service.BookService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 图书服务实现类
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    @Override
    public IPage<Book> getBookPage(Integer page, Integer size, Long categoryId, String keyword) {
        Page<Book> pageParam = new Page<>(page, size);
        // 用户端仅查询上架图书
        return this.baseMapper.selectBookPage(pageParam, categoryId, keyword, 1);
    }

    @Override
    public IPage<Book> getBookPageAdmin(Integer page, Integer size, Long categoryId, String keyword, Integer status) {
        Page<Book> pageParam = new Page<>(page, size);
        return this.baseMapper.selectBookPage(pageParam, categoryId, keyword, status);
    }

    @Override
    public Book getBookDetail(Long id) {
        return this.baseMapper.selectBookById(id);
    }

    @Override
    public List<Book> getHotBooks(Integer limit) {
        int size = limit == null || limit <= 0 ? 10 : limit;
        return this.baseMapper.selectHotBooks(size);
    }

    @Override
    public List<Book> getNewBooks(Integer limit) {
        int size = limit == null || limit <= 0 ? 10 : limit;
        return this.lambdaQuery()
                .eq(Book::getStatus, 1)
                .orderByDesc(Book::getPublishDate)
                .orderByDesc(Book::getCreateTime)
                .last("LIMIT " + size)
                .list();
    }

    @Override
    public boolean addBook(Book book) {
        if (book.getStatus() == null) {
            book.setStatus(1);
        }
        if (book.getSales() == null) {
            book.setSales(0);
        }
        if (book.getRating() == null) {
            book.setRating(java.math.BigDecimal.ZERO);
        }
        if (book.getStock() == null) {
            book.setStock(0);
        }
        return this.save(book);
    }

    @Override
    public boolean updateBook(Book book) {
        if (book.getId() == null) {
            throw new BusinessException("图书ID不能为空");
        }
        return this.updateById(book);
    }

    @Override
    public boolean deleteBook(Long id) {
        return this.removeById(id);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        if (id == null || status == null) {
            throw new BusinessException("参数不能为空");
        }
        Book update = new Book();
        update.setId(id);
        update.setStatus(status);
        return this.updateById(update);
    }

    @Override
    public boolean updateStock(Long bookId, Integer quantity) {
        if (bookId == null || quantity == null || quantity <= 0) {
            throw new BusinessException("库存变更参数错误");
        }
        Book book = this.getById(bookId);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        int current = book.getStock() == null ? 0 : book.getStock();
        int newStock = current - quantity;
        if (newStock < 0) {
            throw new BusinessException("库存不足");
        }
        book.setStock(newStock);
        return this.updateById(book);
    }

    @Override
    public boolean increaseSales(Long bookId, Integer quantity) {
        if (bookId == null || quantity == null || quantity <= 0) {
            throw new BusinessException("销量变更参数错误");
        }
        Book book = this.getById(bookId);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        int current = book.getSales() == null ? 0 : book.getSales();
        book.setSales(current + quantity);
        return this.updateById(book);
    }
}

