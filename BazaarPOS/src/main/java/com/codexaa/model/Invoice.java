package com.codexaa.model;

import com.codexaa.domain.InvoiceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    // One order → one invoice
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Store — uses Store.brand as store name
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    // ── Financial snapshot (copied from Order at generation time) ─────────────

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    // ── Customer snapshot (from User.fullName or guest fields) ────────────────

    private String customerName;    // User.fullName or guestCustomerName
    private String customerPhone;   // User.phone or guestCustomerPhone
    private String billingAddress;  // Order.shippingAddress

    // ── Store snapshot (from Store.brand and StoreContact) ────────────────────

    private String storeBrand;      // Store.brand
    private String storePhone;      // Store.contact.phone (if StoreContact has phone)
    private String storeAddress;    // Store.contact.address (if StoreContact has address)

    private LocalDateTime dueDate;

    private String notes;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}