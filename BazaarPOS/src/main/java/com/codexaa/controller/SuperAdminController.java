package com.codexaa.controller;

import com.codexaa.dto.DashboardStatsDto;
import com.codexaa.dto.StoreDto;
import com.codexaa.dto.UserDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.service.SuperAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/super-admin")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    public SuperAdminController(SuperAdminService superAdminService) {
        this.superAdminService = superAdminService;
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(superAdminService.getDashboardStats());
    }

    // ── Stores ────────────────────────────────────────────────────────────────

    @GetMapping("/stores")
    public ResponseEntity<List<StoreDto>> getAllStores() {
        return ResponseEntity.ok(superAdminService.getAllStores());
    }

    @PostMapping("/create-store")
    public ResponseEntity<StoreDto> createStore(@RequestBody StoreDto storeDto) {
        return ResponseEntity.ok(superAdminService.createStore(storeDto));
    }

    // ✅ Update store — super admin can update any store regardless of ownership
    @PutMapping("/update-store/{storeId}")
    public ResponseEntity<StoreDto> updateStore(
            @PathVariable Long storeId,
            @RequestBody StoreDto storeDto
    ) throws UserExceptions {
        return ResponseEntity.ok(superAdminService.updateStore(storeId, storeDto));
    }

    @PutMapping("/approve-store/{storeId}")
    public ResponseEntity<StoreDto> approveStore(@PathVariable Long storeId) throws UserExceptions {
        return ResponseEntity.ok(superAdminService.approveStore(storeId));
    }

    @PutMapping("/block-store/{storeId}")
    public ResponseEntity<StoreDto> blockStore(@PathVariable Long storeId) throws UserExceptions {
        return ResponseEntity.ok(superAdminService.blockStore(storeId));
    }

    @DeleteMapping("/delete-store/{storeId}")
    public ResponseEntity<String> deleteStore(@PathVariable Long storeId) throws UserExceptions {
        superAdminService.deleteStore(storeId);
        return ResponseEntity.ok("Store deleted successfully");
    }

    // ── Users ─────────────────────────────────────────────────────────────────

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(superAdminService.getAllUsers());
    }

    @PutMapping("/block-user/{userId}")
    public ResponseEntity<UserDto> blockUser(@PathVariable Long userId) throws UserExceptions {
        return ResponseEntity.ok(superAdminService.blockUser(userId));
    }

    @GetMapping("/store-admins")
    public ResponseEntity<List<UserDto>> getStoreAdmins() {
        return ResponseEntity.ok(superAdminService.getStoreAdmins());
    }
}