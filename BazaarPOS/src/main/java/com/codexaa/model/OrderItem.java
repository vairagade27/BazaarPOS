package com.codexaa.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Product fields: id, name, brand, sku, price, quantity, store, category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** Snapshot of product.name at order time */
    private String productName;

    /** Snapshot of product.sku at order time */
    private String productSku;

    @Column(nullable = false)
    private Integer quantity;

    /** Snapshot of product.price at order time */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    /** (quantity × unitPrice) − discount */
    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal lineTotal = BigDecimal.ZERO;

    @PrePersist
    @PreUpdate
    public void computeLineTotal() {
        if (unitPrice != null && quantity != null) {
            BigDecimal gross = unitPrice.multiply(BigDecimal.valueOf(quantity));
            this.lineTotal = gross.subtract(
                    discount != null ? discount : BigDecimal.ZERO);
        }
    }
}