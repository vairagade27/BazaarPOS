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
@PreAuthorize("hasRole('STORE_ADMIN')")
public class StoreAdminController {

    private final StoreAdminService storeAdminService;

    public StoreAdminController(StoreAdminService storeAdminService) {
        this.storeAdminService = storeAdminService;
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    @GetMapping("/{storeId}/dashboard")
    public ResponseEntity<DashboardStatsDto> getDashboardStats(
            @PathVariable Long storeId
    ) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.getDashboardStats(storeId));
    }

    // ── Branches ─────────────────────────────────────────────────────────────

    @GetMapping("/{storeId}/branches")
    public ResponseEntity<List<BranchDTO>> getBranches(
            @PathVariable Long storeId
    ) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.getBranches(storeId));
    }

    @PostMapping("/{storeId}/branches")
    public ResponseEntity<BranchDTO> createBranch(
            @PathVariable Long storeId,
            @RequestBody BranchDTO branchDto
    ) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.createBranch(storeId, branchDto));
    }

    @PutMapping("/{storeId}/branches/{branchId}")
    public ResponseEntity<BranchDTO> updateBranch(
            @PathVariable Long storeId,
            @PathVariable Long branchId,
            @RequestBody BranchDTO branchDto
    ) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.updateBranch(storeId, branchId, branchDto));
    }

    @DeleteMapping("/{storeId}/branches/{branchId}")
    public ResponseEntity<String> deleteBranch(
            @PathVariable Long storeId,
            @PathVariable Long branchId
    ) throws UserExceptions {
        storeAdminService.deleteBranch(storeId, branchId);
        return ResponseEntity.ok("Branch deleted successfully");
    }

    // ── Products ─────────────────────────────────────────────────────────────

    @GetMapping("/{storeId}/products")
    public ResponseEntity<List<ProductDTO>> getProducts(
            @PathVariable Long storeId
    ) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.getProducts(storeId));
    }

    @PostMapping("/{storeId}/products")
    public ResponseEntity<ProductDTO> createProduct(
            @PathVariable Long storeId,
            @RequestBody ProductDTO productDto
    ) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.createProduct(storeId, productDto));
    }

    @PutMapping("/{storeId}/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @RequestBody ProductDTO productDto
    ) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.updateProduct(storeId, productId, productDto));
    }

    @DeleteMapping("/{storeId}/products/{productId}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId
    ) throws UserExceptions {
        storeAdminService.deleteProduct(storeId, productId);
        return ResponseEntity.ok("Product deleted successfully");
    }

    // ── Inventory ─────────────────────────────────────────────────────────────

    @GetMapping("/{storeId}/inventory")
    public ResponseEntity<List<InventoryDto>> getInventory(
            @PathVariable Long storeId
    ) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.getInventory(storeId));
    }

    @PutMapping("/{storeId}/inventory/{inventoryId}")
    public ResponseEntity<InventoryDto> updateInventory(
            @PathVariable Long storeId,
            @PathVariable Long inventoryId,
            @RequestBody InventoryDto inventoryDto
    ) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.updateInventory(storeId, inventoryId, inventoryDto));
    }

    // ── Employees ─────────────────────────────────────────────────────────────

    @GetMapping("/{storeId}/employees")
    public ResponseEntity<List<UserDto>> getEmployees(
            @PathVariable Long storeId
    ) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.getEmployees(storeId));
    }

    @PostMapping("/{storeId}/employees")
    public ResponseEntity<UserDto> addEmployee(
            @PathVariable Long storeId,
            @RequestBody UserDto userDto
    ) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.addEmployee(storeId, userDto));
    }

    @PutMapping("/{storeId}/employees/{employeeId}")
    public ResponseEntity<UserDto> updateEmployee(
            @PathVariable Long storeId,
            @PathVariable Long employeeId,
            @RequestBody UserDto userDto
    ) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.updateEmployee(storeId, employeeId, userDto));
    }

    @DeleteMapping("/{storeId}/employees/{employeeId}")
    public ResponseEntity<String> removeEmployee(
            @PathVariable Long storeId,
            @PathVariable Long employeeId
    ) throws UserExceptions {
        storeAdminService.removeEmployee(storeId, employeeId);
        return ResponseEntity.ok("Employee removed successfully");
    }

    // ── Orders — handled by OrderController (same base path, no duplicates) ───
    // GET    /api/store-admin/{storeId}/orders              → OrderController
    // GET    /api/store-admin/{storeId}/orders/{orderId}    → OrderController
    // POST   /api/store-admin/{storeId}/orders              → OrderController
    // PUT    /api/store-admin/{storeId}/orders/{id}/status  → OrderController
    // PUT    /api/store-admin/{storeId}/orders/{id}/cancel  → OrderController

    // ── Customers ─────────────────────────────────────────────────────────────

    @GetMapping("/{storeId}/customers")
    public ResponseEntity<List<UserDto>> getCustomers(
            @PathVariable Long storeId
    ) throws UserExceptions {
        return ResponseEntity.ok(storeAdminService.getCustomers(storeId));
    }
}