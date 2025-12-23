package com.bookmanager.controller;

import com.bookmanager.common.Result;
import com.bookmanager.dto.LoginDTO;
import com.bookmanager.dto.RegisterDTO;
import com.bookmanager.entity.User;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.service.UserService;
import com.bookmanager.vo.LoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器
 */
@Api(tags = "认证管理")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return 登录结果（包含token）
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        // 1. 执行登录，获取token
        String token = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
        
        // 2. 查询用户信息
        User user = userService.getByUsername(loginDTO.getUsername());
        
        // 3. 构建返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        loginVO.setAvatar(user.getAvatar());
        loginVO.setRole(user.getRole());
        loginVO.setToken(token);
        
        return Result.success("登录成功", loginVO);
    }

    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDTO registerDTO) {
        // 1. 验证两次密码是否一致
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }
        
        // 2. 执行注册
        boolean success = userService.register(
                registerDTO.getUsername(),
                registerDTO.getPassword(),
                registerDTO.getNickname(),
                registerDTO.getEmail()
        );
        
        if (success) {
            return Result.success("注册成功", null);
        } else {
            return Result.error("注册失败，请稍后重试");
        }
    }

    /**
     * 退出登录
     * @return 退出结果
     */
    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public Result<?> logout() {
        // JWT是无状态的，客户端删除token即可
        // 如果需要服务端控制，可以将token加入黑名单（使用Redis）
        return Result.success("退出成功", null);
    }

    /**
     * 检查用户名是否可用
     * @param username 用户名
     * @return 是否可用
     */
    @ApiOperation("检查用户名是否可用")
    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        User user = userService.getByUsername(username);
        // true表示可用，false表示已存在
        return Result.success(user == null);
    }
}
