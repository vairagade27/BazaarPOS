package com.codexaa.service.impl;

import com.codexaa.dto.InventoryDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.mapper.InventoryMapper;
import com.codexaa.model.Branch;
import com.codexaa.model.Inventory;
import com.codexaa.model.Product;
import com.codexaa.repository.BranchRepository;
import com.codexaa.repository.InventoryRepository;
import com.codexaa.repository.ProductRepository;
import com.codexaa.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final BranchRepository branchRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @Override
    public InventoryDto createInventory(InventoryDto dto) throws UserExceptions {

        Branch branch = branchRepository.findById(dto.getBranchId())
                .orElseThrow(() -> new UserExceptions("Branch not found"));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new UserExceptions("Product not found"));

        Inventory inventory = InventoryMapper.toEntity(dto, branch, product);

        Inventory savedInventory = inventoryRepository.save(inventory);

        return InventoryMapper.toDTO(savedInventory);
    }

    @Override
    public InventoryDto updateInventory(Long id, InventoryDto dto) throws UserExceptions {

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new UserExceptions("Inventory not found"));

        inventory.setQuantity(dto.getQuantity());

        Inventory updatedInventory = inventoryRepository.save(inventory);

        return InventoryMapper.toDTO(updatedInventory);
    }

    @Override
    public void deleteInventory(Long id) throws UserExceptions {

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new UserExceptions("Inventory not found"));

        inventoryRepository.delete(inventory);
    }

    @Override
    public InventoryDto getInventoryById(Long id) throws UserExceptions {

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new UserExceptions("Inventory not found"));

        return InventoryMapper.toDTO(inventory);
    }

    @Override
    public InventoryDto findByProductIdAndBranchId(Long productId, Long branchId) throws UserExceptions {

        Inventory inventory = inventoryRepository
                .findByProductIdAndBranchId(productId, branchId)
                .orElseThrow(() -> new UserExceptions("Inventory not found"));

        return InventoryMapper.toDTO(inventory);
    }

    @Override
    public List<InventoryDto> getAllInventoryByBranchId(Long branchId) {

        List<Inventory> inventories = inventoryRepository.findByBranchId(branchId);

        return inventories.stream()
                .map(InventoryMapper::toDTO)
                .collect(Collectors.toList());
    }
}