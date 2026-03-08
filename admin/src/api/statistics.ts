import { http } from "@/utils/http";

/** 获取仪表盘统计数据 */
export const getDashboardStats = (params?: object) => {
  return http.request("get", "/api/admin/statistics/dashboard", { params });
};

/** 获取营收统计 */
export const getRevenueStats = (params?: object) => {
  return http.request("get", "/api/admin/statistics/revenue", { params });
};

/** 获取酒店排名 */
export const getHotelRanking = (params?: object) => {
  return http.request("get", "/api/admin/statistics/hotel-ranking", { params });
};
