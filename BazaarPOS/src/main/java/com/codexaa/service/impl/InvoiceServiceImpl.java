package com.codexaa.service.impl;

import com.codexaa.domain.InvoiceStatus;
import com.codexaa.dto.InvoiceDto;
import com.codexaa.dto.OrderItemDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.model.*;
import com.codexaa.repository.*;
import com.codexaa.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OrderRepository   orderRepository;
    private final StoreRepository   storeRepository;

    // ══════════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════════════════

    private Store requireStore(Long storeId) throws UserExceptions {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new UserExceptions.NotFoundException(
                        "Store not found: " + storeId));
    }

    private Invoice requireInvoice(Long storeId, Long invoiceId) throws UserExceptions {
        return invoiceRepository.findByIdAndStoreId(invoiceId, storeId)
                .orElseThrow(() -> new UserExceptions.NotFoundException(
                        "Invoice " + invoiceId + " not found for store " + storeId));
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  GET ALL INVOICES
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDto> getInvoices(Long storeId) throws UserExceptions {
        requireStore(storeId);
        return invoiceRepository.findByStoreIdOrderByCreatedAtDesc(storeId)
                .stream()
                .map(this::toInvoiceDto)
                .collect(Collectors.toList());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  GET INVOICE BY ID
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public InvoiceDto getInvoiceById(Long storeId, Long invoiceId) throws UserExceptions {
        return toInvoiceDto(requireInvoice(storeId, invoiceId));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  GET INVOICE BY ORDER ID
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public InvoiceDto getInvoiceByOrderId(Long storeId, Long orderId) throws UserExceptions {
        requireStore(storeId);
        return invoiceRepository.findByOrderId(orderId)
                .map(this::toInvoiceDto)
                .orElseThrow(() -> new UserExceptions.NotFoundException(
                        "No invoice found for order: " + orderId));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  GENERATE INVOICE FROM ORDER
    //  - If invoice already exists for this order → return existing
    //  - Uses User.fullName for customerName (matches your UserDto)
    //  - Uses Store.brand for storeBrand (matches your Store entity)
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    public InvoiceDto generateInvoice(Long storeId, Long orderId) throws UserExceptions {
        Store store = requireStore(storeId);

        Order order = orderRepository.findByIdAndStoreId(orderId, storeId)
                .orElseThrow(() -> new UserExceptions.NotFoundException(
                        "Order " + orderId + " not found for store " + storeId));

        // If invoice already exists → return it
        if (invoiceRepository.existsByOrderId(orderId)) {
            return toInvoiceDto(invoiceRepository.findByOrderId(orderId).get());
        }

        // Resolve customer name — User.fullName or guestCustomerName
        String customerName;
        String customerPhone;
        if (order.getCustomer() != null) {
            customerName  = order.getCustomer().getFullName();
            customerPhone = order.getCustomer().getPhone();
        } else {
            customerName  = order.getGuestCustomerName();
            customerPhone = order.getGuestCustomerPhone();
        }

        // Resolve store info — Store.brand, StoreContact (if your StoreContact has phone/address)
        String storePhone   = null;
        String storeAddress = null;
        if (store.getContact() != null) {
            storePhone   = store.getContact().getPhone();
            storeAddress = store.getContact().getAddress();
        }

        Invoice invoice = Invoice.builder()
                .invoiceNumber("INV-" + System.currentTimeMillis())
                .order(order)
                .store(store)
                .status(InvoiceStatus.ISSUED)
                .subtotal(nullSafe(order.getSubtotal()))
                .discount(nullSafe(order.getDiscount()))
                .tax(nullSafe(order.getTax()))
                .totalAmount(nullSafe(order.getTotalPrice()))
                .customerName(customerName)
                .customerPhone(customerPhone)
                .billingAddress(order.getShippingAddress())
                .storeBrand(store.getBrand())       // Store.brand
                .storePhone(storePhone)
                .storeAddress(storeAddress)
                .dueDate(LocalDateTime.now().plusDays(7))
                .build();

        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice generated: {} for order: {}",
                saved.getInvoiceNumber(), order.getOrderNumber());
        return toInvoiceDto(saved);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  UPDATE INVOICE STATUS
    //  DRAFT   → ISSUED | CANCELLED
    //  ISSUED  → PAID   | OVERDUE | CANCELLED
    //  OVERDUE → PAID   | CANCELLED
    //  PAID    → terminal
    //  CANCELLED → terminal
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    public InvoiceDto updateInvoiceStatus(Long storeId, Long invoiceId, String statusStr)
            throws UserExceptions {

        Invoice invoice = requireInvoice(storeId, invoiceId);

        InvoiceStatus newStatus;
        try {
            newStatus = InvoiceStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UserExceptions.InvalidOrderStatusException(
                    "Invalid invoice status '" + statusStr + "'. Valid: "
                            + Arrays.toString(InvoiceStatus.values()));
        }

        validateInvoiceStatusTransition(invoice.getStatus(), newStatus);
        invoice.setStatus(newStatus);

        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice {} status: {} → {}",
                invoice.getInvoiceNumber(), invoice.getStatus(), newStatus);
        return toInvoiceDto(saved);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PRIVATE — VALIDATE INVOICE STATUS TRANSITION
    // ══════════════════════════════════════════════════════════════════════════

    private void validateInvoiceStatusTransition(InvoiceStatus from, InvoiceStatus to)
            throws UserExceptions.InvalidOrderStatusException {

        Map<InvoiceStatus, Set<InvoiceStatus>> allowed = Map.of(
                InvoiceStatus.DRAFT,     Set.of(InvoiceStatus.ISSUED, InvoiceStatus.CANCELLED),
                InvoiceStatus.ISSUED,    Set.of(InvoiceStatus.PAID, InvoiceStatus.OVERDUE, InvoiceStatus.CANCELLED),
                InvoiceStatus.OVERDUE,   Set.of(InvoiceStatus.PAID, InvoiceStatus.CANCELLED),
                InvoiceStatus.PAID,      Set.of(),
                InvoiceStatus.CANCELLED, Set.of()
        );

        Set<InvoiceStatus> next = allowed.getOrDefault(from, Set.of());
        if (!next.contains(to)) {
            throw new UserExceptions.InvalidOrderStatusException(
                    "Cannot transition invoice from " + from + " to " + to
                            + ". Allowed: " + next);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PRIVATE — MAP Invoice → InvoiceDto
    //  Uses User.fullName, Store.brand matching your entities
    // ══════════════════════════════════════════════════════════════════════════

    private InvoiceDto toInvoiceDto(Invoice invoice) {
        if (invoice == null) return null;

        // Pull line items from the linked order
        List<OrderItemDto> items = Collections.emptyList();
        if (invoice.getOrder() != null && invoice.getOrder().getItems() != null) {
            items = invoice.getOrder().getItems().stream()
                    .map(item -> {
                        OrderItemDto dto = new OrderItemDto();
                        dto.setId(item.getId());
                        dto.setProductId(item.getProduct() != null ? item.getProduct().getId() : null);
                        dto.setProductName(item.getProductName());
                        dto.setProductSku(item.getProductSku());
                        dto.setQuantity(item.getQuantity());
                        dto.setUnitPrice(item.getUnitPrice());
                        dto.setDiscount(item.getDiscount());
                        dto.setLineTotal(item.getLineTotal());
                        return dto;
                    })
                    .collect(Collectors.toList());
        }

        InvoiceDto dto = new InvoiceDto();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setStatus(invoice.getStatus());
        dto.setOrderId(invoice.getOrder() != null ? invoice.getOrder().getId() : null);
        dto.setOrderNumber(invoice.getOrder() != null ? invoice.getOrder().getOrderNumber() : null);
        dto.setStoreId(invoice.getStore() != null ? invoice.getStore().getId() : null);
        dto.setStoreBrand(invoice.getStoreBrand());
        dto.setSubtotal(invoice.getSubtotal());
        dto.setDiscount(invoice.getDiscount());
        dto.setTax(invoice.getTax());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setCustomerName(invoice.getCustomerName());
        dto.setCustomerPhone(invoice.getCustomerPhone());
        dto.setBillingAddress(invoice.getBillingAddress());
        dto.setStorePhone(invoice.getStorePhone());
        dto.setStoreAddress(invoice.getStoreAddress());
        dto.setItems(items);
        dto.setDueDate(invoice.getDueDate());
        dto.setNotes(invoice.getNotes());
        dto.setCreatedAt(invoice.getCreatedAt());
        return dto;
    }
}