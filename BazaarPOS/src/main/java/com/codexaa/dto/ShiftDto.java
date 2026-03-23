package com.codexaa.dto;

import com.codexaa.domain.ShiftStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ShiftDto {

    // ── Response fields ───────────────────────────────────────────────────────
    private Long          id;
    private String        shiftNumber;
    private ShiftStatus   status;

    private Long          cashierId;
    private String        cashierName;

    private Long          branchId;
    private String        branchName;

    private Long          storeId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private BigDecimal    totalRevenue;
    private BigDecimal    totalDiscount;
    private BigDecimal    totalTax;
    private BigDecimal    totalRefunds;
    private BigDecimal    netSales;

    private long          totalOrders;
    private long          completedOrders;
    private long          cancelledOrders;
    private long          refundedOrders;

    private String        notes;
    private LocalDateTime createdAt;

    // ── Start request ─────────────────────────────────────────────────────────
    @Data
    public static class StartRequest {
        private Long   branchId;   // required
        private String notes;      // optional
    }

    // ── Close request ─────────────────────────────────────────────────────────
    @Data
    public static class CloseRequest {
        private String notes;      // optional closing notes
    }
}