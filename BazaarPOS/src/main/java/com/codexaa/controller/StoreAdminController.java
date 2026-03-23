package com.codexaa.controller;

import com.codexaa.dto.*;
import com.codexaa.exception.UserExceptions;
import com.codexaa.service.StoreAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/store-admin")
public class StoreAdminController {

    private final StoreAdminService storeAdminService;

    public StoreAdminController(StoreAdminService storeAdminService) {
        this.storeAdminService = storeAdminService;
    }

    @GetMapping("/{storeId}/dashboard")
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER','BRANCH_MANAGER')")
    public ResponseEntity<DashboardStatsDto> getDashboardStats(
            @PathVariable Long storeId) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.getDashboardStats(storeId));
    }

    @GetMapping("/{storeId}/branches")
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER','BRANCH_MANAGER','CASHIER')")
    public ResponseEntity<List<BranchDTO>> getBranches(
            @PathVariable Long storeId) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.getBranches(storeId));
    }

    @PostMapping("/{storeId}/branches")
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER')")
    public ResponseEntity<BranchDTO> createBranch(
            @PathVariable Long storeId,
            @RequestBody BranchDTO branchDto) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.createBranch(storeId, branchDto));
    }

    @PutMapping("/{storeId}/branches/{branchId}")
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER')")
    public ResponseEntity<BranchDTO> updateBranch(
            @PathVariable Long storeId,
            @PathVariable Long branchId,
            @RequestBody BranchDTO branchDto) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.updateBranch(storeId, branchId, branchDto));
    }

    @DeleteMapping("/{storeId}/branches/{branchId}")
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER')")
    public ResponseEntity<String> deleteBranch(
            @PathVariable Long storeId,
            @PathVariable Long branchId) throws UserExceptions {
        storeAdminService.deleteBranch(storeId, branchId);
        return ResponseEntity.ok("Branch deleted successfully");
    }

    @GetMapping("/{storeId}/products")
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER','BRANCH_MANAGER','CASHIER')")
    public ResponseEntity<List<ProductDTO>> getProducts(
            @PathVariable Long storeId) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.getProducts(storeId));
    }

    @PostMapping("/{storeId}/products")
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER')")
    public ResponseEntity<ProductDTO> createProduct(
            @PathVariable Long storeId,
            @RequestBody ProductDTO productDto) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.createProduct(storeId, productDto));
    }

    @PutMapping("/{storeId}/products/{productId}")
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER')")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @RequestBody ProductDTO productDto) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.updateProduct(storeId, productId, productDto));
    }

    @DeleteMapping("/{storeId}/products/{productId}")
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER')")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId) throws UserExceptions {
        storeAdminService.deleteProduct(storeId, productId);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @GetMapping("/{storeId}/inventory")
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER','BRANCH_MANAGER','CASHIER')")
    public ResponseEntity<List<InventoryDto>> getInventory(
            @PathVariable Long storeId) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.getInventory(storeId));
    }

    @PutMapping("/{storeId}/inventory/{inventoryId}")
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER','BRANCH_MANAGER')")
    public ResponseEntity<InventoryDto> updateInventory(
            @PathVariable Long storeId,
            @PathVariable Long inventoryId,
            @RequestBody InventoryDto inventoryDto) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.updateInventory(storeId, inventoryId, inventoryDto));
    }

    @GetMapping("/{storeId}/employees")
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER','BRANCH_MANAGER')")
    public ResponseEntity<List<UserDto>> getEmployees(
            @PathVariable Long storeId) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.getEmployees(storeId));
    }

    @PostMapping("/{storeId}/employees")
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER')")
    public ResponseEntity<UserDto> addEmployee(
            @PathVariable Long storeId,
            @RequestBody UserDto userDto) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.addEmployee(storeId, userDto));
    }

    @PutMapping("/{storeId}/employees/{employeeId}")
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER')")
    public ResponseEntity<UserDto> updateEmployee(
            @PathVariable Long storeId,
            @PathVariable Long employeeId,
            @RequestBody UserDto userDto) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.updateEmployee(storeId, employeeId, userDto));
    }

    @DeleteMapping("/{storeId}/employees/{employeeId}")
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER')")
    public ResponseEntity<String> removeEmployee(
            @PathVariable Long storeId,
            @PathVariable Long employeeId) throws UserExceptions {
        storeAdminService.removeEmployee(storeId, employeeId);
        return ResponseEntity.ok("Employee removed successfully");
    }

    @GetMapping("/{storeId}/customers")
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER','BRANCH_MANAGER')")
    public ResponseEntity<List<UserDto>> getCustomers(
            @PathVariable Long storeId) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.getCustomers(storeId));
    }

}