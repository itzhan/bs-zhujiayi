import { http } from "@/utils/http";

/** 获取审计日志列表 */
export const getAuditLogList = (params?: object) => {
  return http.request("get", "/api/admin/audit-logs", { params });
};
