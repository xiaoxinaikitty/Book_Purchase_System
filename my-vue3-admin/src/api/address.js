import request from '@/utils/request'

/**
 * 获取地址列表
 */
export function getAddressList() {
  return request({
    url: '/address/list',
    method: 'get'
  })
}

/**
 * 添加地址
 * @param {Object} data
 */
export function addAddress(data) {
  return request({
    url: '/address/add',
    method: 'post',
    data
  })
}

/**
 * 更新地址
 * @param {Object} data
 */
export function updateAddress(data) {
  return request({
    url: '/address/update',
    method: 'put',
    data
  })
}

/**
 * 删除地址
 * @param {Number} id
 */
export function deleteAddress(id) {
  return request({
    url: `/address/${id}`,
    method: 'delete'
  })
}

/**
 * 设置默认地址
 * @param {Number} id
 */
export function setDefaultAddress(id) {
  return request({
    url: `/address/default/${id}`,
    method: 'put'
  })
}
