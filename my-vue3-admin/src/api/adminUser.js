import request from '@/utils/request'

/**
 * 获取用户列表（分页）
 * @param {Object} params - { page, size, keyword }
 */
export function getUserList(params) {
  return request({
    url: '/admin/user/list',
    method: 'get',
    params
  })
}

/**
 * 获取用户详情
 * @param {Number} id
 */
export function getUserDetail(id) {
  return request({
    url: /admin/user/,
    method: 'get'
  })
}

/**
 * 更新用户状态
 * @param {Number} id
 * @param {Number} status - 0 禁用, 1 正常
 */
export function updateUserStatus(id, status) {
  return request({
    url: /admin/user/status/,
    method: 'put',
    params: { status }
  })
}

/**
 * 重置用户密码
 * @param {Number} id
 */
export function resetUserPassword(id) {
  return request({
    url: /admin/user/reset-password/,
    method: 'put'
  })
}

/**
 * 删除用户
 * @param {Number} id
 */
export function deleteUser(id) {
  return request({
    url: /admin/user/,
    method: 'delete'
  })
}
