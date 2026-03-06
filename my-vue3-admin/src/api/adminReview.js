import request from '@/utils/request'

/**
 * 获取评价列表（管理员）
 * @param {Object} params - { page, size, keyword, rating, bookId, userId }
 */
export function getAdminReviewList(params) {
  return request({
    url: '/admin/review/list',
    method: 'get',
    params
  })
}

/**
 * 删除评价
 * @param {Number} id
 */
export function deleteAdminReview(id) {
  return request({
    url: `/admin/review/${id}`,
    method: 'delete'
  })
}
