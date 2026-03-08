import { http } from "@/utils/http";

/** 获取公告列表 */
export const getAnnouncementList = (params?: object) => {
  return http.request("get", "/api/admin/announcements", { params });
};

/** 创建公告 */
export const createAnnouncement = (data?: object) => {
  return http.request("post", "/api/admin/announcements", { data });
};

/** 更新公告 */
export const updateAnnouncement = (id: number, data?: object) => {
  return http.request("put", `/api/admin/announcements/${id}`, { data });
};

/** 删除公告 */
export const deleteAnnouncement = (id: number) => {
  return http.request("delete", `/api/admin/announcements/${id}`);
};
