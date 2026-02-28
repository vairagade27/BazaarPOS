package com.codexaa.repository;

import com.codexaa.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Page<Category> findByStoreId(Long storeId, Pageable pageable);

    boolean existsByNameAndStoreId(String name, Long storeId);
}