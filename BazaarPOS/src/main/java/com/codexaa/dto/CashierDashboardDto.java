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
public class CashierDashboardDto {

    private long       todayOrders;
    private long       pendingOrders;
    private long       completedOrders;
    private long       cancelledOrders;
    private BigDecimal todayRevenue;

    private long       lowStockItems;
    private long       outOfStockItems;

    private ShiftDto   activeShift;

    private List<TopProductDto> topProducts;

    private List<String> chartLabels;
    private List<Double> chartRevenue;
    private List<Long>   chartOrders;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProductDto {
        private String     productName;
        private int        quantitySold;
        private BigDecimal revenue;
    }
}