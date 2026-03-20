package com.codexaa.repository;

import com.codexaa.domain.OrderStatus;
import com.codexaa.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Optional<Order> findByIdAndStoreId(Long id, Long storeId);

    List<Order> findByStoreIdOrderByCreatedAtDesc(Long storeId);

    List<Order> findByStoreIdAndStatus(Long storeId, OrderStatus status);

    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    long countByStoreId(Long storeId);

    long countByStoreIdAndStatus(Long storeId, OrderStatus status);

    // Total completed revenue for a store
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o " +
            "WHERE o.store.id = :storeId AND o.status = 'COMPLETED'")
    BigDecimal getTotalRevenueByStoreId(@Param("storeId") Long storeId);

    // Revenue between dates
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o " +
            "WHERE o.store.id = :storeId AND o.status = 'COMPLETED' " +
            "AND o.createdAt BETWEEN :from AND :to")
    BigDecimal getRevenueByStoreIdBetween(@Param("storeId") Long storeId,
                                          @Param("from") LocalDateTime from,
                                          @Param("to") LocalDateTime to);

    // Daily revenue — for chart (returns Object[]{Date, BigDecimal})
    @Query("SELECT DATE(o.createdAt), COALESCE(SUM(o.totalPrice), 0) " +
            "FROM Order o " +
            "WHERE o.store.id = :storeId AND o.status = 'COMPLETED' " +
            "AND o.createdAt >= :since " +
            "GROUP BY DATE(o.createdAt) ORDER BY DATE(o.createdAt)")
    List<Object[]> revenuePerDayAfter(@Param("storeId") Long storeId,
                                      @Param("since") LocalDateTime since);

    // Daily order count — for chart (returns Object[]{Date, Long})
    @Query("SELECT DATE(o.createdAt), COUNT(o) " +
            "FROM Order o " +
            "WHERE o.store.id = :storeId AND o.createdAt >= :since " +
            "GROUP BY DATE(o.createdAt) ORDER BY DATE(o.createdAt)")
    List<Object[]> countOrdersPerDayAfter(@Param("storeId") Long storeId,
                                          @Param("since") LocalDateTime since);

    // Unique customers count
    @Query("SELECT COUNT(DISTINCT o.customer.id) FROM Order o " +
            "WHERE o.store.id = :storeId AND o.customer IS NOT NULL")
    Long countDistinctCustomersByStoreId(@Param("storeId") Long storeId);
}