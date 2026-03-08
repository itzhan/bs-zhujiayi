package com.hotel.controller;

import com.hotel.common.Result;
import com.hotel.dto.PasswordDTO;
import com.hotel.dto.UserUpdateDTO;
import com.hotel.entity.User;
import com.hotel.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public Result<User> getProfile(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(userService.getCurrentUser(userId));
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(Authentication authentication, @RequestBody UserUpdateDTO dto) {
        Long userId = (Long) authentication.getPrincipal();
        userService.updateProfile(userId, dto);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> changePassword(Authentication authentication, @RequestBody @Valid PasswordDTO dto) {
        Long userId = (Long) authentication.getPrincipal();
        userService.changePassword(userId, dto);
        return Result.success();
    }
}
