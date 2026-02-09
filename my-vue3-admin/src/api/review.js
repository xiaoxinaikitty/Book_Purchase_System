import request from '@/utils/request'

export const addReview = (data) => {
  return request({
    url: '/review/add',
    method: 'post',
    data
  })
}

export const getBookReviews = (bookId, params) => {
  return request({
    url: `/review/book/${bookId}`,
    method: 'get',
    params
  })
}

export const getMyReviews = (params) => {
  return request({
    url: '/review/my',
    method: 'get',
    params
  })
}

export const deleteReview = (id) => {
  return request({
    url: `/review/${id}`,
    method: 'delete'
  })
}
