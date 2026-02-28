package com.codexaa.mapper;

import com.codexaa.dto.CategoryDTO;
import com.codexaa.model.Category;
import com.codexaa.model.Store;

public class CategoryMapper {

    // ENTITY ➜ DTO
    public static CategoryDTO toDTO(Category category) {

        if (category == null) return null;

        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .storeId(
                        category.getStore() != null
                                ? category.getStore().getId()
                                : null
                )
                .build();
    }

    // DTO ➜ ENTITY (CREATE)
    public static Category toEntity(CategoryDTO dto, Store store) {

        if (dto == null) return null;

        return Category.builder()
                .name(dto.getName().trim())
                .store(store)
                .build();
    }

    // UPDATE EXISTING ENTITY
    public static void updateEntity(Category category, CategoryDTO dto) {

        if (dto.getName() != null && !dto.getName().isBlank()) {
            category.setName(dto.getName().trim());
        }
    }
}