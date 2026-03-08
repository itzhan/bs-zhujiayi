package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.BusinessException;
import com.hotel.common.PageResult;
import com.hotel.dto.ReviewDTO;
import com.hotel.entity.BookingOrder;
import com.hotel.entity.Review;
import com.hotel.mapper.ReviewMapper;
import com.hotel.service.OrderService;
import com.hotel.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    private final OrderService orderService;

    @Override
    public void createReview(Long userId, ReviewDTO dto) {
        BookingOrder order = orderService.getById(dto.getOrderId());
        if (order == null) throw new BusinessException(404, "订单不存在");
        if (!order.getUserId().equals(userId)) throw new BusinessException(403, "无权评价此订单");
        if (order.getStatus() != 3) throw new BusinessException("只有已完成的订单才能评价");

        // 检查是否已评价
        if (lambdaQuery().eq(Review::getOrderId, dto.getOrderId()).count() > 0) {
            throw new BusinessException("该订单已评价");
        }

        Review review = new Review();
        review.setUserId(userId);
        review.setHotelId(order.getHotelId());
        review.setOrderId(order.getId());
        review.setRoomTypeId(order.getRoomTypeId());
        review.setRating(dto.getRating());
        review.setContent(dto.getContent());
        review.setImages(dto.getImages());
        review.setStatus(0); // 待审核
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        save(review);
    }

    @Override
    public PageResult<Review> getHotelReviews(Long hotelId, Integer page, Integer size) {
        Page<Review> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getHotelId, hotelId)
                .eq(Review::getStatus, 1)
                .orderByDesc(Review::getCreatedAt);
        Page<Review> result = page(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public PageResult<Review> getUserReviews(Long userId, Integer page, Integer size) {
        Page<Review> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getUserId, userId).orderByDesc(Review::getCreatedAt);
        Page<Review> result = page(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public PageResult<Review> adminListReviews(Integer page, Integer size, Integer status, Long hotelId) {
        Page<Review> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(Review::getStatus, status);
        if (hotelId != null) wrapper.eq(Review::getHotelId, hotelId);
        wrapper.orderByDesc(Review::getCreatedAt);
        Page<Review> result = page(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public void adminAuditReview(Long reviewId, Integer status) {
        Review review = getById(reviewId);
        if (review == null) throw new BusinessException(404, "评价不存在");
        review.setStatus(status);
        review.setUpdatedAt(LocalDateTime.now());
        updateById(review);
    }

    @Override
    public void adminReplyReview(Long reviewId, String reply) {
        Review review = getById(reviewId);
        if (review == null) throw new BusinessException(404, "评价不存在");
        review.setReply(reply);
        review.setReplyAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        updateById(review);
    }
}
