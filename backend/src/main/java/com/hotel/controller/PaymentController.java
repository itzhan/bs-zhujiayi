package com.hotel.controller;

import com.hotel.common.Result;
import com.hotel.dto.PaymentDTO;
import com.hotel.entity.Payment;
import com.hotel.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public Result<Payment> pay(Authentication authentication, @RequestBody @Valid PaymentDTO dto) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(paymentService.pay(userId, dto));
    }

    @GetMapping("/order/{orderId}")
    public Result<Payment> getByOrderId(@PathVariable Long orderId) {
        return Result.success(paymentService.getByOrderId(orderId));
    }
}
