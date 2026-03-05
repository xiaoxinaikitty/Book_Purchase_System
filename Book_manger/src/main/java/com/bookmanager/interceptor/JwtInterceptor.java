package com.bookmanager.interceptor;

import com.bookmanager.common.Result;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.utils.JwtUtils;
import com.bookmanager.utils.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 拦截器
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String HEADER_NAME = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 放行OPTIONS请求（跨域预检）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 获取请求头中的Token
        String header = request.getHeader(HEADER_NAME);
        if (!StringUtils.hasText(header)) {
            return writeUnauthorizedResponse(response, "未登录或登录已过期，请重新登录");
        }

        // 解析Token
        String token = header;
        if (header.startsWith(TOKEN_PREFIX)) {
            token = header.substring(TOKEN_PREFIX.length());
        }

        // 验证Token
        if (!jwtUtils.validateToken(token)) {
            return writeUnauthorizedResponse(response, "登录已过期，请重新登录");
        }

        // 将用户信息存入上下文
        Long userId = jwtUtils.getUserIdFromToken(token);
        String username = jwtUtils.getUsernameFromToken(token);
        Integer role = jwtUtils.getRoleFromToken(token);

        UserContext.setUserId(userId);
        UserContext.setUsername(username);
        UserContext.setRole(role);

        return true;
    }

    private boolean writeUnauthorizedResponse(HttpServletResponse response, String message) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        try {
            response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, message)));
            return false;
        } catch (IOException e) {
            throw new BusinessException("鉴权失败，响应写出异常");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        // 请求完成后清除用户上下文
        UserContext.clear();
    }
}

