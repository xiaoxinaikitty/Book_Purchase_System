import request from '@/utils/request'

export const getPersonalRecommend = (params) => {
  return request({
    url: '/recommend/personal',
    method: 'get',
    params
  })
}

export const getHotRecommend = (params) => {
  return request({
    url: '/recommend/hot',
    method: 'get',
    params
  })
}

export const getSimilarRecommend = (bookId, params) => {
  return request({
    url: `/recommend/similar/${bookId}`,
    method: 'get',
    params
  })
}

export const getGuessRecommend = (params) => {
  return request({
    url: '/recommend/guess',
    method: 'get',
    params
  })
}

export const getNewRecommend = (params) => {
  return request({
    url: '/recommend/new',
    method: 'get',
    params
  })
}
