package com.codexaa.service.impl;

import com.codexaa.domain.StoreStatus;
import com.codexaa.dto.StoreDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.mapper.StoreMapper;
import com.codexaa.model.Store;
import com.codexaa.model.StoreContact;
import com.codexaa.model.User;
import com.codexaa.repository.StoreRepository;
import com.codexaa.service.StoreService;
import com.codexaa.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final UserService userService;

    @Override
    public StoreDto createStore(StoreDto storeDto, User user) {

        Store store = StoreMapper.toEntity(storeDto, user);
        store.setStatus(StoreStatus.PENDING);
        store.setCreatedAt(LocalDateTime.now());

        Store saved = storeRepository.save(store);

        return StoreMapper.toDTO(saved);
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

        if (!store.getStoreAdmin().getId().equals(currentUser.getId())) {
            throw new UserExceptions("You don't have permission to update this store");
        }

        store.setBrand(storeDto.getBrand());
        store.setDescription(storeDto.getDescription());
        store.setStoreType(storeDto.getStoreType());
        store.setUpdatedAt(LocalDateTime.now());

        Store updated = storeRepository.save(store);

        return StoreMapper.toDTO(updated);
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

        Store updated = storeRepository.save(store);

        return StoreMapper.toDTO(updated);
    }
}