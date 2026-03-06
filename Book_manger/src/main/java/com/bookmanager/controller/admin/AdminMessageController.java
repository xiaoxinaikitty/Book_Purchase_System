package com.bookmanager.controller.admin;

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
 * 站内消息控制器（管理员端）
 */
@Api(tags = "站内消息-管理员端")
@RestController
@RequestMapping("/api/admin/message")
public class AdminMessageController {

    @Autowired
    private MessageService messageService;

    @ApiOperation("获取已发送消息列表")
    @GetMapping("/list")
    public Result<PageResult<Message>> getSentList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("关键词") @RequestParam(required = false) String keyword) {
        checkAdmin();
        Long adminId = UserContext.getUserId();
        IPage<Message> msgPage = messageService.getAdminSentPage(adminId, page, size, keyword);
        PageResult<Message> result = PageResult.of(
                msgPage.getTotal(),
                msgPage.getRecords(),
                msgPage.getCurrent(),
                msgPage.getSize()
        );
        return Result.success(result);
    }

    @ApiOperation("发送消息（可群发）")
    @PostMapping("/send")
    public Result<String> sendMessage(@Valid @RequestBody MessageSendDTO dto) {
        checkAdmin();
        Long adminId = UserContext.getUserId();
        if (dto.getReceiverId() == null) {
            int count = messageService.sendMessageToAll(adminId, dto.getTitle(), dto.getContent());
            return Result.success("群发成功，共发送 " + count + " 条", String.valueOf(count));
        }
        boolean success = messageService.sendMessage(adminId, dto.getReceiverId(), dto.getTitle(), dto.getContent());
        if (success) {
            return Result.success("发送成功", "1");
        }
        return Result.error("发送失败");
    }

    @ApiOperation("删除已发送消息")
    @DeleteMapping("/{id}")
    public Result<Void> deleteMessage(@PathVariable Long id) {
        checkAdmin();
        Long adminId = UserContext.getUserId();
        boolean success = messageService.deleteForUser(adminId, id);
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
