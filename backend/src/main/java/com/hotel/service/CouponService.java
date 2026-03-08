package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.common.PageResult;
import com.hotel.dto.CouponDTO;
import com.hotel.entity.Coupon;
import com.hotel.entity.UserCoupon;

import java.util.List;

public interface CouponService extends IService<Coupon> {
    List<Coupon> getAvailableCoupons();
    void receiveCoupon(Long userId, Long couponId);
    List<UserCoupon> getUserCoupons(Long userId, Integer status);
    PageResult<Coupon> adminListCoupons(Integer page, Integer size, Integer status);
    void createCoupon(CouponDTO dto);
    void updateCoupon(Long id, CouponDTO dto);
    void deleteCoupon(Long id);
}
