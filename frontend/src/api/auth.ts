import request from '@/utils/request'

export function login(data: { username: string; password: string }) {
  return request({ url: '/api/auth/login', method: 'post', data })
}

export function register(data: {
  username: string
  password: string
  nickname: string
  phone: string
  email: string
}) {
  return request({ url: '/api/auth/register', method: 'post', data })
}

export function getUserProfile() {
  return request({ url: '/api/user/profile', method: 'get' })
}

export function updateUserProfile(data: any) {
  return request({ url: '/api/user/profile', method: 'put', data })
}

export function changePassword(data: { oldPassword: string; newPassword: string }) {
  return request({ url: '/api/user/password', method: 'put', data })
}
