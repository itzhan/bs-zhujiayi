import { http } from "@/utils/http";

/** 获取评价列表 */
export const getReviewList = (params?: object) => {
  return http.request("get", "/api/admin/reviews", { params });
};

/** 审核评价 */
export const auditReview = (id: number, data?: object) => {
  return http.request("put", `/api/admin/reviews/${id}/audit`, { data });
};

/** 删除评价 */
export const deleteReview = (id: number) => {
  return http.request("delete", `/api/admin/reviews/${id}`);
};
