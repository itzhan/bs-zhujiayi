import request from '@/utils/request'

export function searchHotels(params: {
  page?: number
  size?: number
  keyword?: string
  city?: string
  starRating?: number
  minPrice?: number
  maxPrice?: number
  sortBy?: string
}) {
  return request({ url: '/api/hotels', method: 'get', params })
}

export function getHotelDetail(id: number) {
  return request({ url: `/api/hotels/${id}`, method: 'get' })
}

export function getHotelRoomTypes(hotelId: number) {
  return request({ url: `/api/hotels/${hotelId}/room-types`, method: 'get' })
}

export function getRoomTypeDetail(id: number) {
  return request({ url: `/api/room-types/${id}`, method: 'get' })
}

export function getRoomInventory(roomTypeId: number, params: { startDate: string; endDate: string }) {
  return request({ url: `/api/room-types/${roomTypeId}/inventory`, method: 'get', params })
}
