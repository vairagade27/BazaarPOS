package com.codexaa.dto;

import com.codexaa.domain.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {

    // ── Response ──────────────────────────────────────────────────────────────
    private Long        id;
    private String      orderNumber;
    private OrderStatus status;

    private BigDecimal  subtotal;
    private BigDecimal  discount;
    private BigDecimal  tax;
    private BigDecimal  totalPrice;

    // Store — Store.brand is the name field in your Store entity
    private Long        storeId;
    private String      storeBrand;

    // Branch
    private Long        branchId;
    private String      branchName;

    // Customer — User.fullName matches your UserDto
    private Long        customerId;
    private String      customerName;   // from User.fullName or guestCustomerName
    private String      customerPhone;  // from User.phone or guestCustomerPhone

    private String      shippingAddress;
    private String      notes;
    private String      cancellationReason;

    private List<OrderItemDto> items;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ── Create Request ────────────────────────────────────────────────────────
    @Data
    public static class CreateRequest {
        private Long       branchId;            // optional — defaults to first active branch
        private Long       customerId;          // optional registered User
        private String     guestCustomerName;   // walk-in name
        private String     guestCustomerPhone;  // walk-in phone
        private String     shippingAddress;
        private String     notes;
        private BigDecimal discount;            // order-level discount, default 0
        private BigDecimal tax;                 // order-level tax, default 0
        private List<OrderItemDto.Request> items; // required, min 1
    }
}