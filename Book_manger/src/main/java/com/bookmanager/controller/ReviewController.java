package com.bookmanager.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bookmanager.common.PageResult;
import com.bookmanager.common.Result;
import com.bookmanager.dto.ReviewDTO;
import com.bookmanager.entity.Review;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.service.ReviewService;
import com.bookmanager.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 图书评价控制器
 */
@Api(tags = "图书评价")
@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @ApiOperation("添加评价")
    @PostMapping("/add")
    public Result<Void> addReview(@Valid @RequestBody ReviewDTO reviewDTO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Review review = new Review();
        BeanUtils.copyProperties(reviewDTO, review);
        review.setUserId(userId);
        boolean success = reviewService.addReview(review);
        if (success) {
            return Result.success("评价成功", null);
        }
        return Result.error("评价失败");
    }

    @ApiOperation("获取图书评价列表")
    @GetMapping("/book/{bookId}")
    public Result<PageResult<Review>> getBookReviews(
            @PathVariable Long bookId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size) {
        IPage<Review> reviewPage = reviewService.getBookReviews(bookId, page, size);
        PageResult<Review> result = PageResult.of(
                reviewPage.getTotal(),
                reviewPage.getRecords(),
                reviewPage.getCurrent(),
                reviewPage.getSize()
        );
        return Result.success(result);
    }

    @ApiOperation("获取我的评价列表")
    @GetMapping("/my")
    public Result<PageResult<Review>> getMyReviews(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        IPage<Review> reviewPage = reviewService.getUserReviews(userId, page, size);
        PageResult<Review> result = PageResult.of(
                reviewPage.getTotal(),
                reviewPage.getRecords(),
                reviewPage.getCurrent(),
                reviewPage.getSize()
        );
        return Result.success(result);
    }

    @ApiOperation("删除评价")
    @DeleteMapping("/{id}")
    public Result<Void> deleteReview(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Review review = reviewService.getById(id);
        if (review == null || !userId.equals(review.getUserId())) {
            throw new BusinessException("评价不存在");
        }
        boolean success = reviewService.deleteReview(id);
        if (success) {
            return Result.success("删除成功", null);
        }
        return Result.error("删除失败");
    }
}

