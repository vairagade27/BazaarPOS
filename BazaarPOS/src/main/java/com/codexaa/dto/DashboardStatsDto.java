package com.codexaa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {

    // ── Super Admin fields ────────────────────────────────────────────────────
    private long totalStores;
    private long totalUsers;

    // ── Revenue ───────────────────────────────────────────────────────────────
    private BigDecimal totalRevenue;
    private BigDecimal todayRevenue;
    private BigDecimal weekRevenue;
    private BigDecimal monthRevenue;
    private BigDecimal avgOrderValue;

    // ── Orders ────────────────────────────────────────────────────────────────
    private long totalOrders;
    private long pendingOrders;
    private long processingOrders;
    private long completedOrders;
    private long cancelledOrders;

    // ── Store Admin fields ────────────────────────────────────────────────────
    private long totalBranches;
    private long totalProducts;
    private long totalEmployees;
    private long totalCustomers;
    private long lowStockItems;

    // ── Chart data (last 7 days) ──────────────────────────────────────────────
    private List<String> chartLabels;
    private List<Double> chartRevenue;
    private List<Long>   chartOrders;
}