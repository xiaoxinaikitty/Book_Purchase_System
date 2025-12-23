# 认证模块 API 接口文档

> 基于KNN算法的购书推荐系统 - 认证模块接口文档
> 
> 基础路径: `http://localhost:8080`
> 
> 文档更新时间: 2024年

---

## 目录

1. [用户登录](#1-用户登录)
2. [用户注册](#2-用户注册)
3. [退出登录](#3-退出登录)
4. [检查用户名是否可用](#4-检查用户名是否可用)
5. [获取当前用户信息](#5-获取当前用户信息)
6. [更新用户信息](#6-更新用户信息)
7. [修改密码](#7-修改密码)
8. [更新头像](#8-更新头像)
9. [管理员-获取用户列表](#9-管理员-获取用户列表)
10. [管理员-获取用户详情](#10-管理员-获取用户详情)
11. [管理员-更新用户状态](#11-管理员-更新用户状态)
12. [管理员-重置用户密码](#12-管理员-重置用户密码)
13. [管理员-删除用户](#13-管理员-删除用户)

---

## 通用说明

### 请求头

需要认证的接口请在请求头中添加：

```
Authorization: Bearer <token>
```

### 响应格式

所有接口统一返回格式：

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {}
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未登录或登录过期 |
| 403 | 无权限 |
| 500 | 服务器错误 |

---

## 1. 用户登录

### 接口信息

| 项目 | 说明 |
|------|------|
| **功能** | 用户/管理员登录系统 |
| **路径** | `/api/auth/login` |
| **方法** | `POST` |
| **认证** | 不需要 |

### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

### 请求示例

```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456"
  }'
```

### 响应示例

**成功响应：**

```json
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "userId": 1,
        "username": "testuser",
        "nickname": "测试用户",
        "avatar": "http://xxx.com/avatar.jpg",
        "role": 0,
        "token": "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoidGVzdHVzZXIiLCJyb2xlIjowLCJpYXQiOjE3MDM..."
    }
}
```

**失败响应：**

```json
{
    "code": 500,
    "message": "用户名或密码错误",
    "data": null
}
```

---

## 2. 用户注册

### 接口信息

| 项目 | 说明 |
|------|------|
| **功能** | 新用户注册账号 |
| **路径** | `/api/auth/register` |
| **方法** | `POST` |
| **认证** | 不需要 |

### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名（4-20字符） |
| password | String | 是 | 密码（6-20字符） |
| confirmPassword | String | 是 | 确认密码 |
| nickname | String | 否 | 昵称 |
| email | String | 否 | 邮箱 |

### 请求示例

```bash
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "123456",
    "confirmPassword": "123456",
    "nickname": "新用户",
    "email": "newuser@example.com"
  }'
```

### 响应示例

**成功响应：**

```json
{
    "code": 200,
    "message": "注册成功",
    "data": null
}
```

**失败响应：**

```json
{
    "code": 500,
    "message": "用户名已存在",
    "data": null
}
```

---

## 3. 退出登录

### 接口信息

| 项目 | 说明 |
|------|------|
| **功能** | 用户退出登录 |
| **路径** | `/api/auth/logout` |
| **方法** | `POST` |
| **认证** | 需要 |

### 请求示例

```bash
curl -X POST "http://localhost:8080/api/auth/logout" \
  -H "Authorization: Bearer <token>"
```

### 响应示例

```json
{
    "code": 200,
    "message": "退出成功",
    "data": null
}
```

---

## 4. 检查用户名是否可用

### 接口信息

| 项目 | 说明 |
|------|------|
| **功能** | 检查用户名是否已被注册 |
| **路径** | `/api/auth/check-username` |
| **方法** | `GET` |
| **认证** | 不需要 |

### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 要检查的用户名 |

### 请求示例

```bash
curl -X GET "http://localhost:8080/api/auth/check-username?username=testuser"
```

### 响应示例

**用户名可用：**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": true
}
```

**用户名已存在：**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": false
}
```

---

## 5. 获取当前用户信息

### 接口信息

| 项目 | 说明 |
|------|------|
| **功能** | 获取当前登录用户的详细信息 |
| **路径** | `/api/user/info` |
| **方法** | `GET` |
| **认证** | 需要 |

### 请求示例

```bash
curl -X GET "http://localhost:8080/api/user/info" \
  -H "Authorization: Bearer <token>"
```

### 响应示例

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "id": 1,
        "username": "testuser",
        "nickname": "测试用户",
        "email": "test@example.com",
        "phone": "13800138000",
        "avatar": "http://xxx.com/avatar.jpg",
        "role": 0,
        "status": 1,
        "createTime": "2024-01-01 10:00:00"
    }
}
```

---

## 6. 更新用户信息

### 接口信息

| 项目 | 说明 |
|------|------|
| **功能** | 更新当前用户的基本信息 |
| **路径** | `/api/user/update` |
| **方法** | `PUT` |
| **认证** | 需要 |

### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| nickname | String | 否 | 昵称 |
| email | String | 否 | 邮箱 |
| phone | String | 否 | 手机号 |
| avatar | String | 否 | 头像URL |

### 请求示例

```bash
curl -X PUT "http://localhost:8080/api/user/update" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "nickname": "新昵称",
    "email": "newemail@example.com",
    "phone": "13900139000"
  }'
```

### 响应示例

```json
{
    "code": 200,
    "message": "更新成功",
    "data": null
}
```

---

## 7. 修改密码

### 接口信息

| 项目 | 说明 |
|------|------|
| **功能** | 修改当前用户密码 |
| **路径** | `/api/user/password` |
| **方法** | `PUT` |
| **认证** | 需要 |

### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| oldPassword | String | 是 | 原密码 |
| newPassword | String | 是 | 新密码（6-20字符） |
| confirmPassword | String | 是 | 确认新密码 |

### 请求示例

```bash
curl -X PUT "http://localhost:8080/api/user/password" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "oldPassword": "123456",
    "newPassword": "654321",
    "confirmPassword": "654321"
  }'
```

### 响应示例

**成功响应：**

```json
{
    "code": 200,
    "message": "密码修改成功，请重新登录",
    "data": null
}
```

**失败响应：**

```json
{
    "code": 500,
    "message": "原密码错误",
    "data": null
}
```

---

## 8. 更新头像

### 接口信息

| 项目 | 说明 |
|------|------|
| **功能** | 更新用户头像URL |
| **路径** | `/api/user/avatar` |
| **方法** | `PUT` |
| **认证** | 需要 |

### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| avatar | String | 是 | 头像URL（Query参数） |

### 请求示例

```bash
curl -X PUT "http://localhost:8080/api/user/avatar?avatar=http://xxx.com/new-avatar.jpg" \
  -H "Authorization: Bearer <token>"
```

### 响应示例

```json
{
    "code": 200,
    "message": "头像更新成功",
    "data": null
}
```

---

## 9. 管理员-获取用户列表

### 接口信息

| 项目 | 说明 |
|------|------|
| **功能** | 管理员分页查询用户列表 |
| **路径** | `/api/admin/user/list` |
| **方法** | `GET` |
| **认证** | 需要（管理员） |

### 请求参数

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页数量 |
| keyword | String | 否 | - | 搜索关键词（用户名/昵称/邮箱） |

### 请求示例

```bash
curl -X GET "http://localhost:8080/api/admin/user/list?page=1&size=10&keyword=test" \
  -H "Authorization: Bearer <admin_token>"
```

### 响应示例

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "total": 100,
        "records": [
            {
                "id": 2,
                "username": "testuser",
                "nickname": "测试用户",
                "email": "test@example.com",
                "phone": "13800138000",
                "avatar": null,
                "role": 0,
                "status": 1,
                "createTime": "2024-01-01 10:00:00"
            }
        ],
        "current": 1,
        "size": 10,
        "pages": 10
    }
}
```

---

## 10. 管理员-获取用户详情

### 接口信息

| 项目 | 说明 |
|------|------|
| **功能** | 管理员获取指定用户详情 |
| **路径** | `/api/admin/user/{id}` |
| **方法** | `GET` |
| **认证** | 需要（管理员） |

### 请求示例

```bash
curl -X GET "http://localhost:8080/api/admin/user/2" \
  -H "Authorization: Bearer <admin_token>"
```

### 响应示例

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "id": 2,
        "username": "testuser",
        "nickname": "测试用户",
        "email": "test@example.com",
        "phone": "13800138000",
        "avatar": null,
        "role": 0,
        "status": 1,
        "createTime": "2024-01-01 10:00:00"
    }
}
```

---

## 11. 管理员-更新用户状态

### 接口信息

| 项目 | 说明 |
|------|------|
| **功能** | 管理员启用/禁用用户 |
| **路径** | `/api/admin/user/status/{id}` |
| **方法** | `PUT` |
| **认证** | 需要（管理员） |

### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 用户ID（路径参数） |
| status | Integer | 是 | 状态：0-禁用 1-正常（Query参数） |

### 请求示例

```bash
# 禁用用户
curl -X PUT "http://localhost:8080/api/admin/user/status/2?status=0" \
  -H "Authorization: Bearer <admin_token>"

# 启用用户
curl -X PUT "http://localhost:8080/api/admin/user/status/2?status=1" \
  -H "Authorization: Bearer <admin_token>"
```

### 响应示例

```json
{
    "code": 200,
    "message": "用户已禁用",
    "data": null
}
```

---

## 12. 管理员-重置用户密码

### 接口信息

| 项目 | 说明 |
|------|------|
| **功能** | 管理员重置用户密码为默认密码 |
| **路径** | `/api/admin/user/reset-password/{id}` |
| **方法** | `PUT` |
| **认证** | 需要（管理员） |

### 请求示例

```bash
curl -X PUT "http://localhost:8080/api/admin/user/reset-password/2" \
  -H "Authorization: Bearer <admin_token>"
```

### 响应示例

```json
{
    "code": 200,
    "message": "密码已重置为: 123456",
    "data": "123456"
}
```

---

## 13. 管理员-删除用户

### 接口信息

| 项目 | 说明 |
|------|------|
| **功能** | 管理员删除用户 |
| **路径** | `/api/admin/user/{id}` |
| **方法** | `DELETE` |
| **认证** | 需要（管理员） |

### 请求示例

```bash
curl -X DELETE "http://localhost:8080/api/admin/user/2" \
  -H "Authorization: Bearer <admin_token>"
```

### 响应示例

```json
{
    "code": 200,
    "message": "删除成功",
    "data": null
}
```

---

## 测试账号

系统预置了以下测试账号：

| 账号 | 密码 | 角色 | 说明 |
|------|------|------|------|
| admin | admin123 | 管理员 | 系统管理员账号 |

> **注意**: 新注册用户默认为普通用户（role=0）

---

## Postman 测试集合

### 环境变量设置

```json
{
    "baseUrl": "http://localhost:8080",
    "token": ""
}
```

### 登录后自动设置Token（Postman Tests脚本）

在登录接口的 Tests 标签中添加：

```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    if (jsonData.data && jsonData.data.token) {
        pm.environment.set("token", jsonData.data.token);
    }
}
```

---

## 更新日志

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| v1.0.0 | 2024-12 | 初始版本，完成认证模块所有接口 |

