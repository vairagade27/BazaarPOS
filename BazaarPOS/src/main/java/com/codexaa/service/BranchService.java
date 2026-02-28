package com.codexaa.service;

import com.codexaa.dto.BranchDTO;
import com.codexaa.exception.UserExceptions;
import com.codexaa.model.User;

import java.util.List;

public interface BranchService {

    BranchDTO createBranch(BranchDTO branchDTO , User user) throws UserExceptions;
    BranchDTO updateBranch(Long id ,BranchDTO branchDTO ,User user);
    BranchDTO deleteBranch(Long id);
    List<BranchDTO> getAllBranchesbyStoreId(Long storeId);
    BranchDTO getBranchById(Long id);

}
