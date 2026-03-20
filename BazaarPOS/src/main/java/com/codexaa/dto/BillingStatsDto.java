package com.codexaa.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingStatsDto {

    // ── Revenue ───────────────────────────────────────────────────────────────
    private BigDecimal totalRevenue;          // all COMPLETED orders
    private BigDecimal todayRevenue;          // today's completed revenue
    private BigDecimal weekRevenue;           // last 7 days
    private BigDecimal monthRevenue;          // current calendar month

    // ── Orders ────────────────────────────────────────────────────────────────
    private long totalOrders;
    private long pendingOrders;
    private long processingOrders;
    private long completedOrders;
    private long cancelledOrders;
    private long refundedOrders;

    // ── Averages ──────────────────────────────────────────────────────────────
    private BigDecimal avgOrderValue;
    private BigDecimal avgDailyRevenue;       // weekRevenue / 7

    // ── Invoices ──────────────────────────────────────────────────────────────
    private long totalInvoices;
    private long draftInvoices;
    private long issuedInvoices;
    private long paidInvoices;
    private long overdueInvoices;

    // ── Top products (by quantity sold) ───────────────────────────────────────
    private List<TopProductDto> topProducts;

    // ── Chart data (last 7 days) ───────────────────────────────────────────────
    private List<String> chartLabels;         // ["Mon", "Tue", ...]
    private List<Double> chartRevenue;        // daily revenue
    private List<Long>   chartOrders;         // daily order count

    // ── Inner DTO ─────────────────────────────────────────────────────────────
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopProductDto {
        private Long       productId;
        private String     productName;
        private Integer    totalQuantitySold;
        private BigDecimal totalRevenue;
    }
}