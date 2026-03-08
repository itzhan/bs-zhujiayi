package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.BusinessException;
import com.hotel.common.PageResult;
import com.hotel.dto.LoginDTO;
import com.hotel.dto.PasswordDTO;
import com.hotel.dto.RegisterDTO;
import com.hotel.dto.UserUpdateDTO;
import com.hotel.entity.User;
import com.hotel.mapper.UserMapper;
import com.hotel.security.JwtTokenProvider;
import com.hotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Map<String, Object> login(LoginDTO dto) {
        User user = lambdaQuery().eq(User::getUsername, dto.getUsername()).one();
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(403, "账号已被禁用");
        }
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", sanitizeUser(user));
        return result;
    }

    @Override
    public void register(RegisterDTO dto) {
        if (lambdaQuery().eq(User::getUsername, dto.getUsername()).count() > 0) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(0);
        user.setRole(0);
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        save(user);
    }

    @Override
    public User getCurrentUser(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return sanitizeUser(user);
    }

    @Override
    public void updateProfile(Long userId, UserUpdateDTO dto) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getAvatar() != null) user.setAvatar(dto.getAvatar());
        if (dto.getGender() != null) user.setGender(dto.getGender());
        user.setUpdatedAt(LocalDateTime.now());
        updateById(user);
    }

    @Override
    public void changePassword(Long userId, PasswordDTO dto) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        updateById(user);
    }

    @Override
    public PageResult<User> listUsers(Integer page, Integer size, String keyword, Integer role, Integer status) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword));
        }
        if (role != null) wrapper.eq(User::getRole, role);
        if (status != null) wrapper.eq(User::getStatus, status);
        wrapper.orderByDesc(User::getCreatedAt);
        Page<User> result = page(pageParam, wrapper);
        result.getRecords().forEach(u -> u.setPassword(null));
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        User user = getById(userId);
        if (user == null) throw new BusinessException(404, "用户不存在");
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        updateById(user);
    }

    @Override
    public void resetPassword(Long userId) {
        User user = getById(userId);
        if (user == null) throw new BusinessException(404, "用户不存在");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setUpdatedAt(LocalDateTime.now());
        updateById(user);
    }

    private User sanitizeUser(User user) {
        user.setPassword(null);
        return user;
    }
}
