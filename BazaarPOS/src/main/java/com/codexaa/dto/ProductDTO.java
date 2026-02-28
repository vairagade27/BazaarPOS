package com.codexaa.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;
    private String name;
    private String brand;
    private String sku;
    private BigDecimal price;
    private Integer quantity;

    private Long storeId;
    private Long categoryId;
}