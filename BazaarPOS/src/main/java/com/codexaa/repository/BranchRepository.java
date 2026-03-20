package com.codexaa.repository;

import com.codexaa.model.Branch;
import com.codexaa.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    List<Branch> findByStoreId(Long storeId);

    List<Branch> findByStore(Store store);

    // Only active branches — used when assigning default branch to an order
    List<Branch> findByStoreIdAndActiveTrue(Long storeId);

    // Ownership check
    Optional<Branch> findByIdAndStoreId(Long branchId, Long storeId);

    boolean existsByNameAndStoreId(String name, Long storeId);

    long countByStoreId(Long storeId);

    @Query("SELECT b FROM Branch b WHERE b.manager.id = :managerId")
    Optional<Branch> findByManagerId(@Param("managerId") Long managerId);
}