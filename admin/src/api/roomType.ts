import { http } from "@/utils/http";

/** 获取房型列表 */
export const getRoomTypeList = (params?: object) => {
  return http.request("get", "/api/admin/room-types", { params });
};

/** 创建房型 */
export const createRoomType = (data?: object) => {
  return http.request("post", "/api/admin/room-types", { data });
};

/** 更新房型 */
export const updateRoomType = (id: number, data?: object) => {
  return http.request("put", `/api/admin/room-types/${id}`, { data });
};

/** 删除房型 */
export const deleteRoomType = (id: number) => {
  return http.request("delete", `/api/admin/room-types/${id}`);
};
