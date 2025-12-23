package com.bookmanager.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bookmanager.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户注册
     */
    boolean register(String username, String password, String nickname, String email);
    
    /**
     * 用户登录
     */
    String login(String username, String password);
    
    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);
    
    /**
     * 修改密码
     */
    boolean updatePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 分页查询用户列表（管理员）
     */
    IPage<User> getUserPage(Integer page, Integer size, String keyword);
    
    /**
     * 更新用户状态
     */
    boolean updateStatus(Long userId, Integer status);
}

