import request from '@/utils/request'

/**
 * 用户登录
 * @param {Object} data - { username, password }
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 用户注册
 * @param {Object} data - { username, password, confirmPassword, nickname, email }
 */
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

/**
 * 退出登录
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

/**
 * 检查用户名是否可用
 * @param {String} username
 */
export function checkUsername(username) {
  return request({
    url: '/auth/check-username',
    method: 'get',
    params: { username }
  })
}

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

/**
 * 更新用户信息
 * @param {Object} data - 用户信息
 */
export function updateUserInfo(data) {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

/**
 * 修改密码
 * @param {Object} data - { oldPassword, newPassword, confirmPassword }
 */
export function updatePassword(data) {
  return request({
    url: '/user/password',
    method: 'put',
    data
  })
}

/**
 * 更新头像
 * @param {String} avatar
 */
export function updateAvatar(avatar) {
  return request({
    url: '/user/avatar',
    method: 'put',
    params: { avatar }
  })
}
