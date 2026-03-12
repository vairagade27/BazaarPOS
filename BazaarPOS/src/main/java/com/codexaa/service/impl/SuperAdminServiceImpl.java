package com.codexaa.service.impl;

import com.codexaa.domain.StoreStatus;
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
}