package com.codexaa.mapper;

import com.codexaa.dto.BranchDTO;
import com.codexaa.model.Branch;
import com.codexaa.model.Store;
import com.codexaa.model.User;

public class BranchMapper {

    // Entity -> DTO
    public static BranchDTO toDTO(Branch branch) {
        return BranchDTO.builder()
                .id(branch.getId())
                .name(branch.getName())
                .address(branch.getAddress())
                .phone(branch.getPhone())
                .email(branch.getEmail())
                .workingDays(branch.getWorkingDays())
                .openTime(branch.getOpenTime())
                .closeTime(branch.getCloseTime())
                .createdAt(branch.getCreatedAt())
                .updatedAt(branch.getUpdatedAt())
                .storeId(branch.getStore() != null ? branch.getStore().getId() : null)
                .managerId(branch.getManager() != null ? branch.getManager().getId() : null)
                .build();
    }

    // DTO -> Entity
    public static Branch toEntity(BranchDTO dto, Store store, User manager) {
        return Branch.builder()
                .id(dto.getId())
                .name(dto.getName())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .workingDays(dto.getWorkingDays())
                .openTime(dto.getOpenTime())
                .closeTime(dto.getCloseTime())
                .store(store)
                .manager(manager)
                .build();
    }
}