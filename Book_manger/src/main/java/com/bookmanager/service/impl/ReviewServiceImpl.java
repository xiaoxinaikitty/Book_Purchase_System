package com.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookmanager.entity.Book;
import com.bookmanager.entity.Review;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.mapper.ReviewMapper;
import com.bookmanager.service.BookService;
import com.bookmanager.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 图书评价服务实现类
 */
@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    @Autowired
    private BookService bookService;

    @Override
    public boolean addReview(Review review) {
        if (review.getUserId() == null || review.getBookId() == null) {
            throw new BusinessException("评价参数不完整");
        }
        Book book = bookService.getById(review.getBookId());
        if (book == null || book.getStatus() == null || book.getStatus() != 1) {
            throw new BusinessException("图书不存在或已下架");
        }

        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getUserId, review.getUserId())
                .eq(Review::getBookId, review.getBookId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("您已评价过该图书");
        }

        boolean saved = this.save(review);
        if (saved) {
            updateBookRating(review.getBookId());
        }
        return saved;
    }

    @Override
    public IPage<Review> getBookReviews(Long bookId, Integer page, Integer size) {
        Page<Review> pageParam = new Page<>(page, size);
        return this.baseMapper.selectReviewPage(pageParam, bookId);
    }

    @Override
    public IPage<Review> getUserReviews(Long userId, Integer page, Integer size) {
        Page<Review> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getUserId, userId).orderByDesc(Review::getCreateTime);
        return this.page(pageParam, wrapper);
    }

    @Override
    public boolean deleteReview(Long id) {
        Review review = this.getById(id);
        if (review == null) {
            return false;
        }
        boolean removed = this.removeById(id);
        if (removed) {
            updateBookRating(review.getBookId());
        }
        return removed;
    }

    @Override
    public Double getAverageRating(Long bookId) {
        List<Review> list = this.lambdaQuery().eq(Review::getBookId, bookId).list();
        if (list.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (Review review : list) {
            if (review.getRating() != null) {
                sum += review.getRating();
            }
        }
        return sum / list.size();
    }

    private void updateBookRating(Long bookId) {
        Double avg = getAverageRating(bookId);
        BigDecimal rating = BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP);
        Book update = new Book();
        update.setId(bookId);
        update.setRating(rating);
        bookService.updateById(update);
    }
}

