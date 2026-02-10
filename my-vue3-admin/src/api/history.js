import request from '@/utils/request'

export const getHistoryList = (params) => {
  return request({
    url: '/history/list',
    method: 'get',
    params
  })
}

export const clearHistory = () => {
  return request({
    url: '/history/clear',
    method: 'delete'
  })
}

export const deleteHistory = (id) => {
  return request({
    url: `/history/${id}`,
    method: 'delete'
  })
}
