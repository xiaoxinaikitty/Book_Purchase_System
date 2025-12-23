package com.bookmanager.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录返回VO
 */
@Data
public class LoginVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 角色 0-普通用户 1-管理员
     */
    private Integer role;
    
    /**
     * JWT Token
     */
    private String token;
}

