package com.codexaa.repository;

import com.codexaa.domain.StoreStatus;
import com.codexaa.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {

    Store findByStoreAdminId(Long adminId);

    long countByStatus(StoreStatus status);

    List<Store> findByStatus(StoreStatus status);

}