import request from '@/utils/request'

/**
 * 获取图书列表
 * @param {Object} params - { page, size, categoryId, keyword }
 */
export function getBookList(params) {
  return request({
    url: '/book/list',
    method: 'get',
    params
  })
}

/**
 * 获取图书详情
 * @param {Number} id
 */
export function getBookDetail(id) {
  return request({
    url: `/book/detail/${id}`,
    method: 'get'
  })
}

/**
 * 搜索图书
 * @param {Object} params - { keyword, page, size }
 */
export function searchBooks(params) {
  return request({
    url: '/book/search',
    method: 'get',
    params
  })
}

/**
 * 获取热门图书
 * @param {Number} limit
 */
export function getHotBooks(limit) {
  return request({
    url: '/book/hot',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取新书推荐
 * @param {Number} limit
 */
export function getNewBooks(limit) {
  return request({
    url: '/book/new',
    method: 'get',
    params: { limit }
  })
}
