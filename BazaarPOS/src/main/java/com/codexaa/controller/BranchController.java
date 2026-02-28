package com.codexaa.controller;

import com.codexaa.dto.BranchDTO;
import com.codexaa.exception.UserExceptions;
import com.codexaa.model.User;
import com.codexaa.service.BranchService;
import com.codexaa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class BranchController {

    private final BranchService branchService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<BranchDTO> createBranch(
            @RequestBody BranchDTO branchDTO) throws UserExceptions {

        User currentUser = userService.getCurrentUser();

        BranchDTO createdBranch = branchService.createBranch(branchDTO, currentUser);

        return ResponseEntity.ok(createdBranch);
    }


    @PutMapping("/{id}")
    public ResponseEntity<BranchDTO> updateBranch(
            @PathVariable Long id,
            @RequestBody BranchDTO branchDTO) throws UserExceptions {

        User currentUser = userService.getCurrentUser();

        BranchDTO updatedBranch = branchService.updateBranch(id, branchDTO, currentUser);

        return ResponseEntity.ok(updatedBranch);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BranchDTO> deleteBranch(@PathVariable Long id) {

        BranchDTO deletedBranch = branchService.deleteBranch(id);

        return ResponseEntity.ok(deletedBranch);
    }


    @GetMapping("/{id}")
    public ResponseEntity<BranchDTO> getBranchById(@PathVariable Long id) {

        BranchDTO branchDTO = branchService.getBranchById(id);

        return ResponseEntity.ok(branchDTO);
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<BranchDTO>> getBranchesByStore(
            @PathVariable Long storeId) {

        List<BranchDTO> branches =
                branchService.getAllBranchesbyStoreId(storeId);

        return ResponseEntity.ok(branches);
    }
}