import request from '@/utils/request'

/**
 * 获取购物车列表
 */
export function getCartList() {
  return request({
    url: '/cart/list',
    method: 'get'
  })
}

/**
 * 添加商品到购物车
 * @param {Object} data - { bookId, quantity }
 */
export function addToCart(data) {
  return request({
    url: '/cart/add',
    method: 'post',
    data
  })
}

/**
 * 更新购物车数量
 * @param {Number} cartId
 * @param {Number} quantity
 */
export function updateCartQuantity(cartId, quantity) {
  return request({
    url: '/cart/update',
    method: 'put',
    params: { cartId, quantity }
  })
}

/**
 * 删除购物车商品
 * @param {Number} id
 */
export function removeFromCart(id) {
  return request({
    url: `/cart/${id}`,
    method: 'delete'
  })
}

/**
 * 清空购物车
 */
export function clearCart() {
  return request({
    url: '/cart/clear',
    method: 'delete'
  })
}

/**
 * 获取购物车数量
 */
export function getCartCount() {
  return request({
    url: '/cart/count',
    method: 'get'
  })
}
