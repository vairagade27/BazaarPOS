package com.codexaa.domain;

public enum InvoiceStatus {
    DRAFT,      // Auto-created when order is placed
    ISSUED,     // Sent/shown to customer
    PAID,       // Payment received
    OVERDUE,    // Past due date, unpaid
    CANCELLED   // Voided
}