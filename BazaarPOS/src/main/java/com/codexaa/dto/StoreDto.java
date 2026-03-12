package com.codexaa.dto;

import com.codexaa.domain.StoreStatus;
import com.codexaa.model.StoreContact;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StoreDto {

    private long id;

    private String brand;

    // for creation
    private Long storeAdminId;

    private UserDto storAdmin;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String description;

    private String storeType;

    private StoreStatus status;

    private StoreContact contact;
}