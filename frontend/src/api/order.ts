import request from '@/utils/request'

export function createOrder(data: any) {
  return request({ url: '/api/orders', method: 'post', data })
}

export function getMyOrders(params: { page?: number; size?: number; status?: string }) {
  return request({ url: '/api/orders', method: 'get', params })
}

export function getOrderDetail(id: number) {
  return request({ url: `/api/orders/${id}`, method: 'get' })
}

export function cancelOrder(id: number) {
  return request({ url: `/api/orders/${id}/cancel`, method: 'put' })
}

export function requestRefund(id: number) {
  return request({ url: `/api/orders/${id}/refund`, method: 'put' })
}
