package com.codexaa.service.impl;

import com.codexaa.domain.StoreStatus;
import com.codexaa.domain.UserRole;
import com.codexaa.dto.DashboardStatsDto;
import com.codexaa.dto.StoreDto;
import com.codexaa.model.Store;
import com.codexaa.model.User;
import com.codexaa.repository.StoreRepository;
import com.codexaa.repository.UserRepository;
import com.codexaa.service.SuperAdminService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuperAdminServiceImpl implements SuperAdminService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public SuperAdminServiceImpl(StoreRepository storeRepository,
                                 UserRepository userRepository) {
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public DashboardStatsDto getDashboardStats() {

        DashboardStatsDto stats = new DashboardStatsDto();

        stats.setTotalStores(storeRepository.count());
        stats.setTotalUsers(userRepository.count());

        return stats;
    }

    @Override
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    @Override
    public Store approveStore(Long storeId) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        store.setStatus(StoreStatus.ACTIVE);

        return storeRepository.save(store);
    }

    @Override
    public Store blockStore(Long storeId) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        store.setStatus(StoreStatus.BLOCKED);

        return storeRepository.save(store);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User blockUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(false);

        return userRepository.save(user);
    }

    @Override
    public Store createStore(StoreDto dto) {

        User storeAdmin = userRepository.findById(dto.getStoreAdminId())
                .orElseThrow(() -> new RuntimeException("Store Admin not found"));

        Store store = new Store();

        store.setBrand(dto.getBrand());
        store.setDescription(dto.getDescription());
        store.setStoreType(dto.getStoreType());

        store.setStoreAdmin(storeAdmin);

        store.setStatus(StoreStatus.PENDING);

        store.setContact(dto.getContact());

        return storeRepository.save(store);
    }
    @Override
    public List<User> getStoreAdmins() {
        System.out.println("🔍 DEBUG: Fetching Store Admins...");

        // 1. Check what roles exist in DB roughly
        long totalCount = userRepository.count();
        System.out.println("🔍 DEBUG: Total users in DB: " + totalCount);

        // 2. Execute the filter
        List<User> admins = userRepository.findByRoleAndEnabledTrue(UserRole.ROLE_STORE_ADMIN);

        System.out.println("🔍 DEBUG: Found " + admins.size() + " Store Admins.");
        if (admins.isEmpty()) {
            System.out.println("⚠️ WARNING: No users found with role ROLE_STORE_ADMIN and enabled=true");
            System.out.println("💡 Tip: Check your database. Do any users have role='ROLE_STORE_ADMIN'?");
        } else {
            admins.forEach(u -> System.out.println("✅ Found Admin: " + u.getEmail()));
        }

        return admins;
    }
}