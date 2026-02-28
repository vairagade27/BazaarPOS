package com.codexaa.service;

import com.codexaa.dto.InventoryDto;
import com.codexaa.exception.UserExceptions;

import java.util.List;

public interface InventoryService {

    InventoryDto createInventory(InventoryDto inventoryDto) throws UserExceptions;

    InventoryDto updateInventory(Long id, InventoryDto inventoryDto) throws UserExceptions;

    void deleteInventory(Long id) throws UserExceptions;

    InventoryDto getInventoryById(Long id) throws UserExceptions;

    InventoryDto findByProductIdAndBranchId(Long productId, Long branchId) throws UserExceptions;

    List<InventoryDto> getAllInventoryByBranchId(Long branchId);
}