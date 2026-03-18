package com.codexaa.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PlanDto {

    private Long id;

    private String name;

    private Double price;

    private String status;

    private List<String> features;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}