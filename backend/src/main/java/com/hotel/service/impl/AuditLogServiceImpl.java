package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.PageResult;
import com.hotel.entity.AuditLog;
import com.hotel.mapper.AuditLogMapper;
import com.hotel.service.AuditLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class AuditLogServiceImpl extends ServiceImpl<AuditLogMapper, AuditLog> implements AuditLogService {

    @Override
    public void log(Long userId, String username, String action, String target, String detail, String ip) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setAction(action);
        log.setTarget(target);
        log.setDetail(detail);
        log.setIp(ip);
        log.setCreatedAt(LocalDateTime.now());
        save(log);
    }

    @Override
    public PageResult<AuditLog> adminListLogs(Integer page, Integer size, String action, String username) {
        Page<AuditLog> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AuditLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(action)) wrapper.like(AuditLog::getAction, action);
        if (StringUtils.hasText(username)) wrapper.like(AuditLog::getUsername, username);
        wrapper.orderByDesc(AuditLog::getCreatedAt);
        Page<AuditLog> result = page(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
}
