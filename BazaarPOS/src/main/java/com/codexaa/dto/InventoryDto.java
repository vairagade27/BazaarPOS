package com.codexaa.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InventoryDto {

    private Long id;


    private Long branchId;
    private Long productId;


    private BranchDTO branch;
    private ProductDTO product;

    private Integer quantity;

    private LocalDateTime lastUpdated;
}