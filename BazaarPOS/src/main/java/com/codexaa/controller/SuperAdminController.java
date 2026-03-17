package com.codexaa.controller;

import com.codexaa.dto.DashboardStatsDto;
import com.codexaa.dto.StoreDto;
import com.codexaa.model.Store;
import com.codexaa.model.User;
import com.codexaa.service.SuperAdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/super-admin")
public class SuperAdminController {
    private final SuperAdminService superAdminService;

    public SuperAdminController(SuperAdminService superAdminService) {
        this.superAdminService = superAdminService;
    }

    @GetMapping("/dashboard")
    public DashboardStatsDto getDashboardStats() {
        return superAdminService.getDashboardStats();
    }

    @PostMapping("/create-store")
    public Store createStore(@RequestBody StoreDto storeDto) {
        return superAdminService.createStore(storeDto);
    }

    @GetMapping("/stores")
    public List<Store> getAllStores() {
        return superAdminService.getAllStores();
    }

    @PutMapping("/approve-store/{storeId}")
    public Store approveStore(@PathVariable Long storeId) {
        return superAdminService.approveStore(storeId);
    }

    @PutMapping("/block-store/{storeId}")
    public Store blockStore(@PathVariable Long storeId) {
        return superAdminService.blockStore(storeId);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return superAdminService.getAllUsers();
    }

    @PutMapping("/block-user/{userId}")
    public User blockUser(@PathVariable Long userId) {
        return superAdminService.blockUser(userId);
    }

    // ✅ 3. ADD THIS MISSING ENDPOINT
    @GetMapping("/store-admins")
    public List<User> getStoreAdmins() {
        return superAdminService.getStoreAdmins();
    }
}