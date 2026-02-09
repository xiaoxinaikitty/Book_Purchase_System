import request from '@/utils/request'

export const addFavorite = (bookId) => {
  return request({
    url: `/favorite/add/${bookId}`,
    method: 'post'
  })
}

export const removeFavorite = (bookId) => {
  return request({
    url: `/favorite/remove/${bookId}`,
    method: 'delete'
  })
}

export const getFavoriteList = (params) => {
  return request({
    url: '/favorite/list',
    method: 'get',
    params
  })
}

export const checkFavorite = (bookId) => {
  return request({
    url: `/favorite/check/${bookId}`,
    method: 'get'
  })
}
