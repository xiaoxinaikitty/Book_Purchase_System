import request from '@/utils/request'

/**
 * 创建订单
 * @param {Object} data - { cartIds, addressId, remark }
 */
export function createOrder(data) {
  return request({
    url: '/order/create',
    method: 'post',
    data
  })
}

/**
 * 获取订单列表
 * @param {Object} params - { page, size, status }
 */
export function getOrderList(params) {
  return request({
    url: '/order/list',
    method: 'get',
    params
  })
}

/**
 * 获取订单详情
 * @param {Number} id
 */
export function getOrderDetail(id) {
  return request({
    url: `/order/detail/${id}`,
    method: 'get'
  })
}

/**
 * 取消订单
 * @param {Number} id
 */
export function cancelOrder(id) {
  return request({
    url: `/order/cancel/${id}`,
    method: 'put'
  })
}

/**
 * 支付订单
 * @param {Number} id
 */
export function payOrder(id) {
  return request({
    url: `/order/pay/${id}`,
    method: 'put'
  })
}

/**
 * 确认收货
 * @param {Number} id
 */
export function confirmOrder(id) {
  return request({
    url: `/order/confirm/${id}`,
    method: 'put'
  })
}
