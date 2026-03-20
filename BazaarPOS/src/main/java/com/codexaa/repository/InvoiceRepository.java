package com.codexaa.repository;

import com.codexaa.domain.InvoiceStatus;
import com.codexaa.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    // One invoice per order
    Optional<Invoice> findByOrderId(Long orderId);

    // Ownership check — invoice belongs to store
    Optional<Invoice> findByIdAndStoreId(Long id, Long storeId);

    List<Invoice> findByStoreIdOrderByCreatedAtDesc(Long storeId);

    List<Invoice> findByStoreIdAndStatus(Long storeId, InvoiceStatus status);

    long countByStoreId(Long storeId);

    long countByStoreIdAndStatus(Long storeId, InvoiceStatus status);

    boolean existsByOrderId(Long orderId);
}