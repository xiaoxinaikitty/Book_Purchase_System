package com.bookmanager.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bookmanager.common.PageResult;
import com.bookmanager.common.Result;
import com.bookmanager.entity.User;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.service.UserService;
import com.bookmanager.utils.UserContext;
import com.bookmanager.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理控制器（管理员端）
 */
@Api(tags = "用户管理-管理员端")
@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户列表（分页）
     */
    @ApiOperation("获取用户列表（分页）")
    @GetMapping("/list")
    public Result<PageResult<UserVO>> getUserList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("关键词") @RequestParam(required = false) String keyword) {
        
        // 检查是否为管理员
        checkAdmin();
        
        IPage<User> userPage = userService.getUserPage(page, size, keyword);
        
        // 转换为VO列表
        List<UserVO> voList = userPage.getRecords().stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            return vo;
        }).collect(Collectors.toList());
        
        PageResult<UserVO> result = PageResult.of(
                userPage.getTotal(),
                voList,
                userPage.getCurrent(),
                userPage.getSize()
        );
        
        return Result.success(result);
    }

    /**
     * 获取用户详情
     */
    @ApiOperation("获取用户详情")
    @GetMapping("/{id}")
    public Result<UserVO> getUserDetail(@PathVariable Long id) {
        checkAdmin();
        
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        
        return Result.success(vo);
    }

    /**
     * 更新用户状态（启用/禁用）
     */
    @ApiOperation("更新用户状态（启用/禁用）")
    @PutMapping("/status/{id}")
    public Result<?> updateStatus(
            @PathVariable Long id,
            @ApiParam("状态 0-禁用 1-正常") @RequestParam Integer status) {
        
        checkAdmin();
        
        // 不能禁用自己
        if (id.equals(UserContext.getUserId())) {
            throw new BusinessException("不能修改自己的状态");
        }
        
        boolean success = userService.updateStatus(id, status);
        if (success) {
            return Result.success(status == 1 ? "用户已启用" : "用户已禁用", null);
        } else {
            return Result.error("操作失败");
        }
    }

    /**
     * 重置用户密码
     */
    @ApiOperation("重置用户密码")
    @PutMapping("/reset-password/{id}")
    public Result<String> resetPassword(@PathVariable Long id) {
        checkAdmin();
        
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 重置为默认密码 123456
        String defaultPassword = "123456";
        User updateUser = new User();
        updateUser.setId(id);
        // 使用MD5加密
        String encryptedPassword = DigestUtils
                .md5DigestAsHex((defaultPassword + "book_purchase_system").getBytes(StandardCharsets.UTF_8));
        updateUser.setPassword(encryptedPassword);
        
        boolean success = userService.updateById(updateUser);
        if (success) {
            return Result.success("密码已重置为: " + defaultPassword, defaultPassword);
        } else {
            return Result.error("重置失败");
        }
    }

    /**
     * 删除用户
     */
    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    public Result<?> deleteUser(@PathVariable Long id) {
        checkAdmin();
        
        // 不能删除自己
        if (id.equals(UserContext.getUserId())) {
            throw new BusinessException("不能删除自己");
        }
        
        boolean success = userService.removeById(id);
        if (success) {
            return Result.success("删除成功", null);
        } else {
            return Result.error("删除失败");
        }
    }

    /**
     * 检查当前用户是否为管理员
     */
    private void checkAdmin() {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(403, "无权限访问");
        }
    }
}
