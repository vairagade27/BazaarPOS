package com.codexaa.controller;

import com.codexaa.dto.InventoryDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.response.ApiResponse;
import com.codexaa.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;


    @PostMapping
    public InventoryDto createInventory(@RequestBody InventoryDto dto) throws UserExceptions {
        return inventoryService.createInventory(dto);
    }


    @PutMapping("/{id}")
    public InventoryDto updateInventory(@PathVariable Long id,
                                        @RequestBody InventoryDto dto) throws UserExceptions {
        return inventoryService.updateInventory(id, dto);
    }


    @DeleteMapping("/{id}")
    public ApiResponse deleteInventory(@PathVariable Long id) throws UserExceptions {

        inventoryService.deleteInventory(id);

        ApiResponse response = new ApiResponse();
        response.setMessage("Inventory deleted successfully");

        return response;
    }


    @GetMapping("/{id}")
    public InventoryDto getInventoryById(@PathVariable Long id) throws UserExceptions {
        return inventoryService.getInventoryById(id);
    }


    @GetMapping("/product/{productId}/branch/{branchId}")
    public InventoryDto getByProductAndBranch(@PathVariable Long productId,
                                              @PathVariable Long branchId) throws UserExceptions {
        return inventoryService.findByProductIdAndBranchId(productId, branchId);
    }


    @GetMapping("/branch/{branchId}")
    public List<InventoryDto> getAllByBranch(@PathVariable Long branchId) {
        return inventoryService.getAllInventoryByBranchId(branchId);
    }
}