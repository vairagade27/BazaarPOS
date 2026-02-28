package com.codexaa.repository;

import com.codexaa.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByStoreId(Long storeId, Pageable pageable);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    boolean existsBySkuAndStoreId(String sku, Long storeId);

    @Query("SELECT p FROM Product p " +
            "WHERE p.store.id = :storeId " +
            "AND (" +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchByKeyword(@Param("storeId") Long storeId,
                                  @Param("keyword") String keyword,
                                  Pageable pageable);
}