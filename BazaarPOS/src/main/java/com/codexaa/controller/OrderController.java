package com.codexaa.controller;

import com.codexaa.dto.OrderDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/store-admin/{storeId}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER','BRANCH_MANAGER','CASHIER')")
    public ResponseEntity<List<OrderDto>> getOrders(
            @PathVariable Long storeId) throws UserExceptions {
        return ResponseEntity.ok(orderService.getOrders(storeId));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER','BRANCH_MANAGER','CASHIER')")
    public ResponseEntity<OrderDto> getOrderById(
            @PathVariable Long storeId,
            @PathVariable Long orderId) throws UserExceptions {
        return ResponseEntity.ok(orderService.getOrderById(storeId, orderId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER','BRANCH_MANAGER','CASHIER')")
    public ResponseEntity<OrderDto> createOrder(
            @PathVariable Long storeId,
            @RequestBody OrderDto.CreateRequest request) throws UserExceptions {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(storeId, request));
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER','BRANCH_MANAGER')")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long storeId,
            @PathVariable Long orderId,
            @RequestParam String status) throws UserExceptions {
        return ResponseEntity.ok(orderService.updateOrderStatus(storeId, orderId, status));
    }

    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER','BRANCH_MANAGER')")
    public ResponseEntity<OrderDto> cancelOrder(
            @PathVariable Long storeId,
            @PathVariable Long orderId,
            @RequestParam(required = false) String reason) throws UserExceptions {
        return ResponseEntity.ok(orderService.cancelOrder(storeId, orderId, reason));
    }
}