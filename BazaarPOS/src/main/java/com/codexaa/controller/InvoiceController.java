package com.codexaa.controller;

import com.codexaa.dto.InvoiceDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/store-admin/{storeId}")
@PreAuthorize("hasRole('STORE_ADMIN')")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    // ── GET ALL INVOICES ──────────────────────────────────────────────────────
    // GET /api/store-admin/{storeId}/invoices
    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceDto>> getInvoices(
            @PathVariable Long storeId
    ) throws UserExceptions {
        return ResponseEntity.ok(invoiceService.getInvoices(storeId));
    }

    // ── GET INVOICE BY ID ─────────────────────────────────────────────────────
    // GET /api/store-admin/{storeId}/invoices/{invoiceId}
    @GetMapping("/invoices/{invoiceId}")
    public ResponseEntity<InvoiceDto> getInvoiceById(
            @PathVariable Long storeId,
            @PathVariable Long invoiceId
    ) throws UserExceptions {
        return ResponseEntity.ok(invoiceService.getInvoiceById(storeId, invoiceId));
    }

    // ── GET INVOICE BY ORDER ──────────────────────────────────────────────────
    // GET /api/store-admin/{storeId}/orders/{orderId}/invoice
    @GetMapping("/orders/{orderId}/invoice")
    public ResponseEntity<InvoiceDto> getInvoiceByOrder(
            @PathVariable Long storeId,
            @PathVariable Long orderId
    ) throws UserExceptions {
        return ResponseEntity.ok(invoiceService.getInvoiceByOrderId(storeId, orderId));
    }

    // ── GENERATE INVOICE ──────────────────────────────────────────────────────
    // POST /api/store-admin/{storeId}/orders/{orderId}/invoice/generate
    // Generates an invoice from a placed order.
    // If invoice already exists → returns existing one (no duplicate).
    @PostMapping("/orders/{orderId}/invoice/generate")
    public ResponseEntity<InvoiceDto> generateInvoice(
            @PathVariable Long storeId,
            @PathVariable Long orderId
    ) throws UserExceptions {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(invoiceService.generateInvoice(storeId, orderId));
    }

    // ── UPDATE INVOICE STATUS ─────────────────────────────────────────────────
    // PUT /api/store-admin/{storeId}/invoices/{invoiceId}/status?status=ISSUED
    //
    // Valid transitions:
    //   DRAFT   → ISSUED | CANCELLED
    //   ISSUED  → PAID   | OVERDUE | CANCELLED
    //   OVERDUE → PAID   | CANCELLED
    //   PAID    → (terminal)
    //   CANCELLED → (terminal)
    @PutMapping("/invoices/{invoiceId}/status")
    public ResponseEntity<InvoiceDto> updateInvoiceStatus(
            @PathVariable Long storeId,
            @PathVariable Long invoiceId,
            @RequestParam String status
    ) throws UserExceptions {
        return ResponseEntity.ok(
                invoiceService.updateInvoiceStatus(storeId, invoiceId, status));
    }
}