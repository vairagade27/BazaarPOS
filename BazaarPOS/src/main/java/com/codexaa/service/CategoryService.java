package com.codexaa.service;

import com.codexaa.dto.CategoryDTO;
import com.codexaa.exception.UserExceptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    CategoryDTO createCategory(CategoryDTO dto) throws UserExceptions;

    Page<CategoryDTO> getCategoriesByStore(Long storeId, Pageable pageable);

    CategoryDTO updateCategory(Long id, CategoryDTO dto) throws UserExceptions;

    void deleteCategory(Long id) throws UserExceptions;
}