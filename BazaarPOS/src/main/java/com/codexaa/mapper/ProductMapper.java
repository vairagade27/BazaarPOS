package com.codexaa.mapper;

import com.codexaa.dto.ProductDTO;
import com.codexaa.model.Category;
import com.codexaa.model.Product;
import com.codexaa.model.Store;

public class ProductMapper {

    // ENTITY ➜ DTO
    public static ProductDTO toDTO(Product product) {

        if (product == null) return null;

        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .sku(product.getSku())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .storeId(
                        product.getStore() != null
                                ? product.getStore().getId()
                                : null
                )
                .categoryId(
                        product.getCategory() != null
                                ? product.getCategory().getId()
                                : null
                )
                .build();
    }

    // DTO ➜ ENTITY (CREATE)
    public static Product toEntity(ProductDTO dto,
                                   Store store,
                                   Category category) {

        if (dto == null) return null;

        return Product.builder()
                .name(dto.getName().trim())
                .brand(dto.getBrand())
                .sku(dto.getSku().trim())
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .store(store)
                .category(category)
                .build();
    }

    // UPDATE EXISTING ENTITY
    public static void updateEntity(Product product, ProductDTO dto) {

        if (dto.getName() != null && !dto.getName().isBlank()) {
            product.setName(dto.getName().trim());
        }

        if (dto.getBrand() != null) {
            product.setBrand(dto.getBrand());
        }

        if (dto.getPrice() != null) {
            product.setPrice(dto.getPrice());
        }

        if (dto.getQuantity() != null) {
            product.setQuantity(dto.getQuantity());
        }
    }
}