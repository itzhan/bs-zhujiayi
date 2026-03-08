package com.hotel.controller;

import com.hotel.common.PageResult;
import com.hotel.common.Result;
import com.hotel.dto.ReviewDTO;
import com.hotel.entity.Review;
import com.hotel.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Result<Void> createReview(Authentication authentication, @RequestBody @Valid ReviewDTO dto) {
        Long userId = (Long) authentication.getPrincipal();
        reviewService.createReview(userId, dto);
        return Result.success();
    }

    @GetMapping("/hotel/{hotelId}")
    public Result<PageResult<Review>> getHotelReviews(
            @PathVariable Long hotelId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(reviewService.getHotelReviews(hotelId, page, size));
    }

    @GetMapping("/my")
    public Result<PageResult<Review>> getMyReviews(
            Authentication authentication,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(reviewService.getUserReviews(userId, page, size));
    }
}
