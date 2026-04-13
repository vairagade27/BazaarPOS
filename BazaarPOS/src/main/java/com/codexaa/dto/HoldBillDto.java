// ── HoldBillDto.java ──────────────────────────────────────────────────────────
package com.codexaa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoldBillDto {

    private String        holdId;          // UUID generated server-side
    private Long          storeId;
    private Long          cashierId;
    private String        label;           // e.g. "Table 3" or "Customer Priya"
    private BigDecimal    discount;
    private String        discountType;    // "PERCENT" or "FLAT"
    private String        notes;
    private LocalDateTime heldAt;
    private List<OrderItemDto.Request> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private String                     label;
        private BigDecimal                 discount;
        private String                     discountType;
        private String                     notes;
        private List<OrderItemDto.Request> items;
    }
}