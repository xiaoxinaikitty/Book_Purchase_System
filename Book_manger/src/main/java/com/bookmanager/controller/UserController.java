package com.bookmanager.controller;

import com.bookmanager.common.Result;
import com.bookmanager.dto.PasswordDTO;
import com.bookmanager.entity.User;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.service.UserService;
import com.bookmanager.utils.UserContext;
import com.bookmanager.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户控制器
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public Result<UserVO> getUserInfo() {
        // 从上下文获取当前用户ID
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        // 查询用户信息
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 转换为VO对象
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return Result.success(userVO);
    }

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新结果
     */
    @ApiOperation("更新用户信息")
    @PutMapping("/update")
    public Result<?> updateUserInfo(@RequestBody User user) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        // 只允许更新部分字段
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setNickname(user.getNickname());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setAvatar(user.getAvatar());

        boolean success = userService.updateById(updateUser);
        if (success) {
            return Result.success("更新成功", null);
        } else {
            return Result.error("更新失败");
        }
    }

    /**
     * 修改密码
     * @param passwordDTO 密码信息
     * @return 修改结果
     */
    @ApiOperation("修改密码")
    @PutMapping("/password")
    public Result<?> updatePassword(@Valid @RequestBody PasswordDTO passwordDTO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        // 验证两次密码是否一致
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            throw new BusinessException("两次输入的新密码不一致");
        }

        boolean success = userService.updatePassword(
                userId,
                passwordDTO.getOldPassword(),
                passwordDTO.getNewPassword()
        );

        if (success) {
            return Result.success("密码修改成功，请重新登录", null);
        } else {
            return Result.error("密码修改失败");
        }
    }

    /**
     * 更新用户头像
     * @param avatar 头像URL
     * @return 更新结果
     */
    @ApiOperation("更新头像")
    @PutMapping("/avatar")
    public Result<?> updateAvatar(@RequestParam String avatar) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setAvatar(avatar);

        boolean success = userService.updateById(updateUser);
        if (success) {
            return Result.success("头像更新成功", null);
        } else {
            return Result.error("头像更新失败");
        }
    }
}
