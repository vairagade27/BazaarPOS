package com.codexaa.repository;

import com.codexaa.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // All inventory across all branches of a store
    @Query("SELECT i FROM Inventory i WHERE i.branch.store.id = :storeId")
    List<Inventory> findByStoreId(@Param("storeId") Long storeId);

    List<Inventory> findByBranchId(Long branchId);

    Optional<Inventory> findByProductIdAndBranchId(Long productId, Long branchId);

    // Ownership check — inventory belongs to a branch that belongs to the store
    @Query("SELECT i FROM Inventory i WHERE i.id = :id AND i.branch.store.id = :storeId")
    Optional<Inventory> findByIdAndBranchStoreId(@Param("id") Long id,
                                                 @Param("storeId") Long storeId);
}