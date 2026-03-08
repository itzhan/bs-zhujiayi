import request from '@/utils/request'

export function getHotelReviews(hotelId: number, params: { page?: number; size?: number }) {
  return request({ url: `/api/reviews/hotel/${hotelId}`, method: 'get', params })
}

export function createReview(data: any) {
  return request({ url: '/api/reviews', method: 'post', data })
}

export function getMyReviews(params: { page?: number; size?: number }) {
  return request({ url: '/api/reviews/my', method: 'get', params })
}
