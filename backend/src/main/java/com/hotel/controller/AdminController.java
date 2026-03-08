package com.hotel.controller;

import com.hotel.common.PageResult;
import com.hotel.common.Result;
import com.hotel.dto.*;
import com.hotel.entity.*;
import com.hotel.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final HotelService hotelService;
    private final RoomTypeService roomTypeService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final ReviewService reviewService;
    private final CouponService couponService;
    private final AnnouncementService announcementService;
    private final AuditLogService auditLogService;
    private final StatisticsService statisticsService;

    // ==================== 用户管理 ====================
    @GetMapping("/users")
    public Result<PageResult<User>> listUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) Integer status) {
        return Result.success(userService.listUsers(page, size, keyword, role, status));
    }

    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        userService.updateUserStatus(id, body.get("status"));
        return Result.success();
    }

    @PutMapping("/users/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.success();
    }

    // ==================== 酒店管理 ====================
    @GetMapping("/hotels")
    public Result<PageResult<Hotel>> listHotels(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(hotelService.adminListHotels(page, size, keyword, status));
    }

    @PostMapping("/hotels")
    public Result<Void> createHotel(@RequestBody @Valid HotelDTO dto) {
        hotelService.createHotel(dto);
        return Result.success();
    }

    @PutMapping("/hotels/{id}")
    public Result<Void> updateHotel(@PathVariable Long id, @RequestBody @Valid HotelDTO dto) {
        hotelService.updateHotel(id, dto);
        return Result.success();
    }

    @DeleteMapping("/hotels/{id}")
    public Result<Void> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return Result.success();
    }

    @PutMapping("/hotels/{id}/status")
    public Result<Void> updateHotelStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        hotelService.updateHotelStatus(id, body.get("status"));
        return Result.success();
    }

    // ==================== 房型管理 ====================
    @GetMapping("/room-types")
    public Result<PageResult<RoomType>> listRoomTypes(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(roomTypeService.adminListRoomTypes(page, size, hotelId, keyword, status));
    }

    @PostMapping("/room-types")
    public Result<Void> createRoomType(@RequestBody @Valid RoomTypeDTO dto) {
        roomTypeService.createRoomType(dto);
        return Result.success();
    }

    @PutMapping("/room-types/{id}")
    public Result<Void> updateRoomType(@PathVariable Long id, @RequestBody @Valid RoomTypeDTO dto) {
        roomTypeService.updateRoomType(id, dto);
        return Result.success();
    }

    @DeleteMapping("/room-types/{id}")
    public Result<Void> deleteRoomType(@PathVariable Long id) {
        roomTypeService.deleteRoomType(id);
        return Result.success();
    }

    // ==================== 订单管理 ====================
    @GetMapping("/orders")
    public Result<PageResult<BookingOrder>> listOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        return Result.success(orderService.adminListOrders(page, size, orderNo, status, keyword));
    }

    @PutMapping("/orders/{id}/status")
    public Result<Void> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        orderService.adminUpdateOrderStatus(id, body.get("status"));
        return Result.success();
    }

    @PutMapping("/orders/{id}/check-in")
    public Result<Void> checkIn(@PathVariable Long id) {
        orderService.checkIn(id);
        return Result.success();
    }

    @PutMapping("/orders/{id}/check-out")
    public Result<Void> checkOut(@PathVariable Long id) {
        orderService.checkOut(id);
        return Result.success();
    }

    @PutMapping("/orders/{id}/refund")
    public Result<Void> processRefund(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        boolean approved = (boolean) body.get("approved");
        String reason = (String) body.get("reason");
        orderService.adminProcessRefund(id, approved, reason);
        if (approved) {
            paymentService.refund(id, reason);
        }
        return Result.success();
    }

    // ==================== 支付管理 ====================
    @GetMapping("/payments")
    public Result<PageResult<Payment>> listPayments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String paymentNo,
            @RequestParam(required = false) Integer status) {
        return Result.success(paymentService.adminListPayments(page, size, paymentNo, status));
    }

    // ==================== 评价管理 ====================
    @GetMapping("/reviews")
    public Result<PageResult<Review>> listReviews(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long hotelId) {
        return Result.success(reviewService.adminListReviews(page, size, status, hotelId));
    }

    @PutMapping("/reviews/{id}/audit")
    public Result<Void> auditReview(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        reviewService.adminAuditReview(id, body.get("status"));
        return Result.success();
    }

    @PutMapping("/reviews/{id}/reply")
    public Result<Void> replyReview(@PathVariable Long id, @RequestBody Map<String, String> body) {
        reviewService.adminReplyReview(id, body.get("reply"));
        return Result.success();
    }

    // ==================== 优惠券管理 ====================
    @GetMapping("/coupons")
    public Result<PageResult<Coupon>> listCoupons(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        return Result.success(couponService.adminListCoupons(page, size, status));
    }

    @PostMapping("/coupons")
    public Result<Void> createCoupon(@RequestBody @Valid CouponDTO dto) {
        couponService.createCoupon(dto);
        return Result.success();
    }

    @PutMapping("/coupons/{id}")
    public Result<Void> updateCoupon(@PathVariable Long id, @RequestBody @Valid CouponDTO dto) {
        couponService.updateCoupon(id, dto);
        return Result.success();
    }

    @DeleteMapping("/coupons/{id}")
    public Result<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return Result.success();
    }

    // ==================== 公告管理 ====================
    @GetMapping("/announcements")
    public Result<PageResult<Announcement>> listAnnouncements(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        return Result.success(announcementService.adminListAnnouncements(page, size, type, status));
    }

    @PostMapping("/announcements")
    public Result<Void> createAnnouncement(@RequestBody @Valid AnnouncementDTO dto) {
        announcementService.createAnnouncement(dto);
        return Result.success();
    }

    @PutMapping("/announcements/{id}")
    public Result<Void> updateAnnouncement(@PathVariable Long id, @RequestBody @Valid AnnouncementDTO dto) {
        announcementService.updateAnnouncement(id, dto);
        return Result.success();
    }

    @DeleteMapping("/announcements/{id}")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return Result.success();
    }

    @PutMapping("/announcements/{id}/publish")
    public Result<Void> publishAnnouncement(@PathVariable Long id) {
        announcementService.publishAnnouncement(id);
        return Result.success();
    }

    // ==================== 审计日志 ====================
    @GetMapping("/audit-logs")
    public Result<PageResult<AuditLog>> listAuditLogs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String username) {
        return Result.success(auditLogService.adminListLogs(page, size, action, username));
    }

    // ==================== 统计数据 ====================
    @GetMapping("/statistics/dashboard")
    public Result<Map<String, Object>> getDashboardStats() {
        return Result.success(statisticsService.getDashboardStats());
    }

    @GetMapping("/statistics/revenue")
    public Result<Map<String, Object>> getRevenueStats(
            @RequestParam(defaultValue = "2025-01-01") String startDate) {
        return Result.success(statisticsService.getRevenueStats(startDate));
    }

    @GetMapping("/statistics/hotel-ranking")
    public Result<Map<String, Object>> getHotelRanking() {
        return Result.success(statisticsService.getHotelRanking());
    }
}
