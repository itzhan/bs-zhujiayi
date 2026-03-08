import { http } from "@/utils/http";

/** 获取优惠券列表 */
export const getCouponList = (params?: object) => {
  return http.request("get", "/api/admin/coupons", { params });
};

/** 创建优惠券 */
export const createCoupon = (data?: object) => {
  return http.request("post", "/api/admin/coupons", { data });
};

/** 更新优惠券 */
export const updateCoupon = (id: number, data?: object) => {
  return http.request("put", `/api/admin/coupons/${id}`, { data });
};

/** 删除优惠券 */
export const deleteCoupon = (id: number) => {
  return http.request("delete", `/api/admin/coupons/${id}`);
};
