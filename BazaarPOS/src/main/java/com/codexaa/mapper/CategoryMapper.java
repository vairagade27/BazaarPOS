package com.codexaa.mapper;

import com.codexaa.dto.CategoryDTO;
import com.codexaa.model.Category;
import com.codexaa.model.Store;

public class CategoryMapper {


    public static CategoryDTO toDTO(Category category) {

        if (category == null) {
            return null;
        }

        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());

        if (category.getStore() != null) {
            dto.setStoreId(category.getStore().getId());
        }

        return dto;
    }

    public static Category toEntity(CategoryDTO dto, Store store) {

        if (dto == null) {
            return null;
        }

        return Category.builder()
                .name(dto.getName())
                .store(store)
                .build();
    }


    public static void updateEntity(Category category, CategoryDTO dto) {

        category.setName(dto.getName());
    }
}