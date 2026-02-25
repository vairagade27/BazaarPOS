package com.codexaa.service;

import com.codexaa.dto.CategoryDTO;
import com.codexaa.exception.UserExceptions;
import com.codexaa.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDTO createCategory(CategoryDTO categoryDTO) throws UserExceptions;
    List<CategoryDTO> getCategoryByStore(Long StoreId);
    CategoryDTO updateCategory(Long id ,CategoryDTO categoryDTO) throws UserExceptions;
    void deleteCategory(Long id) throws UserExceptions;
}
