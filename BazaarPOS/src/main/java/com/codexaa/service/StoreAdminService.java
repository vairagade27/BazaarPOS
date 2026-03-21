package com.codexaa.service;

import com.codexaa.dto.*;
import com.codexaa.exception.UserExceptions;

import java.util.List;

public interface StoreAdminService {


    DashboardStatsDto getDashboardStats(Long storeId) throws UserExceptions;


    List<BranchDTO> getBranches(Long storeId) throws UserExceptions;
    BranchDTO createBranch(Long storeId, BranchDTO dto) throws UserExceptions;
    BranchDTO updateBranch(Long storeId, Long branchId, BranchDTO dto) throws UserExceptions;
    void deleteBranch(Long storeId, Long branchId) throws UserExceptions;


    List<ProductDTO> getProducts(Long storeId) throws UserExceptions;
    ProductDTO createProduct(Long storeId, ProductDTO dto) throws UserExceptions;
    ProductDTO updateProduct(Long storeId, Long productId, ProductDTO dto) throws UserExceptions;
    void deleteProduct(Long storeId, Long productId) throws UserExceptions;

    // ── Inventory — uses InventoryDto (your exact class name) ────────────────
    List<InventoryDto> getInventory(Long storeId) throws UserExceptions;
    InventoryDto updateInventory(Long storeId, Long inventoryId, InventoryDto dto) throws UserExceptions;


    List<UserDto> getEmployees(Long storeId) throws UserExceptions;
    UserDto addEmployee(Long storeId, UserDto dto) throws UserExceptions;
    UserDto updateEmployee(Long storeId, Long employeeId, UserDto dto) throws UserExceptions;
    void removeEmployee(Long storeId, Long employeeId) throws UserExceptions;


    List<OrderDto> getOrders(Long storeId) throws UserExceptions;
    OrderDto getOrderById(Long storeId, Long orderId) throws UserExceptions;
    OrderDto updateOrderStatus(Long storeId, Long orderId, String status) throws UserExceptions;


    List<UserDto> getCustomers(Long storeId) throws UserExceptions;
}