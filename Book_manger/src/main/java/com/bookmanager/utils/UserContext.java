package com.bookmanager.utils;

/**
 * 用户上下文工具类
 * 用于在请求生命周期内存储和获取当前登录用户信息
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    private static final ThreadLocal<Integer> ROLE = new ThreadLocal<>();

    /**
     * 设置当前用户ID
     */
    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        return USER_ID.get();
    }

    /**
     * 设置当前用户名
     */
    public static void setUsername(String username) {
        USERNAME.set(username);
    }

    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        return USERNAME.get();
    }

    /**
     * 设置当前用户角色
     */
    public static void setRole(Integer role) {
        ROLE.set(role);
    }

    /**
     * 获取当前用户角色
     */
    public static Integer getRole() {
        return ROLE.get();
    }

    /**
     * 判断当前用户是否为管理员
     */
    public static boolean isAdmin() {
        Integer role = ROLE.get();
        return role != null && role == 1;
    }

    /**
     * 清除当前线程的用户信息
     */
    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
        ROLE.remove();
    }
}

