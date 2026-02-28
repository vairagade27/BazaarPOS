package com.codexaa.service;

import com.codexaa.domain.StoreStatus;
import com.codexaa.dto.StoreDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.model.Store;
import com.codexaa.model.User;

import java.util.List;
public interface StoreService {

    StoreDto createStore(StoreDto storeDto, User user) throws UserExceptions;

    StoreDto getStoreById(Long id) throws UserExceptions;

    List<StoreDto> getAllStores();

    StoreDto getStoreByAdmin() throws UserExceptions;

    StoreDto updateStore(Long id, StoreDto storeDto) throws UserExceptions;

    void deleteStore(Long id) throws UserExceptions;

    StoreDto moderateStore(Long id, StoreStatus status) throws UserExceptions;
}