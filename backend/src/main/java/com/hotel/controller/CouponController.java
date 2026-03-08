package com.hotel.controller;

import com.hotel.common.Result;
import com.hotel.entity.Coupon;
import com.hotel.entity.UserCoupon;
import com.hotel.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/available")
    public Result<List<Coupon>> getAvailableCoupons() {
        return Result.success(couponService.getAvailableCoupons());
    }

    @PostMapping("/{couponId}/receive")
    public Result<Void> receiveCoupon(Authentication authentication, @PathVariable Long couponId) {
        Long userId = (Long) authentication.getPrincipal();
        couponService.receiveCoupon(userId, couponId);
        return Result.success();
    }

    @GetMapping("/my")
    public Result<List<UserCoupon>> getMyCoupons(
            Authentication authentication,
            @RequestParam(required = false) Integer status) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(couponService.getUserCoupons(userId, status));
    }
}
