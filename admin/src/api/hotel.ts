import { http } from "@/utils/http";

/** 获取酒店列表 */
export const getHotelList = (params?: object) => {
  return http.request("get", "/api/admin/hotels", { params });
};

/** 创建酒店 */
export const createHotel = (data?: object) => {
  return http.request("post", "/api/admin/hotels", { data });
};

/** 更新酒店 */
export const updateHotel = (id: number, data?: object) => {
  return http.request("put", `/api/admin/hotels/${id}`, { data });
};

/** 删除酒店 */
export const deleteHotel = (id: number) => {
  return http.request("delete", `/api/admin/hotels/${id}`);
};

/** 更新酒店状态 */
export const updateHotelStatus = (id: number, status: number) => {
  return http.request("put", `/api/admin/hotels/${id}/status`, {
    data: { status }
  });
};
