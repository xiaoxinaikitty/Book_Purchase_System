import request from '@/utils/request'

/**
 * 获取分类列表（管理员）
 */
export function getAdminCategoryList() {
  return request({
    url: '/admin/category/list',
    method: 'get'
  })
}

/**
 * 获取分类树（管理员）
 */
export function getAdminCategoryTree() {
  return request({
    url: '/admin/category/tree',
    method: 'get'
  })
}

/**
 * 添加分类
 * @param {Object} data
 */
export function addCategory(data) {
  return request({
    url: '/admin/category/add',
    method: 'post',
    data
  })
}

/**
 * 更新分类
 * @param {Object} data
 */
export function updateCategory(data) {
  return request({
    url: '/admin/category/update',
    method: 'put',
    data
  })
}

/**
 * 删除分类
 * @param {Number} id
 */
export function deleteCategory(id) {
  return request({
    url: `/admin/category/${id}`,
    method: 'delete'
  })
}
