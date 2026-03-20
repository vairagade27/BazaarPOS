package com.codexaa.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {

    // ── Response ──────────────────────────────────────────────────────────────
    private Long       id;
    private Long       productId;
    private String     productName;
    private String     productSku;
    private Integer    quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal lineTotal;

    // ── Request (used when creating an order) ─────────────────────────────────
    @Data
    public static class Request {
        private Long       productId;   // required
        private Integer    quantity;    // required, > 0
        private BigDecimal unitPrice;   // optional — defaults to product.price
        private BigDecimal discount;    // optional — defaults to 0
    }
}