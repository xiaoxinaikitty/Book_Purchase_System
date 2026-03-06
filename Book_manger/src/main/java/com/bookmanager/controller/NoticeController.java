package com.bookmanager.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bookmanager.common.PageResult;
import com.bookmanager.common.Result;
import com.bookmanager.entity.Notice;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.service.NoticeService;
import com.bookmanager.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 公告控制器（用户端）
 */
@Api(tags = "公告中心")
@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @ApiOperation("获取公告列表")
    @GetMapping("/list")
    public Result<PageResult<Notice>> getNoticeList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("关键词") @RequestParam(required = false) String keyword) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        IPage<Notice> noticePage = noticeService.getUserNoticePage(userId, page, size, keyword);
        PageResult<Notice> result = PageResult.of(
                noticePage.getTotal(),
                noticePage.getRecords(),
                noticePage.getCurrent(),
                noticePage.getSize()
        );
        return Result.success(result);
    }

    @ApiOperation("获取公告详情")
    @GetMapping("/detail/{id}")
    public Result<Notice> getNoticeDetail(@PathVariable Long id) {
        Notice notice = noticeService.getById(id);
        if (notice == null || notice.getStatus() == null || notice.getStatus() != 1) {
            throw new BusinessException("公告不存在或已下线");
        }
        return Result.success(notice);
    }

    @ApiOperation("标记公告已读")
    @PostMapping("/read/{id}")
    public Result<Void> markRead(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        noticeService.markRead(userId, id);
        return Result.success("标记成功", null);
    }

    @ApiOperation("获取未读公告数量")
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount() {
        Long userId = UserContext.getUserId();
        Long count = noticeService.getUnreadCount(userId);
        return Result.success(count);
    }
}
