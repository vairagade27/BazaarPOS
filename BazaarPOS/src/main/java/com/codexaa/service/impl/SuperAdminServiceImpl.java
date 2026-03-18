package com.codexaa.service.impl;

import com.codexaa.domain.StoreStatus;
import com.codexaa.domain.UserRole;
import com.codexaa.dto.DashboardStatsDto;
import com.codexaa.dto.StoreDto;
import com.codexaa.dto.UserDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.mapper.StoreMapper;
import com.codexaa.mapper.UserMapper;
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
    private final UserRepository  userRepository;

    public SuperAdminServiceImpl(StoreRepository storeRepository,
                                 UserRepository  userRepository) {
        this.storeRepository = storeRepository;
        this.userRepository  = userRepository;
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    @Override
    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalStores(storeRepository.count());
        stats.setTotalUsers(userRepository.count());
        return stats;
    }

    // ── Stores ────────────────────────────────────────────────────────────────

    @Override
    public List<StoreDto> getAllStores() {
        return storeRepository.findAll()
                .stream()
                .map(StoreMapper::toDTO)
                .toList();
    }

    @Override
    public StoreDto createStore(StoreDto dto) {
        User storeAdmin = userRepository.findById(dto.getStoreAdminId())
                .orElseThrow(() -> new RuntimeException("Store admin not found with id: " + dto.getStoreAdminId()));

        Store store = new Store();
        store.setBrand(dto.getBrand());
        store.setDescription(dto.getDescription());
        store.setStoreType(dto.getStoreType());
        store.setStoreAdmin(storeAdmin);
        store.setContact(dto.getContact());
        store.setStatus(StoreStatus.PENDING);

        return StoreMapper.toDTO(storeRepository.save(store));
    }

    // ✅ Super admin can update ANY store field — no ownership check needed
    @Override
    public StoreDto updateStore(Long storeId, StoreDto dto) throws UserExceptions {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new UserExceptions("Store not found with id: " + storeId));

        // Only update fields that are actually provided (null-safe)
        if (dto.getBrand() != null && !dto.getBrand().isBlank()) {
            store.setBrand(dto.getBrand());
        }
        if (dto.getDescription() != null) {
            store.setDescription(dto.getDescription());
        }
        if (dto.getStoreType() != null && !dto.getStoreType().isBlank()) {
            store.setStoreType(dto.getStoreType());
        }
        if (dto.getContact() != null) {
            store.setContact(dto.getContact());
        }

        // Super admin can also reassign the store to a different admin
        if (dto.getStoreAdminId() != null) {
            User newAdmin = userRepository.findById(dto.getStoreAdminId())
                    .orElseThrow(() -> new UserExceptions("Store admin not found with id: " + dto.getStoreAdminId()));
            store.setStoreAdmin(newAdmin);
        }

        // @PreUpdate in Store.java handles updatedAt automatically
        return StoreMapper.toDTO(storeRepository.save(store));
    }

    @Override
    public StoreDto approveStore(Long storeId) throws UserExceptions {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new UserExceptions("Store not found with id: " + storeId));
        store.setStatus(StoreStatus.ACTIVE);
        return StoreMapper.toDTO(storeRepository.save(store));
    }

    @Override
    public StoreDto blockStore(Long storeId) throws UserExceptions {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new UserExceptions("Store not found with id: " + storeId));
        store.setStatus(StoreStatus.BLOCKED);
        return StoreMapper.toDTO(storeRepository.save(store));
    }

    @Override
    public void deleteStore(Long storeId) throws UserExceptions {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new UserExceptions("Store not found with id: " + storeId));
        storeRepository.delete(store);
    }

    // ── Users ─────────────────────────────────────────────────────────────────

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToDto)
                .toList();
    }

    @Override
    public UserDto blockUser(Long userId) throws UserExceptions {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserExceptions("User not found with id: " + userId));
        user.setEnabled(false);
        return UserMapper.mapToDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> getStoreAdmins() {
        return userRepository.findByRoleAndEnabledTrue(UserRole.ROLE_STORE_ADMIN)
                .stream()
                .map(UserMapper::mapToDto)
                .toList();
    }
}