package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.common.PageResult;
import com.hotel.dto.ReviewDTO;
import com.hotel.entity.Review;

public interface ReviewService extends IService<Review> {
    void createReview(Long userId, ReviewDTO dto);
    PageResult<Review> getHotelReviews(Long hotelId, Integer page, Integer size);
    PageResult<Review> getUserReviews(Long userId, Integer page, Integer size);
    PageResult<Review> adminListReviews(Integer page, Integer size, Integer status, Long hotelId);
    void adminAuditReview(Long reviewId, Integer status);
    void adminReplyReview(Long reviewId, String reply);
}
