package com.bookmanager.controller.admin;

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

import java.time.LocalDateTime;

/**
 * 公告管理控制器（管理员端）
 */
@Api(tags = "公告管理-管理员端")
@RestController
@RequestMapping("/api/admin/notice")
public class AdminNoticeController {

    @Autowired
    private NoticeService noticeService;

    @ApiOperation("获取公告列表（分页）")
    @GetMapping("/list")
    public Result<PageResult<Notice>> getNoticeList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        checkAdmin();
        IPage<Notice> noticePage = noticeService.getAdminNoticePage(page, size, keyword, status);
        PageResult<Notice> result = PageResult.of(
                noticePage.getTotal(),
                noticePage.getRecords(),
                noticePage.getCurrent(),
                noticePage.getSize()
        );
        return Result.success(result);
    }

    @ApiOperation("获取公告详情")
    @GetMapping("/{id}")
    public Result<Notice> getNoticeDetail(@PathVariable Long id) {
        checkAdmin();
        Notice notice = noticeService.getById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        return Result.success(notice);
    }

    @ApiOperation("新增公告")
    @PostMapping("/add")
    public Result<Void> addNotice(@RequestBody Notice notice) {
        checkAdmin();
        normalizeNotice(notice);
        boolean success = noticeService.save(notice);
        if (success) {
            return Result.success("新增成功", null);
        }
        return Result.error("新增失败");
    }

    @ApiOperation("更新公告")
    @PutMapping("/update")
    public Result<Void> updateNotice(@RequestBody Notice notice) {
        checkAdmin();
        if (notice.getId() == null) {
            throw new BusinessException("公告ID不能为空");
        }
        normalizeNotice(notice);
        boolean success = noticeService.updateById(notice);
        if (success) {
            return Result.success("更新成功", null);
        }
        return Result.error("更新失败");
    }

    @ApiOperation("更新公告状态")
    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @ApiParam("状态 0-下线 1-发布") @RequestParam Integer status) {
        checkAdmin();
        Notice update = new Notice();
        update.setId(id);
        update.setStatus(status);
        if (status != null && status == 1) {
            update.setPublishTime(LocalDateTime.now());
        }
        boolean success = noticeService.updateById(update);
        if (success) {
            return Result.success("状态更新成功", null);
        }
        return Result.error("状态更新失败");
    }

    @ApiOperation("删除公告")
    @DeleteMapping("/{id}")
    public Result<Void> deleteNotice(@PathVariable Long id) {
        checkAdmin();
        boolean success = noticeService.removeById(id);
        if (success) {
            return Result.success("删除成功", null);
        }
        return Result.error("删除失败");
    }

    private void normalizeNotice(Notice notice) {
        if (notice == null) {
            throw new BusinessException("公告数据不能为空");
        }
        if (notice.getStatus() == null) {
            notice.setStatus(1);
        }
        if (notice.getPriority() == null) {
            notice.setPriority(0);
        }
        if (notice.getStatus() == 1 && notice.getPublishTime() == null) {
            notice.setPublishTime(LocalDateTime.now());
        }
    }

    private void checkAdmin() {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(403, "无权限访问");
        }
    }
}
