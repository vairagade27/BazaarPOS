package com.codexaa.controller;

import com.codexaa.dto.InventoryDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branch")
@RequiredArgsConstructor
public class BranchInventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{branchId}/inventory")
    @PreAuthorize("hasAnyRole('BRANCH_MANAGER','STORE_MANAGER','CASHIER','STORE_ADMIN')")
    public ResponseEntity<List<InventoryDto>> getInventoryByBranch(
            @PathVariable Long branchId) {
        return ResponseEntity.ok(inventoryService.getAllInventoryByBranchId(branchId));
    }

    @PutMapping("/{branchId}/inventory/{inventoryId}")
    @PreAuthorize("hasAnyRole('BRANCH_MANAGER','STORE_MANAGER','STORE_ADMIN')")
    public ResponseEntity<InventoryDto> updateInventory(
            @PathVariable Long branchId,
            @PathVariable Long inventoryId,
            @RequestBody InventoryDto dto) throws UserExceptions {
        return ResponseEntity.ok(inventoryService.updateInventory(inventoryId, dto));
    }

    @PostMapping("/{branchId}/inventory")
    @PreAuthorize("hasAnyRole('BRANCH_MANAGER','STORE_MANAGER','STORE_ADMIN')")
    public ResponseEntity<InventoryDto> createInventory(
            @PathVariable Long branchId,
            @RequestBody InventoryDto dto) throws UserExceptions {
        dto.setBranchId(branchId);
        return ResponseEntity.ok(inventoryService.createInventory(dto));
    }

    @DeleteMapping("/{branchId}/inventory/{inventoryId}")
    @PreAuthorize("hasAnyRole('BRANCH_MANAGER','STORE_MANAGER','STORE_ADMIN')")
    public ResponseEntity<String> deleteInventory(
            @PathVariable Long branchId,
            @PathVariable Long inventoryId) throws UserExceptions {
        inventoryService.deleteInventory(inventoryId);
        return ResponseEntity.ok("Inventory deleted successfully");
    }
}