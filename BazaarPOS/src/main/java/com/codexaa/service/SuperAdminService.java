package com.codexaa.service;

import com.codexaa.dto.DashboardStatsDto;
import com.codexaa.dto.StoreDto;
import com.codexaa.dto.UserDto;
import com.codexaa.exception.UserExceptions;

import java.util.List;

public interface SuperAdminService {

    // ── Dashboard ─────────────────────────────────────────────────────────────
    DashboardStatsDto getDashboardStats();

    // ── Stores ────────────────────────────────────────────────────────────────
    List<StoreDto> getAllStores();

    StoreDto createStore(StoreDto storeDto);

    StoreDto updateStore(Long storeId, StoreDto storeDto) throws UserExceptions;  // ✅ NEW

    StoreDto approveStore(Long storeId) throws UserExceptions;

    StoreDto blockStore(Long storeId) throws UserExceptions;

    void deleteStore(Long storeId) throws UserExceptions;                          // ✅ NEW

    // ── Users ─────────────────────────────────────────────────────────────────
    List<UserDto> getAllUsers();

    UserDto blockUser(Long userId) throws UserExceptions;

    List<UserDto> getStoreAdmins();
}