import request from '@/utils/request'

/**
 * 获取公告列表（管理员）
 * @param {Object} params - { page, size, keyword, status }
 */
export function getAdminNoticeList(params) {
  return request({
    url: '/admin/notice/list',
    method: 'get',
    params
  })
}

/**
 * 获取公告详情
 * @param {Number} id
 */
export function getAdminNoticeDetail(id) {
  return request({
    url: `/admin/notice/${id}`,
    method: 'get'
  })
}

/**
 * 新增公告
 * @param {Object} data
 */
export function addNotice(data) {
  return request({
    url: '/admin/notice/add',
    method: 'post',
    data
  })
}

/**
 * 更新公告
 * @param {Object} data
 */
export function updateNotice(data) {
  return request({
    url: '/admin/notice/update',
    method: 'put',
    data
  })
}

/**
 * 更新公告状态
 * @param {Number} id
 * @param {Number} status
 */
export function updateNoticeStatus(id, status) {
  return request({
    url: `/admin/notice/status/${id}`,
    method: 'put',
    params: { status }
  })
}

/**
 * 删除公告
 * @param {Number} id
 */
export function deleteNotice(id) {
  return request({
    url: `/admin/notice/${id}`,
    method: 'delete'
  })
}
