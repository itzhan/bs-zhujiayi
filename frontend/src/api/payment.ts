import request from '@/utils/request'

export function payOrder(data: { orderId: number; paymentMethod: string }) {
  return request({ url: '/api/payments', method: 'post', data })
}
