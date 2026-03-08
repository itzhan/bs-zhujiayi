import request from '@/utils/request'

export function toggleFavorite(hotelId: number) {
  return request({ url: `/api/favorites/${hotelId}`, method: 'post' })
}

export function checkFavorite(hotelId: number) {
  return request({ url: `/api/favorites/check/${hotelId}`, method: 'get' })
}

export function getMyFavorites() {
  return request({ url: '/api/favorites', method: 'get' })
}
