package com.codexaa.repository;

import com.codexaa.domain.ShiftStatus;
import com.codexaa.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    // Active (OPEN) shift for a cashier
    Optional<Shift> findByCashierIdAndStatus(Long cashierId, ShiftStatus status);

    // Active shift for a branch
    Optional<Shift> findByBranchIdAndStatus(Long branchId, ShiftStatus status);

    // All shifts for a store (newest first)
    List<Shift> findByStoreIdOrderByStartTimeDesc(Long storeId);

    // All shifts for a branch (newest first)
    List<Shift> findByBranchIdOrderByStartTimeDesc(Long branchId);

    // All shifts for a cashier (newest first)
    List<Shift> findByCashierIdOrderByStartTimeDesc(Long cashierId);

    // Ownership check
    @Query("SELECT s FROM Shift s WHERE s.id = :id AND s.store.id = :storeId")
    Optional<Shift> findByIdAndStoreId(@Param("id") Long id, @Param("storeId") Long storeId);
}