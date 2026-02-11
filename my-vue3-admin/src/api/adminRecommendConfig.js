import request from '@/utils/request'

export const getRecommendConfig = () => {
  return request({
    url: '/admin/recommend/config',
    method: 'get'
  })
}

export const updateRecommendConfig = (data) => {
  return request({
    url: '/admin/recommend/config',
    method: 'put',
    data
  })
}
