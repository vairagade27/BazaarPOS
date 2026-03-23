package com.codexaa.model;

import com.codexaa.domain.ShiftStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shifts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String shiftNumber;          // e.g. SHF-1748293920000

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_id", nullable = false)
    private User cashier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ShiftStatus status = ShiftStatus.OPEN;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    // ── Totals (populated on shift close) ────────────────────────────────────

    @Builder.Default
    private BigDecimal totalRevenue    = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal totalDiscount   = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal totalTax        = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal totalRefunds    = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal netSales        = BigDecimal.ZERO;

    @Builder.Default
    private long totalOrders           = 0;

    @Builder.Default
    private long completedOrders       = 0;

    @Builder.Default
    private long cancelledOrders       = 0;

    @Builder.Default
    private long refundedOrders        = 0;

    private String notes;

    @CreationTimestamp
    private LocalDateTime createdAt;
}