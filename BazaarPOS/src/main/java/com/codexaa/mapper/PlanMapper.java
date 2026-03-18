package com.codexaa.mapper;

import com.codexaa.dto.PlanDto;
import com.codexaa.model.Plan;

public class PlanMapper {

    public static PlanDto toDTO(Plan plan) {
        if (plan == null) return null;

        PlanDto dto = new PlanDto();
        dto.setId(plan.getId());
        dto.setName(plan.getName());
        dto.setPrice(plan.getPrice());
        dto.setStatus(plan.getStatus());
        dto.setFeatures(plan.getFeatures());
        dto.setCreatedAt(plan.getCreatedAt());
        dto.setUpdatedAt(plan.getUpdatedAt());
        return dto;
    }

    public static Plan toEntity(PlanDto dto) {
        if (dto == null) return null;

        Plan plan = new Plan();
        plan.setName(dto.getName());
        plan.setPrice(dto.getPrice());
        plan.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
        plan.setFeatures(dto.getFeatures());
        return plan;
    }
}