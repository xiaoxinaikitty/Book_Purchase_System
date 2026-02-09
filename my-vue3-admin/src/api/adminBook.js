import request from '@/utils/request'

/**
 * 获取图书列表（管理员）
 * @param {Object} params - { page, size, categoryId, keyword, status }
 */
export function getAdminBookList(params) {
  return request({
    url: '/admin/book/list',
    method: 'get',
    params
  })
}

/**
 * 获取图书详情（管理员）
 * @param {Number} id
 */
export function getAdminBookDetail(id) {
  return request({
    url: `/admin/book/${id}`,
    method: 'get'
  })
}

/**
 * 添加图书
 * @param {Object} data
 */
export function addBook(data) {
  return request({
    url: '/admin/book/add',
    method: 'post',
    data
  })
}

/**
 * 更新图书
 * @param {Object} data
 */
export function updateBook(data) {
  return request({
    url: '/admin/book/update',
    method: 'put',
    data
  })
}

/**
 * 删除图书
 * @param {Number} id
 */
export function deleteBook(id) {
  return request({
    url: `/admin/book/${id}`,
    method: 'delete'
  })
}

/**
 * 更新图书状态
 * @param {Number} id
 * @param {Number} status
 */
export function updateBookStatus(id, status) {
  return request({
    url: `/admin/book/status/${id}`,
    method: 'put',
    params: { status }
  })
}
