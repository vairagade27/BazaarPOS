package com.codexaa.mapper;

import com.codexaa.dto.StoreDto;
import com.codexaa.model.Store;
import com.codexaa.model.User;

public class StoreMapper {


    public static StoreDto toDTO(Store store) {

        if (store == null) {
            return null;
        }

        StoreDto storeDto = new StoreDto();

        storeDto.setId(store.getId());
        storeDto.setBrand(store.getBrand());
        storeDto.setDescription(store.getDescription());
        storeDto.setStoreType(store.getStoreType());
        storeDto.setContact(store.getContact());
        storeDto.setCreatedAt(store.getCreatedAt());
        storeDto.setUpdatedAt(store.getUpdatedAt());
        storeDto.setStatus(store.getStatus());

        if (store.getStoreAdmin() != null) {
            storeDto.setStorAdmin(UserMapper.mapToDto(store.getStoreAdmin()));
        }

        return storeDto;
    }


    public static Store toEntity(StoreDto storeDto, User storeAdmin) {

        if (storeDto == null) {
            return null;
        }

        Store store = new Store();


        store.setBrand(storeDto.getBrand());
        store.setDescription(storeDto.getDescription());
        store.setStoreType(storeDto.getStoreType());
        store.setContact(storeDto.getContact());

        // 🔥 IMPORTANT
        store.setStoreAdmin(storeAdmin);

        return store;
    }
}