package com.codexaa.service.impl;

import com.codexaa.domain.StoreStatus;
import com.codexaa.dto.StoreDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.mapper.StoreMapper;
import com.codexaa.model.Store;
import com.codexaa.model.User;
import com.codexaa.repository.StoreRepository;
import com.codexaa.service.StoreService;
import com.codexaa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final UserService userService;

    @Override
    public StoreDto createStore(StoreDto storeDto, User user) {
        User storeAdmin = userService.getUserById(storeDto.getStoreAdminId());
        if (storeAdmin == null) {
            throw new RuntimeException("Store admin not found");
        }

        Store store = new Store();
        store.setBrand(storeDto.getBrand());
        store.setDescription(storeDto.getDescription());
        store.setStoreType(storeDto.getStoreType());
        store.setStoreAdmin(storeAdmin);
        store.setContact(storeDto.getContact()); // ✅ contact saved
        store.setStatus(StoreStatus.PENDING);
        store.setCreatedAt(LocalDateTime.now());

        return StoreMapper.toDTO(storeRepository.save(store));
    }

    @Override
    public StoreDto getStoreById(Long id) throws UserExceptions {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new UserExceptions("Store not found"));
        return StoreMapper.toDTO(store);
    }

    @Override
    public List<StoreDto> getAllStores() {
        return storeRepository.findAll()
                .stream()
                .map(StoreMapper::toDTO)
                .toList();
    }

    @Override
    public StoreDto getStoreByAdmin() throws UserExceptions {
        User admin = userService.getCurrentUser();
        Store store = storeRepository.findByStoreAdminId(admin.getId());
        if (store == null) {
            throw new UserExceptions("Store not found for this admin");
        }
        return StoreMapper.toDTO(store);
    }

    @Override
    public StoreDto updateStore(Long id, StoreDto storeDto) throws UserExceptions {
        User currentUser = userService.getCurrentUser();

        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new UserExceptions("Store not found"));

        // ✅ ROLE_ADMIN (super admin) can update any store — only enforce ownership for store admins
        boolean isSuperAdmin = currentUser.getRole().name().equals("ROLE_ADMIN");
        if (!isSuperAdmin && !store.getStoreAdmin().getId().equals(currentUser.getId())) {
            throw new UserExceptions("You don't have permission to update this store");
        }

        if (storeDto.getBrand() != null && !storeDto.getBrand().isBlank()) {
            store.setBrand(storeDto.getBrand());
        }
        if (storeDto.getDescription() != null) {
            store.setDescription(storeDto.getDescription());
        }
        if (storeDto.getStoreType() != null) {
            store.setStoreType(storeDto.getStoreType());
        }
        if (storeDto.getContact() != null) {
            store.setContact(storeDto.getContact()); // ✅ contact updates persist
        }
        // @PreUpdate handles updatedAt automatically

        return StoreMapper.toDTO(storeRepository.save(store));
    }

    @Override
    public void deleteStore(Long id) throws UserExceptions {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new UserExceptions("Store not found"));
        storeRepository.delete(store);
    }

    @Override
    public StoreDto moderateStore(Long id, StoreStatus status) throws UserExceptions {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new UserExceptions("Store not found"));
        store.setStatus(status);
        return StoreMapper.toDTO(storeRepository.save(store));
    }
}