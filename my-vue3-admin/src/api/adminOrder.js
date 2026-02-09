import request from '@/utils/request'

/**
 * 管理员订单列表
 * @param {Object} params - { page, size, status, orderNo }
 */
export function getAdminOrderList(params) {
  return request({
    url: '/admin/order/list',
    method: 'get',
    params
  })
}

/**
 * 获取订单详情
 * @param {Number} id
 */
export function getAdminOrderDetail(id) {
  return request({
    url: `/admin/order/${id}`,
    method: 'get'
  })
}

/**
 * 订单发货
 * @param {Number} id
 */
export function shipOrder(id) {
  return request({
    url: `/admin/order/ship/${id}`,
    method: 'put'
  })
}

/**
 * 更新订单状态
 * @param {Number} id
 * @param {Number} status
 */
export function updateOrderStatus(id, status) {
  return request({
    url: `/admin/order/status/${id}`,
    method: 'put',
    params: { status }
  })
}

/**
 * 删除订单
 * @param {Number} id
 */
export function deleteOrder(id) {
  return request({
    url: `/admin/order/${id}`,
    method: 'delete'
  })
}
