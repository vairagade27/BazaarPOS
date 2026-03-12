package com.codexaa.service;

import com.codexaa.dto.DashboardStatsDto;
import com.codexaa.dto.StoreDto;
import com.codexaa.model.Store;
import com.codexaa.model.User;

import java.util.List;

public interface SuperAdminService {

    DashboardStatsDto getDashboardStats();

    List<Store> getAllStores();

    Store approveStore(Long storeId);

    Store blockStore(Long storeId);

    List<User> getAllUsers();

    User blockUser(Long userId);

    Store createStore(StoreDto storeDto);
}