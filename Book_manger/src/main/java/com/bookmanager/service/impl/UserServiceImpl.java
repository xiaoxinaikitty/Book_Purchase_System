package com.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookmanager.entity.User;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.mapper.UserMapper;
import com.bookmanager.service.UserService;
import com.bookmanager.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 密码加密盐值
     */
    private static final String SALT = "book_purchase_system";

    @Override
    public boolean register(String username, String password, String nickname, String email) {
        // 1. 检查用户名是否已存在
        User existUser = getByUsername(username);
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 2. 检查邮箱是否已被使用
        if (StringUtils.hasText(email)) {
            LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
            emailWrapper.eq(User::getEmail, email);
            User emailUser = this.getOne(emailWrapper);
            if (emailUser != null) {
                throw new BusinessException("邮箱已被注册");
            }
        }

        // 3. 创建新用户
        User user = new User();
        user.setUsername(username);
        // 密码加密：MD5(password + salt)
        user.setPassword(encryptPassword(password));
        user.setNickname(StringUtils.hasText(nickname) ? nickname : username);
        user.setEmail(email);
        user.setRole(0); // 普通用户
        user.setStatus(1); // 正常状态

        return this.save(user);
    }

    @Override
    public String login(String username, String password) {
        // 1. 查询用户
        User user = getByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 验证密码
        String encryptedPassword = encryptPassword(password);
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 3. 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 4. 生成JWT Token
        return jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return this.getOne(wrapper);
    }

    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        // 1. 查询用户
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 2. 验证旧密码
        String encryptedOldPassword = encryptPassword(oldPassword);
        if (!encryptedOldPassword.equals(user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 3. 更新密码
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPassword(encryptPassword(newPassword));
        return this.updateById(updateUser);
    }

    @Override
    public IPage<User> getUserPage(Integer page, Integer size, String keyword) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        // 只查询普通用户
        wrapper.eq(User::getRole, 0);
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(User::getUsername, keyword)
                    .or()
                    .like(User::getNickname, keyword)
                    .or()
                    .like(User::getEmail, keyword)
            );
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        
        // 查询时不返回密码
        IPage<User> result = this.page(pageParam, wrapper);
        result.getRecords().forEach(u -> u.setPassword(null));
        
        return result;
    }

    @Override
    public boolean updateStatus(Long userId, Integer status) {
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        return this.updateById(user);
    }

    /**
     * 密码加密
     * @param password 原始密码
     * @return 加密后的密码
     */
    private String encryptPassword(String password) {
        String input = password + SALT;
        return DigestUtils.md5DigestAsHex(input.getBytes(StandardCharsets.UTF_8));
    }
}
