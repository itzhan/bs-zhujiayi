import { http } from "@/utils/http";

/** 获取订单列表 */
export const getOrderList = (params?: object) => {
  return http.request("get", "/api/admin/orders", { params });
};

/** 更新订单状态 */
export const updateOrderStatus = (id: number, status: number) => {
  return http.request("put", `/api/admin/orders/${id}/status`, {
    data: { status }
  });
};

/** 办理入住 */
export const checkIn = (id: number) => {
  return http.request("put", `/api/admin/orders/${id}/check-in`);
};

/** 办理退房 */
export const checkOut = (id: number) => {
  return http.request("put", `/api/admin/orders/${id}/check-out`);
};

/** 处理退款 */
export const processRefund = (id: number, data?: object) => {
  return http.request("put", `/api/admin/orders/${id}/refund`, { data });
};
