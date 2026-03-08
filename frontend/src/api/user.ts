import request from '@/utils/request'

export function uploadFile(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/api/file/upload',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function getAnnouncements() {
  return request({ url: '/api/announcements/published', method: 'get' })
}
