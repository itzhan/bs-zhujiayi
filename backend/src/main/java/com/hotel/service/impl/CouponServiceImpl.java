package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.BusinessException;
import com.hotel.common.PageResult;
import com.hotel.dto.CouponDTO;
import com.hotel.entity.Coupon;
import com.hotel.entity.UserCoupon;
import com.hotel.mapper.CouponMapper;
import com.hotel.mapper.UserCouponMapper;
import com.hotel.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {

    private final UserCouponMapper userCouponMapper;

    @Override
    public List<Coupon> getAvailableCoupons() {
        return lambdaQuery()
                .eq(Coupon::getStatus, 1)
                .le(Coupon::getStartTime, LocalDateTime.now())
                .ge(Coupon::getEndTime, LocalDateTime.now())
                .orderByDesc(Coupon::getCreatedAt)
                .list();
    }

    @Override
    public void receiveCoupon(Long userId, Long couponId) {
        Coupon coupon = getById(couponId);
        if (coupon == null) throw new BusinessException(404, "优惠券不存在");
        if (coupon.getStatus() != 1) throw new BusinessException("优惠券已停用");
        if (coupon.getEndTime().isBefore(LocalDateTime.now())) throw new BusinessException("优惠券已过期");
        if (coupon.getTotalCount() > 0 && coupon.getUsedCount() >= coupon.getTotalCount()) {
            throw new BusinessException("优惠券已领完");
        }

        // 检查是否已领取
        Long count = new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getCouponId, couponId).hashCode() > 0 ?
                userCouponMapper.selectCount(new LambdaQueryWrapper<UserCoupon>()
                        .eq(UserCoupon::getUserId, userId)
                        .eq(UserCoupon::getCouponId, couponId)) : 0L;
        if (count > 0) throw new BusinessException("您已领取过此优惠券");

        UserCoupon uc = new UserCoupon();
        uc.setUserId(userId);
        uc.setCouponId(couponId);
        uc.setStatus(0);
        uc.setReceivedAt(LocalDateTime.now());
        userCouponMapper.insert(uc);
    }

    @Override
    public List<UserCoupon> getUserCoupons(Long userId, Integer status) {
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);
        if (status != null) wrapper.eq(UserCoupon::getStatus, status);
        wrapper.orderByDesc(UserCoupon::getReceivedAt);
        return userCouponMapper.selectList(wrapper);
    }

    @Override
    public PageResult<Coupon> adminListCoupons(Integer page, Integer size, Integer status) {
        Page<Coupon> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(Coupon::getStatus, status);
        wrapper.orderByDesc(Coupon::getCreatedAt);
        Page<Coupon> result = page(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public void createCoupon(CouponDTO dto) {
        Coupon coupon = new Coupon();
        copyProperties(dto, coupon);
        coupon.setUsedCount(0);
        coupon.setCreatedAt(LocalDateTime.now());
        coupon.setUpdatedAt(LocalDateTime.now());
        save(coupon);
    }

    @Override
    public void updateCoupon(Long id, CouponDTO dto) {
        Coupon coupon = getById(id);
        if (coupon == null) throw new BusinessException(404, "优惠券不存在");
        copyProperties(dto, coupon);
        coupon.setUpdatedAt(LocalDateTime.now());
        updateById(coupon);
    }

    @Override
    public void deleteCoupon(Long id) {
        if (getById(id) == null) throw new BusinessException(404, "优惠券不存在");
        removeById(id);
    }

    private void copyProperties(CouponDTO dto, Coupon entity) {
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setType(dto.getType());
        entity.setConditionAmount(dto.getConditionAmount());
        entity.setDiscountAmount(dto.getDiscountAmount());
        entity.setDiscountRate(dto.getDiscountRate());
        entity.setTotalCount(dto.getTotalCount() != null ? dto.getTotalCount() : 0);
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
    }
}
