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
@PreAuthorize("hasRole('STORE_ADMIN')")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // GET /api/store-admin/{storeId}/orders
    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders(
            @PathVariable Long storeId
    ) throws UserExceptions {
        return ResponseEntity.ok(orderService.getOrders(storeId));
    }

    // GET /api/store-admin/{storeId}/orders/{orderId}
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderById(
            @PathVariable Long storeId,
            @PathVariable Long orderId
    ) throws UserExceptions {
        return ResponseEntity.ok(orderService.getOrderById(storeId, orderId));
    }

    /**
     * POST /api/store-admin/{storeId}/orders
     * {
     *   "branchId": 1,
     *   "customerId": 5,
     *   "guestCustomerName": "John Doe",
     *   "guestCustomerPhone": "9876543210",
     *   "discount": 50.00,
     *   "tax": 18.00,
     *   "items": [
     *     { "productId": 2, "quantity": 3 },
     *     { "productId": 7, "quantity": 1, "unitPrice": 499.00 }
     *   ]
     * }
     */
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
            @PathVariable Long storeId,
            @RequestBody OrderDto.CreateRequest request
    ) throws UserExceptions {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(storeId, request));
    }

    /**
     * PUT /api/store-admin/{storeId}/orders/{orderId}/status?status=PROCESSING
     * Transitions: PENDING→PROCESSING→COMPLETED→REFUNDED
     *              PENDING/PROCESSING→CANCELLED (stock auto-restored)
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long storeId,
            @PathVariable Long orderId,
            @RequestParam String status
    ) throws UserExceptions {
        return ResponseEntity.ok(
                orderService.updateOrderStatus(storeId, orderId, status));
    }

    /**
     * PUT /api/store-admin/{storeId}/orders/{orderId}/cancel?reason=...
     * Stock is restored automatically via Inventory.quantity
     */
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDto> cancelOrder(
            @PathVariable Long storeId,
            @PathVariable Long orderId,
            @RequestParam(required = false) String reason
    ) throws UserExceptions {
        return ResponseEntity.ok(
                orderService.cancelOrder(storeId, orderId, reason));
    }
}