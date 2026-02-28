package com.codexaa.mapper;

import com.codexaa.dto.InventoryDto;
import com.codexaa.model.Branch;
import com.codexaa.model.Inventory;
import com.codexaa.model.Product;

public class InventoryMapper {

    public static InventoryDto toDTO(Inventory inventory) {
        return InventoryDto.builder()
                .id(inventory.getId())

                // Correct branch ID
                .branchId(inventory.getBranch().getId())

                // Product
                .productId(inventory.getProduct().getId())
                .product(ProductMapper.toDTO(inventory.getProduct()))

                .quantity(inventory.getQuantity())
                .lastUpdated(inventory.getLastUpdated())

                .build();
    }

    public static Inventory toEntity(InventoryDto dto,
                                     Branch branch,
                                     Product product) {

        return Inventory.builder()
                .branch(branch)
                .product(product)
                .quantity(dto.getQuantity())
                .build();
    }
}