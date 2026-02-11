import request from '@/utils/request'

export const uploadFile = (data) => {
  return request({
    url: '/file/upload',
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const uploadCover = (data) => {
  return request({
    url: '/file/upload/cover',
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const uploadAvatar = (data) => {
  return request({
    url: '/file/upload/avatar',
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
