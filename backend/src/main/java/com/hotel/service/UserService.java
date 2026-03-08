package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.common.PageResult;
import com.hotel.dto.LoginDTO;
import com.hotel.dto.PasswordDTO;
import com.hotel.dto.RegisterDTO;
import com.hotel.dto.UserUpdateDTO;
import com.hotel.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {
    Map<String, Object> login(LoginDTO dto);
    void register(RegisterDTO dto);
    User getCurrentUser(Long userId);
    void updateProfile(Long userId, UserUpdateDTO dto);
    void changePassword(Long userId, PasswordDTO dto);
    PageResult<User> listUsers(Integer page, Integer size, String keyword, Integer role, Integer status);
    void updateUserStatus(Long userId, Integer status);
    void resetPassword(Long userId);
}
