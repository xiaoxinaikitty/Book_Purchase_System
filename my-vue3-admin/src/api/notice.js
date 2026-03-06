import request from '@/utils/request'

/**
 * 获取公告列表（用户端）
 * @param {Object} params - { page, size, keyword }
 */
export function getNoticeList(params) {
  return request({
    url: '/notice/list',
    method: 'get',
    params
  })
}

/**
 * 获取公告详情
 * @param {Number} id
 */
export function getNoticeDetail(id) {
  return request({
    url: `/notice/detail/${id}`,
    method: 'get'
  })
}

/**
 * 标记已读
 * @param {Number} id
 */
export function markNoticeRead(id) {
  return request({
    url: `/notice/read/${id}`,
    method: 'post'
  })
}

/**
 * 获取未读数量
 */
export function getUnreadNoticeCount() {
  return request({
    url: '/notice/unread-count',
    method: 'get'
  })
}
