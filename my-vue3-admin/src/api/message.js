import request from '@/utils/request'

/**
 * 收件箱
 * @param {Object} params - { page, size, status }
 */
export function getInbox(params) {
  return request({
    url: '/message/inbox',
    method: 'get',
    params
  })
}

/**
 * 发件箱
 * @param {Object} params - { page, size }
 */
export function getSent(params) {
  return request({
    url: '/message/sent',
    method: 'get',
    params
  })
}

/**
 * 发送消息
 * @param {Object} data - { receiverId, title, content }
 */
export function sendMessage(data) {
  return request({
    url: '/message/send',
    method: 'post',
    data
  })
}

/**
 * 消息详情
 * @param {Number} id
 */
export function getMessageDetail(id) {
  return request({
    url: `/message/detail/${id}`,
    method: 'get'
  })
}

/**
 * 标记已读
 * @param {Number} id
 */
export function markMessageRead(id) {
  return request({
    url: `/message/read/${id}`,
    method: 'post'
  })
}

/**
 * 删除消息
 * @param {Number} id
 */
export function deleteMessage(id) {
  return request({
    url: `/message/${id}`,
    method: 'delete'
  })
}

/**
 * 未读数量
 */
export function getMessageUnreadCount() {
  return request({
    url: '/message/unread-count',
    method: 'get'
  })
}
