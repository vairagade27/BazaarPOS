package com.codexaa.repository;

import com.codexaa.domain.UserRole;
import com.codexaa.model.Branch;
import com.codexaa.model.Store;
import com.codexaa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ── Your existing methods (unchanged) ─────────────────────────────────────

    User findByEmail(String email);

    List<User> findByStore(Store store);

    List<User> findByBranch(Branch branch);

    List<User> findByRole(UserRole role);

    List<User> findByStoreAndRole(Store store, UserRole role);

    List<User> findByRoleAndEnabledTrue(UserRole role);

    // ── Added — needed by StoreAdminServiceImpl ───────────────────────────────

    boolean existsByEmail(String email);

    // Employees in a store: ROLE_CASHIER, ROLE_BRANCH_MANAGER, ROLE_STORE_MANAGER
    @Query("SELECT u FROM User u WHERE u.store.id = :storeId " +
            "AND u.role IN ('ROLE_CASHIER', 'ROLE_BRANCH_MANAGER', 'ROLE_STORE_MANAGER')")
    List<User> findEmployeesByStoreId(@Param("storeId") Long storeId);

    // Customers who placed orders in a store
    @Query("SELECT DISTINCT o.customer FROM Order o " +
            "WHERE o.store.id = :storeId AND o.customer IS NOT NULL")
    List<User> findCustomersByStoreId(@Param("storeId") Long storeId);
}