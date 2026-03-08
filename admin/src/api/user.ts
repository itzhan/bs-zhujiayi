import { http } from "@/utils/http";

export type UserResult = {
  code: number;
  message: string;
  data: {
    token: string;
    user: {
      id: number;
      username: string;
      nickname: string;
      role: number;
      avatar: string;
    };
    roles: string[];
    permissions: string[];
    accessToken: string;
    refreshToken: string;
    expires: Date;
  };
};

export type RefreshTokenResult = {
  code: number;
  message: string;
  data: {
    accessToken: string;
    refreshToken: string;
    expires: Date;
  };
};

export type UserInfo = {
  avatar: string;
  username: string;
  nickname: string;
  email: string;
  phone: string;
  description: string;
};

export type UserInfoResult = {
  code: number;
  message: string;
  data: UserInfo;
};

type ResultTable = {
  code: number;
  message: string;
  data?: {
    list: Array<any>;
    total?: number;
    pageSize?: number;
    currentPage?: number;
  };
};

/** 登录 */
export const getLogin = (data?: object) => {
  return http.request<UserResult>("post", "/api/auth/login", { data });
};

/** 刷新token */
export const refreshTokenApi = (data?: object) => {
  return http.request<RefreshTokenResult>("post", "/api/auth/refresh-token", {
    data
  });
};

/** 账户设置-个人信息 */
export const getMine = (data?: object) => {
  return http.request<UserInfoResult>("get", "/api/user/profile", { data });
};

/** 账户设置-个人安全日志 */
export const getMineLogs = (data?: object) => {
  return http.request<ResultTable>("get", "/api/admin/audit-logs", { data });
};
