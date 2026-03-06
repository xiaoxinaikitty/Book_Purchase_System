import request from '@/utils/request'

/**
 * 获取已发送消息（管理员）
 * @param {Object} params - { page, size, keyword }
 */
export function getAdminMessageList(params) {
  return request({
    url: '/admin/message/list',
    method: 'get',
    params
  })
}

/**
 * 发送消息（管理员，可群发）
 * @param {Object} data - { receiverId, title, content }
 */
export function sendAdminMessage(data) {
  return request({
    url: '/admin/message/send',
    method: 'post',
    data
  })
}

/**
 * 删除消息
 * @param {Number} id
 */
export function deleteAdminMessage(id) {
  return request({
    url: `/admin/message/${id}`,
    method: 'delete'
  })
}
