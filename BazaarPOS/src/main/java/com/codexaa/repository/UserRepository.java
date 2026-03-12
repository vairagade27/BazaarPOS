package com.codexaa.repository;

import com.codexaa.domain.UserRole;
import com.codexaa.model.Branch;
import com.codexaa.model.Store;
import com.codexaa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

    User findByEmail(String email);

    List<User> findByStore(Store store);

    List<User> findByBranch(Branch branch);

    List<User> findByRole(UserRole role);

    List<User> findByStoreAndRole(Store store, UserRole role);

}