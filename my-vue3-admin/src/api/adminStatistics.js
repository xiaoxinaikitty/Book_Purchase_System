import request from '@/utils/request'

export const getOverview = () => {
  return request({
    url: '/admin/statistics/overview',
    method: 'get'
  })
}

export const getSalesStatistics = (params) => {
  return request({
    url: '/admin/statistics/sales',
    method: 'get',
    params
  })
}

export const getSalesRank = (params) => {
  return request({
    url: '/admin/statistics/rank/sales',
    method: 'get',
    params
  })
}

export const getCategorySales = () => {
  return request({
    url: '/admin/statistics/category/sales',
    method: 'get'
  })
}

export const getUserGrowth = (params) => {
  return request({
    url: '/admin/statistics/user/growth',
    method: 'get',
    params
  })
}
