package com.codexaa.repository;

import com.codexaa.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ── Basic finders ─────────────────────────────────────────────────────────

    Optional<Product> findByIdAndStoreId(Long productId, Long storeId);

    // ── Store-level queries ───────────────────────────────────────────────────

    Page<Product> findByStoreId(Long storeId, Pageable pageable);

    List<Product> findByStoreId(Long storeId);

    // ── Category queries ──────────────────────────────────────────────────────

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByStoreIdAndCategoryId(Long storeId, Long categoryId, Pageable pageable);

    // ── Existence checks ──────────────────────────────────────────────────────

    boolean existsBySkuAndStoreId(String sku, Long storeId);

    // For update — ensure SKU is not taken by a different product
    boolean existsBySkuAndStoreIdAndIdNot(String sku, Long storeId, Long excludeId);

    // ── Count ─────────────────────────────────────────────────────────────────

    long countByStoreId(Long storeId);

    // ── Search — removed p.active = true (Product has no active field) ────────

    @Query("SELECT p FROM Product p " +
            "WHERE p.store.id = :storeId " +
            "AND (" +
            "   LOWER(p.name)  LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.sku)   LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchByKeyword(@Param("storeId") Long storeId,
                                  @Param("keyword") String keyword,
                                  Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE p.store.id = :storeId " +
            "AND p.category.id = :categoryId " +
            "AND (" +
            "   LOWER(p.name)  LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.sku)   LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchByKeywordAndCategory(@Param("storeId") Long storeId,
                                             @Param("categoryId") Long categoryId,
                                             @Param("keyword") String keyword,
                                             Pageable pageable);
}