package com.codexaa.service.impl;

import com.codexaa.dto.BranchDTO;
import com.codexaa.exception.UserExceptions;
import com.codexaa.mapper.BranchMapper;
import com.codexaa.model.Branch;
import com.codexaa.model.Store;
import com.codexaa.model.User;
import com.codexaa.repository.BranchRepository;
import com.codexaa.repository.StoreRepository;
import com.codexaa.repository.UserRepository;
import com.codexaa.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Override
    public BranchDTO createBranch(BranchDTO branchDTO, User user) throws UserExceptions {


        Store store = storeRepository.findByStoreAdminId(user.getId());

        if (store == null) {
            throw new UserExceptions("Store not found for this user");
        }


        User manager = null;
        if (branchDTO.getManagerId() != null) {
            manager = userRepository.findById(branchDTO.getManagerId())
                    .orElseThrow(() -> new UserExceptions("Manager not found"));
        }


        Branch branch = BranchMapper.toEntity(branchDTO, store, manager);


        Branch savedBranch = branchRepository.save(branch);

        return BranchMapper.toDTO(savedBranch);
    }


    @Override
    public BranchDTO updateBranch(Long id, BranchDTO branchDTO, User user) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));


        if (!branch.getStore().getStoreAdmin().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to update this branch");
        }

        branch.setName(branchDTO.getName());
        branch.setAddress(branchDTO.getAddress());
        branch.setPhone(branchDTO.getPhone());
        branch.setEmail(branchDTO.getEmail());
        branch.setWorkingDays(branchDTO.getWorkingDays());
        branch.setOpenTime(branchDTO.getOpenTime());
        branch.setCloseTime(branchDTO.getCloseTime());


        if (branchDTO.getManagerId() != null) {
            User manager = userRepository.findById(branchDTO.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
            branch.setManager(manager);
        }

        Branch updatedBranch = branchRepository.save(branch);

        return BranchMapper.toDTO(updatedBranch);
    }


    @Override
    public BranchDTO deleteBranch(Long id) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        branchRepository.delete(branch);

        return BranchMapper.toDTO(branch);
    }


    @Override
    public List<BranchDTO> getAllBranchesbyStoreId(Long storeId) {

        List<Branch> branches = branchRepository.findByStoreId(storeId);

        return branches.stream()
                .map(BranchMapper::toDTO)
                .toList();
    }


    @Override
    public BranchDTO getBranchById(Long id) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        return BranchMapper.toDTO(branch);
    }
}