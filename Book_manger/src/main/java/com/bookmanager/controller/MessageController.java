package com.bookmanager.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bookmanager.common.PageResult;
import com.bookmanager.common.Result;
import com.bookmanager.dto.MessageSendDTO;
import com.bookmanager.entity.Message;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.service.MessageService;
import com.bookmanager.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 站内消息控制器（用户端）
 */
@Api(tags = "站内消息")
@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @ApiOperation("收件箱")
    @GetMapping("/inbox")
    public Result<PageResult<Message>> getInbox(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("状态 0-未读 1-已读") @RequestParam(required = false) Integer status) {
        Long userId = UserContext.getUserId();
        IPage<Message> msgPage = messageService.getInboxPage(userId, page, size, status);
        PageResult<Message> result = PageResult.of(
                msgPage.getTotal(),
                msgPage.getRecords(),
                msgPage.getCurrent(),
                msgPage.getSize()
        );
        return Result.success(result);
    }

    @ApiOperation("发件箱")
    @GetMapping("/sent")
    public Result<PageResult<Message>> getSent(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size) {
        Long userId = UserContext.getUserId();
        IPage<Message> msgPage = messageService.getSentPage(userId, page, size);
        PageResult<Message> result = PageResult.of(
                msgPage.getTotal(),
                msgPage.getRecords(),
                msgPage.getCurrent(),
                msgPage.getSize()
        );
        return Result.success(result);
    }

    @ApiOperation("发送消息")
    @PostMapping("/send")
    public Result<Void> sendMessage(@Valid @RequestBody MessageSendDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (dto.getReceiverId() == null) {
            throw new BusinessException("接收者不能为空");
        }
        boolean success = messageService.sendMessage(userId, dto.getReceiverId(), dto.getTitle(), dto.getContent());
        if (success) {
            return Result.success("发送成功", null);
        }
        return Result.error("发送失败");
    }

    @ApiOperation("消息详情")
    @GetMapping("/detail/{id}")
    public Result<Message> getDetail(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        Message message = messageService.getById(id);
        if (message == null) {
            throw new BusinessException("消息不存在");
        }
        boolean isSender = userId != null && userId.equals(message.getSenderId());
        boolean isReceiver = userId != null && userId.equals(message.getReceiverId());
        if (!isSender && !isReceiver) {
            throw new BusinessException(403, "无权限访问");
        }
        if (isSender && Integer.valueOf(1).equals(message.getDeleteBySender())) {
            throw new BusinessException("消息不存在");
        }
        if (isReceiver && Integer.valueOf(1).equals(message.getDeleteByReceiver())) {
            throw new BusinessException("消息不存在");
        }
        if (isReceiver && (message.getStatus() == null || message.getStatus() == 0)) {
            messageService.markRead(userId, id);
        }
        return Result.success(message);
    }

    @ApiOperation("标记已读")
    @PostMapping("/read/{id}")
    public Result<Void> markRead(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        messageService.markRead(userId, id);
        return Result.success("标记成功", null);
    }

    @ApiOperation("未读数量")
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount() {
        Long userId = UserContext.getUserId();
        Long count = messageService.getUnreadCount(userId);
        return Result.success(count);
    }

    @ApiOperation("删除消息")
    @DeleteMapping("/{id}")
    public Result<Void> deleteMessage(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        boolean success = messageService.deleteForUser(userId, id);
        if (success) {
            return Result.success("删除成功", null);
        }
        return Result.error("删除失败");
    }
}
