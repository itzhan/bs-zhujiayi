import request from '@/utils/request'

export function getAvailableCoupons() {
  return request({ url: '/api/coupons/available', method: 'get' })
}

export function receiveCoupon(id: number) {
  return request({ url: `/api/coupons/${id}/receive`, method: 'post' })
}

export function getMyCoupons(params?: { status?: string }) {
  return request({ url: '/api/coupons/my', method: 'get', params })
}
