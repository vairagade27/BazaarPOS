package com.codexaa.repository;

import com.codexaa.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BranchRepository extends JpaRepository<Branch,Long> {
    List<Branch> findByStoreId(Long storeId);
}
