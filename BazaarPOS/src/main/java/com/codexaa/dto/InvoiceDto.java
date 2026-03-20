package com.codexaa.dto;

import com.codexaa.domain.InvoiceStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoiceDto {

    private Long          id;
    private String        invoiceNumber;
    private InvoiceStatus status;

    // Linked order
    private Long          orderId;
    private String        orderNumber;

    // Store info — Store.brand is your name field
    private Long          storeId;
    private String        storeBrand;

    // Financials
    private BigDecimal    subtotal;
    private BigDecimal    discount;
    private BigDecimal    tax;
    private BigDecimal    totalAmount;

    // Customer snapshot — User.fullName or guest
    private String        customerName;
    private String        customerPhone;
    private String        billingAddress;

    // Store snapshot
    private String        storePhone;
    private String        storeAddress;

    // Line items from Order
    private List<OrderItemDto> items;

    private LocalDateTime dueDate;
    private String        notes;
    private LocalDateTime createdAt;
}