package com.bookmanager.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bookmanager.common.PageResult;
import com.bookmanager.common.Result;
import com.bookmanager.entity.Review;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.service.ReviewService;
import com.bookmanager.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 评价管理控制器（管理员端）
 */
@Api(tags = "评价管理-管理员端")
@RestController
@RequestMapping("/api/admin/review")
public class AdminReviewController {

    @Autowired
    private ReviewService reviewService;

    @ApiOperation("获取评价列表（分页）")
    @GetMapping("/list")
    public Result<PageResult<Review>> getReviewList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("评分") @RequestParam(required = false) Integer rating,
            @ApiParam("图书ID") @RequestParam(required = false) Long bookId,
            @ApiParam("用户ID") @RequestParam(required = false) Long userId) {
        checkAdmin();
        IPage<Review> reviewPage = reviewService.getAdminReviews(page, size, keyword, rating, bookId, userId);
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
        checkAdmin();
        boolean success = reviewService.deleteReview(id);
        if (success) {
            return Result.success("删除成功", null);
        }
        return Result.error("删除失败");
    }

    private void checkAdmin() {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(403, "无权限访问");
        }
    }
}
