package com.codexaa.service;

import com.codexaa.dto.InvoiceDto;
import com.codexaa.exception.UserExceptions;

import java.util.List;

public interface InvoiceService {

    List<InvoiceDto> getInvoices(Long storeId) throws UserExceptions;

    InvoiceDto getInvoiceById(Long storeId, Long invoiceId) throws UserExceptions;

    InvoiceDto getInvoiceByOrderId(Long storeId, Long orderId) throws UserExceptions;

    // Auto-generates a DRAFT invoice from an existing order
    InvoiceDto generateInvoice(Long storeId, Long orderId) throws UserExceptions;

    // DRAFT → ISSUED → PAID / OVERDUE / CANCELLED
    InvoiceDto updateInvoiceStatus(Long storeId, Long invoiceId, String status) throws UserExceptions;
}