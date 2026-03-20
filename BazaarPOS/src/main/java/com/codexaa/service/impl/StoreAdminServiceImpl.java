package com.codexaa.service.impl;

import com.codexaa.domain.OrderStatus;
import com.codexaa.domain.UserRole;
import com.codexaa.dto.*;
import com.codexaa.exception.UserExceptions;
import com.codexaa.model.*;
import com.codexaa.repository.*;
import com.codexaa.service.StoreAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StoreAdminServiceImpl implements StoreAdminService {

    private final StoreRepository     storeRepository;
    private final BranchRepository    branchRepository;
    private final ProductRepository   productRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderRepository     orderRepository;
    private final UserRepository      userRepository;
    private final PasswordEncoder     passwordEncoder;

    // Items with Inventory.quantity <= this are "low stock"
    private static final int LOW_STOCK_THRESHOLD = 10;

    // ══════════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════════════════

    private Store requireStore(Long storeId) throws UserExceptions {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new UserExceptions.NotFoundException(
                        "Store not found: " + storeId));
    }

    private Branch requireBranch(Long storeId, Long branchId) throws UserExceptions {
        return branchRepository.findByIdAndStoreId(branchId, storeId)
                .orElseThrow(() -> new UserExceptions.NotFoundException(
                        "Branch " + branchId + " not found for store " + storeId));
    }

    private Product requireProduct(Long storeId, Long productId) throws UserExceptions {
        return productRepository.findByIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new UserExceptions.NotFoundException(
                        "Product " + productId + " not found for store " + storeId));
    }

    private BigDecimal nullSafe(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DASHBOARD
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats(Long storeId) throws UserExceptions {
        requireStore(storeId);

        LocalDateTime now        = LocalDateTime.now();
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime weekStart  = LocalDate.now().minusDays(6).atStartOfDay();
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        BigDecimal totalRevenue = nullSafe(orderRepository.getTotalRevenueByStoreId(storeId));
        BigDecimal todayRevenue = nullSafe(orderRepository.getRevenueByStoreIdBetween(storeId, todayStart, now));
        BigDecimal weekRevenue  = nullSafe(orderRepository.getRevenueByStoreIdBetween(storeId, weekStart, now));
        BigDecimal monthRevenue = nullSafe(orderRepository.getRevenueByStoreIdBetween(storeId, monthStart, now));

        long totalOrders      = orderRepository.countByStoreId(storeId);
        long pendingOrders    = orderRepository.countByStoreIdAndStatus(storeId, OrderStatus.PENDING);
        long processingOrders = orderRepository.countByStoreIdAndStatus(storeId, OrderStatus.PROCESSING);
        long completedOrders  = orderRepository.countByStoreIdAndStatus(storeId, OrderStatus.COMPLETED);
        long cancelledOrders  = orderRepository.countByStoreIdAndStatus(storeId, OrderStatus.CANCELLED);

        BigDecimal avgOrderValue = completedOrders > 0
                ? totalRevenue.divide(BigDecimal.valueOf(completedOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long totalBranches  = branchRepository.countByStoreId(storeId);
        long totalProducts  = productRepository.countByStoreId(storeId);
        long totalEmployees = userRepository.findEmployeesByStoreId(storeId).size();
        long totalCustomers = orderRepository.countDistinctCustomersByStoreId(storeId);

        // Low stock: Inventory.quantity <= LOW_STOCK_THRESHOLD
        long lowStockItems = inventoryRepository.findByStoreId(storeId).stream()
                .filter(i -> i.getQuantity() != null && i.getQuantity() <= LOW_STOCK_THRESHOLD)
                .count();

        // Chart data — last 7 days
        Map<String, Double> revenueByDay = new LinkedHashMap<>();
        Map<String, Long>   ordersByDay  = new LinkedHashMap<>();
        List<String>        labels       = new ArrayList<>();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEE");
        for (int i = 6; i >= 0; i--) {
            String label = LocalDate.now().minusDays(i).format(fmt);
            labels.add(label);
            revenueByDay.put(label, 0.0);
            ordersByDay.put(label, 0L);
        }

        orderRepository.revenuePerDayAfter(storeId, weekStart).forEach(row -> {
            String day = ((java.sql.Date) row[0]).toLocalDate().format(fmt);
            revenueByDay.put(day, nullSafe((BigDecimal) row[1]).doubleValue());
        });
        orderRepository.countOrdersPerDayAfter(storeId, weekStart).forEach(row -> {
            String day = ((java.sql.Date) row[0]).toLocalDate().format(fmt);
            ordersByDay.put(day, (Long) row[1]);
        });

        return DashboardStatsDto.builder()
                .totalRevenue(totalRevenue)
                .todayRevenue(todayRevenue)
                .weekRevenue(weekRevenue)
                .monthRevenue(monthRevenue)
                .avgOrderValue(avgOrderValue)
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .processingOrders(processingOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .totalBranches(totalBranches)
                .totalProducts(totalProducts)
                .totalEmployees(totalEmployees)
                .totalCustomers(totalCustomers)
                .lowStockItems(lowStockItems)
                .chartLabels(labels)
                .chartRevenue(new ArrayList<>(revenueByDay.values()))
                .chartOrders(new ArrayList<>(ordersByDay.values()))
                .build();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  BRANCHES
    //  Branch fields: name, address, phone, email, workingDays,
    //                 openTime, closeTime, store, manager
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<BranchDTO> getBranches(Long storeId) throws UserExceptions {
        requireStore(storeId);
        return branchRepository.findByStoreId(storeId)
                .stream().map(this::toBranchDTO).collect(Collectors.toList());
    }

    @Override
    public BranchDTO createBranch(Long storeId, BranchDTO dto) throws UserExceptions {
        Store store = requireStore(storeId);

        // Resolve manager — User with matching id
        User manager = null;
        if (dto.getManagerId() != null) {
            manager = userRepository.findById(dto.getManagerId())
                    .orElseThrow(() -> new UserExceptions.NotFoundException(
                            "Manager not found: " + dto.getManagerId()));
        }

        Branch branch = Branch.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .workingDays(dto.getWorkingDays())
                .openTime(dto.getOpenTime())
                .closeTime(dto.getCloseTime())
                .store(store)
                .manager(manager)
                .build();

        return toBranchDTO(branchRepository.save(branch));
    }

    @Override
    public BranchDTO updateBranch(Long storeId, Long branchId, BranchDTO dto) throws UserExceptions {
        Branch branch = requireBranch(storeId, branchId);

        if (dto.getName()        != null) branch.setName(dto.getName());
        if (dto.getAddress()     != null) branch.setAddress(dto.getAddress());
        if (dto.getPhone()       != null) branch.setPhone(dto.getPhone());
        if (dto.getEmail()       != null) branch.setEmail(dto.getEmail());
        if (dto.getWorkingDays() != null) branch.setWorkingDays(dto.getWorkingDays());
        if (dto.getOpenTime()    != null) branch.setOpenTime(dto.getOpenTime());
        if (dto.getCloseTime()   != null) branch.setCloseTime(dto.getCloseTime());

        if (dto.getManagerId() != null) {
            User manager = userRepository.findById(dto.getManagerId())
                    .orElseThrow(() -> new UserExceptions.NotFoundException(
                            "Manager not found: " + dto.getManagerId()));
            branch.setManager(manager);
        }

        return toBranchDTO(branchRepository.save(branch));
    }

    @Override
    public void deleteBranch(Long storeId, Long branchId) throws UserExceptions {
        Branch branch = requireBranch(storeId, branchId);
        branchRepository.delete(branch);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PRODUCTS
    //  Product fields: name, brand, sku, price, quantity, store, category
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getProducts(Long storeId) throws UserExceptions {
        requireStore(storeId);
        return productRepository.findByStoreId(storeId)
                .stream().map(this::toProductDTO).collect(Collectors.toList());
    }

    @Override
    public ProductDTO createProduct(Long storeId, ProductDTO dto) throws UserExceptions {
        Store store = requireStore(storeId);

        if (dto.getSku() != null && productRepository.existsBySkuAndStoreId(dto.getSku(), storeId)) {
            throw new UserExceptions.DuplicateResourceException(
                    "Product with SKU '" + dto.getSku() + "' already exists in this store");
        }

        // Resolve category if provided
        Category category = null;
        if (dto.getCategoryId() != null) {
            // assumes you have a CategoryRepository — if not, skip this
            // category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
        }

        Product product = Product.builder()
                .name(dto.getName())
                .brand(dto.getBrand())
                .sku(dto.getSku())
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .store(store)
                .category(category)
                .build();

        return toProductDTO(productRepository.save(product));
    }

    @Override
    public ProductDTO updateProduct(Long storeId, Long productId, ProductDTO dto) throws UserExceptions {
        Product product = requireProduct(storeId, productId);

        if (dto.getName()     != null) product.setName(dto.getName());
        if (dto.getBrand()    != null) product.setBrand(dto.getBrand());
        if (dto.getSku()      != null) product.setSku(dto.getSku());
        if (dto.getPrice()    != null) product.setPrice(dto.getPrice());
        if (dto.getQuantity() != null) product.setQuantity(dto.getQuantity());

        return toProductDTO(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long storeId, Long productId) throws UserExceptions {
        Product product = requireProduct(storeId, productId);
        productRepository.delete(product);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  INVENTORY
    //  Inventory fields: branch, product, quantity, lastUpdated
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDto> getInventory(Long storeId) throws UserExceptions {
        requireStore(storeId);
        return inventoryRepository.findByStoreId(storeId)
                .stream().map(this::toInventoryDto).collect(Collectors.toList());
    }

    @Override
    public InventoryDto updateInventory(Long storeId, Long inventoryId, InventoryDto dto)
            throws UserExceptions {
        Inventory inv = inventoryRepository.findByIdAndBranchStoreId(inventoryId, storeId)
                .orElseThrow(() -> new UserExceptions.NotFoundException(
                        "Inventory record not found: " + inventoryId));

        if (dto.getQuantity() != null) inv.setQuantity(dto.getQuantity());

        return toInventoryDto(inventoryRepository.save(inv));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  EMPLOYEES
    //  User fields: fullName, email, password, phone, role, enabled,
    //               store, branch, lastLogin
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getEmployees(Long storeId) throws UserExceptions {
        requireStore(storeId);
        return userRepository.findEmployeesByStoreId(storeId)
                .stream().map(this::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto addEmployee(Long storeId, UserDto dto) throws UserExceptions {
        Store store = requireStore(storeId);

        if (userRepository.findByEmail(dto.getEmail()) != null) {
            throw new UserExceptions.DuplicateResourceException(
                    "User with email '" + dto.getEmail() + "' already exists");
        }

        Branch branch = null;
        if (dto.getBranchId() != null) {
            branch = requireBranch(storeId, dto.getBranchId());
        }

        User employee = new User();
        employee.setFullName(dto.getFullName());
        employee.setEmail(dto.getEmail());
        employee.setPassword(passwordEncoder.encode(
                dto.getPassword() != null ? dto.getPassword() : "Welcome@123"));
        employee.setPhone(dto.getPhone());
        employee.setRole(dto.getRole() != null ? dto.getRole() : UserRole.ROLE_CASHIER);
        employee.setEnabled(true);
        employee.setStore(store);
        employee.setBranch(branch);

        return toUserDto(userRepository.save(employee));
    }

    @Override
    public UserDto updateEmployee(Long storeId, Long employeeId, UserDto dto) throws UserExceptions {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new UserExceptions.NotFoundException(
                        "Employee not found: " + employeeId));

        if (employee.getStore() == null || !employee.getStore().getId().equals(storeId)) {
            throw new UserExceptions.AccessDeniedException(
                    "Employee does not belong to this store");
        }

        if (dto.getFullName() != null) employee.setFullName(dto.getFullName());
        if (dto.getPhone()    != null) employee.setPhone(dto.getPhone());
        if (dto.getRole()     != null) employee.setRole(dto.getRole());

        if (dto.getBranchId() != null) {
            Branch branch = requireBranch(storeId, dto.getBranchId());
            employee.setBranch(branch);
        }

        return toUserDto(userRepository.save(employee));
    }

    @Override
    public void removeEmployee(Long storeId, Long employeeId) throws UserExceptions {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new UserExceptions.NotFoundException(
                        "Employee not found: " + employeeId));

        if (employee.getStore() == null || !employee.getStore().getId().equals(storeId)) {
            throw new UserExceptions.AccessDeniedException(
                    "Employee does not belong to this store");
        }

        // Soft remove: unlink from store, disable account
        employee.setStore(null);
        employee.setBranch(null);
        employee.setEnabled(false);
        userRepository.save(employee);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ORDERS — delegates to OrderRepository directly
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrders(Long storeId) throws UserExceptions {
        requireStore(storeId);
        return orderRepository.findByStoreIdOrderByCreatedAtDesc(storeId)
                .stream().map(this::toOrderDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long storeId, Long orderId) throws UserExceptions {
        Order order = orderRepository.findByIdAndStoreId(orderId, storeId)
                .orElseThrow(() -> new UserExceptions.NotFoundException(
                        "Order " + orderId + " not found for store " + storeId));
        return toOrderDto(order);
    }

    @Override
    public OrderDto updateOrderStatus(Long storeId, Long orderId, String statusStr)
            throws UserExceptions {
        Order order = orderRepository.findByIdAndStoreId(orderId, storeId)
                .orElseThrow(() -> new UserExceptions.NotFoundException(
                        "Order " + orderId + " not found for store " + storeId));

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UserExceptions.InvalidOrderStatusException(
                    "Invalid status: " + statusStr);
        }

        order.setStatus(newStatus);
        return toOrderDto(orderRepository.save(order));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CUSTOMERS
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getCustomers(Long storeId) throws UserExceptions {
        requireStore(storeId);
        return userRepository.findCustomersByStoreId(storeId)
                .stream().map(this::toUserDto).collect(Collectors.toList());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PRIVATE MAPPERS
    // ══════════════════════════════════════════════════════════════════════════

    // User.fullName, User.enabled, User.lastLogin → UserDto.lastlogin
    private UserDto toUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        // password never mapped in response
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setStoreId(user.getStore()   != null ? user.getStore().getId()   : null);
        dto.setBranchId(user.getBranch() != null ? user.getBranch().getId()  : null);
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setLastlogin(user.getLastLogin()); // entity: lastLogin → dto: lastlogin
        return dto;
    }

    // Branch.manager.fullName, Branch.store, Branch.workingDays
    private BranchDTO toBranchDTO(Branch branch) {
        return BranchDTO.builder()
                .id(branch.getId())
                .name(branch.getName())
                .address(branch.getAddress())
                .phone(branch.getPhone())
                .email(branch.getEmail())
                .workingDays(branch.getWorkingDays())
                .openTime(branch.getOpenTime())
                .closeTime(branch.getCloseTime())
                .storeId(branch.getStore() != null ? branch.getStore().getId() : null)
                .managerId(branch.getManager() != null ? branch.getManager().getId() : null)
                .createdAt(branch.getCreatedAt())
                .updatedAt(branch.getUpdatedAt())
                .build();
    }

    // Product.brand, Product.sku, Product.price, Product.quantity, Product.category
    private ProductDTO toProductDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .sku(product.getSku())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .storeId(product.getStore()    != null ? product.getStore().getId()    : null)
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .build();
    }

    // Inventory.quantity, Inventory.lastUpdated — embeds BranchDTO + ProductDTO
    private InventoryDto toInventoryDto(Inventory inv) {
        BranchDTO  branchDTO  = inv.getBranch()  != null ? toBranchDTO(inv.getBranch())   : null;
        ProductDTO productDTO = inv.getProduct() != null ? toProductDTO(inv.getProduct()) : null;

        int qty = inv.getQuantity() != null ? inv.getQuantity() : 0;

        return InventoryDto.builder()
                .id(inv.getId())
                .branchId(inv.getBranch()  != null ? inv.getBranch().getId()  : null)
                .productId(inv.getProduct() != null ? inv.getProduct().getId() : null)
                .branch(branchDTO)
                .product(productDTO)
                .quantity(qty)
                .lastUpdated(inv.getLastUpdated())
                .build();
    }

    // Order → OrderDto (uses User.fullName, Store.brand)
    private OrderDto toOrderDto(Order order) {
        String customerName  = null;
        String customerPhone = null;
        if (order.getCustomer() != null) {
            customerName  = order.getCustomer().getFullName();
            customerPhone = order.getCustomer().getPhone();
        } else {
            customerName  = order.getGuestCustomerName();
            customerPhone = order.getGuestCustomerPhone();
        }

        List<OrderItemDto> items = order.getItems() != null
                ? order.getItems().stream().map(this::toOrderItemDto).collect(Collectors.toList())
                : Collections.emptyList();

        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus());
        dto.setSubtotal(order.getSubtotal());
        dto.setDiscount(order.getDiscount());
        dto.setTax(order.getTax());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setStoreId(order.getStore()   != null ? order.getStore().getId()   : null);
        dto.setStoreBrand(order.getStore() != null ? order.getStore().getBrand() : null);
        dto.setBranchId(order.getBranch() != null ? order.getBranch().getId()  : null);
        dto.setBranchName(order.getBranch() != null ? order.getBranch().getName() : null);
        dto.setCustomerId(order.getCustomer() != null ? order.getCustomer().getId() : null);
        dto.setCustomerName(customerName);
        dto.setCustomerPhone(customerPhone);
        dto.setShippingAddress(order.getShippingAddress());
        dto.setNotes(order.getNotes());
        dto.setCancellationReason(order.getCancellationReason());
        dto.setItems(items);
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        return dto;
    }

    private OrderItemDto toOrderItemDto(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct() != null ? item.getProduct().getId() : null);
        dto.setProductName(item.getProductName());
        dto.setProductSku(item.getProductSku());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setDiscount(item.getDiscount());
        dto.setLineTotal(item.getLineTotal());
        return dto;
    }
}