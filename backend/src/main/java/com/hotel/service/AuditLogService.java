package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.common.PageResult;
import com.hotel.entity.AuditLog;

public interface AuditLogService extends IService<AuditLog> {
    void log(Long userId, String username, String action, String target, String detail, String ip);
    PageResult<AuditLog> adminListLogs(Integer page, Integer size, String action, String username);
}
