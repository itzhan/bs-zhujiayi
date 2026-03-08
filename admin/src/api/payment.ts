import { http } from "@/utils/http";

/** 获取支付记录列表 */
export const getPaymentList = (params?: object) => {
  return http.request("get", "/api/admin/payments", { params });
};
