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

    // Dashboard statistics
    @GetMapping("/dashboard")
    public DashboardStatsDto getDashboardStats() {
        return superAdminService.getDashboardStats();
    }

    // Create Store
    @PostMapping("/create-store")
    public Store createStore(@RequestBody StoreDto storeDto) {
        return superAdminService.createStore(storeDto);
    }

    // Get All Stores
    @GetMapping("/stores")
    public List<Store> getAllStores() {
        return superAdminService.getAllStores();
    }

    // Approve Store
    @PutMapping("/approve-store/{storeId}")
    public Store approveStore(@PathVariable Long storeId) {
        return superAdminService.approveStore(storeId);
    }

    // Block Store
    @PutMapping("/block-store/{storeId}")
    public Store blockStore(@PathVariable Long storeId) {
        return superAdminService.blockStore(storeId);
    }

    // Get All Users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return superAdminService.getAllUsers();
    }

    // Block User
    @PutMapping("/block-user/{userId}")
    public User blockUser(@PathVariable Long userId) {
        return superAdminService.blockUser(userId);
    }
}