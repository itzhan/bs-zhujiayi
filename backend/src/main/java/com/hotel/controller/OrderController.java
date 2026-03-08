package com.hotel.controller;

import com.hotel.common.PageResult;
import com.hotel.common.Result;
import com.hotel.dto.OrderDTO;
import com.hotel.entity.BookingOrder;
import com.hotel.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Result<BookingOrder> createOrder(Authentication authentication, @RequestBody @Valid OrderDTO dto) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(orderService.createOrder(userId, dto));
    }

    @GetMapping
    public Result<PageResult<BookingOrder>> getUserOrders(
            Authentication authentication,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(orderService.getUserOrders(userId, page, size, status));
    }

    @GetMapping("/{id}")
    public Result<BookingOrder> getOrderDetail(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(orderService.getOrderDetail(userId, id));
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancelOrder(Authentication authentication, @PathVariable Long id,
                                     @RequestBody(required = false) Map<String, String> body) {
        Long userId = (Long) authentication.getPrincipal();
        String reason = body != null ? body.get("reason") : null;
        orderService.cancelOrder(userId, id, reason);
        return Result.success();
    }

    @PutMapping("/{id}/refund")
    public Result<Void> requestRefund(Authentication authentication, @PathVariable Long id,
                                       @RequestBody(required = false) Map<String, String> body) {
        Long userId = (Long) authentication.getPrincipal();
        String reason = body != null ? body.get("reason") : null;
        orderService.requestRefund(userId, id, reason);
        return Result.success();
    }
}
