package com.codexaa.service.impl;

import com.codexaa.domain.OrderStatus;
import com.codexaa.dto.OrderDto;
import com.codexaa.dto.OrderItemDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.mapper.EntityMapper;
import com.codexaa.model.*;
import com.codexaa.repository.*;
import com.codexaa.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository     orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final StoreRepository     storeRepository;
    private final BranchRepository    branchRepository;
    private final ProductRepository   productRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository      userRepository;
    private final EntityMapper        mapper;

    private Store requireStore(Long storeId) throws UserExceptions {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new UserExceptions.NotFoundException("Store not found: " + storeId));
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

    private Order requireOrder(Long storeId, Long orderId) throws UserExceptions {
        return orderRepository.findByIdAndStoreId(orderId, storeId)
                .orElseThrow(() -> new UserExceptions.NotFoundException(
                        "Order " + orderId + " not found for store " + storeId));
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }

    private Inventory requireInventory(Long productId, Long branchId, String productName, String branchName)
            throws UserExceptions {
        List<Inventory> results = inventoryRepository.findAllByProductIdAndBranchId(productId, branchId);
        if (results.isEmpty()) {
            throw new UserExceptions.NotFoundException(
                    "No inventory for product '" + productName + "' in branch '" + branchName + "'");
        }
        return results.get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrders(Long storeId) throws UserExceptions {
        requireStore(storeId);
        return mapper.toOrderDtoList(
                orderRepository.findByStoreIdOrderByCreatedAtDesc(storeId));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long storeId, Long orderId) throws UserExceptions {
        return mapper.toOrderDto(requireOrder(storeId, orderId));
    }

    @Override
    public OrderDto createOrder(Long storeId, OrderDto.CreateRequest req) throws UserExceptions {

        Store store = requireStore(storeId);

        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new UserExceptions("Order must contain at least one item");
        }

        Branch branch;
        if (req.getBranchId() != null) {
            branch = requireBranch(storeId, req.getBranchId());
        } else {
            branch = branchRepository.findByStoreId(storeId)
                    .stream().findFirst()
                    .orElseThrow(() -> new UserExceptions.NotFoundException(
                            "No branch found for store: " + storeId));
        }
        final Branch finalBranch = branch;

        User customer = null;
        if (req.getCustomerId() != null) {
            customer = userRepository.findById(req.getCustomerId())
                    .orElseThrow(() -> new UserExceptions.NotFoundException(
                            "Customer not found: " + req.getCustomerId()));
            if (!customer.isEnabled()) {
                throw new UserExceptions("Customer account is disabled");
            }
        }

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .status(OrderStatus.PENDING)
                .store(store)
                .branch(finalBranch)
                .customer(customer)
                .guestCustomerName(req.getGuestCustomerName())
                .guestCustomerPhone(req.getGuestCustomerPhone())
                .shippingAddress(req.getShippingAddress())
                .notes(req.getNotes())
                .discount(req.getDiscount() != null ? req.getDiscount() : BigDecimal.ZERO)
                .tax(req.getTax()           != null ? req.getTax()      : BigDecimal.ZERO)
                .subtotal(BigDecimal.ZERO)
                .totalPrice(BigDecimal.ZERO)
                .build();

        for (OrderItemDto.Request itemReq : req.getItems()) {

            if (itemReq.getProductId() == null) {
                throw new UserExceptions("Each item must have a productId");
            }
            if (itemReq.getQuantity() == null || itemReq.getQuantity() <= 0) {
                throw new UserExceptions("Each item must have a quantity greater than 0");
            }

            Product product = requireProduct(storeId, itemReq.getProductId());

            Inventory inv = requireInventory(
                    product.getId(), finalBranch.getId(),
                    product.getName(), finalBranch.getName());

            int availableQty = inv.getQuantity() != null ? inv.getQuantity() : 0;

            if (availableQty < itemReq.getQuantity()) {
                throw new UserExceptions.InsufficientStockException(
                        "Insufficient stock for '" + product.getName()
                                + "'. Available: " + availableQty
                                + ", Requested: " + itemReq.getQuantity());
            }

            inv.setQuantity(availableQty - itemReq.getQuantity());
            inventoryRepository.save(inv);

            BigDecimal unitPrice    = itemReq.getUnitPrice() != null ? itemReq.getUnitPrice() : product.getPrice();
            BigDecimal itemDiscount = itemReq.getDiscount()  != null ? itemReq.getDiscount()  : BigDecimal.ZERO;

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .productSku(product.getSku())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(unitPrice)
                    .discount(itemDiscount)
                    .lineTotal(BigDecimal.ZERO)
                    .build();

            order.addItem(orderItem);
        }

        order.recalculate();
        Order saved = orderRepository.save(order);
        log.info("Order created: {} for store: {}", saved.getOrderNumber(), storeId);
        return mapper.toOrderDto(saved);
    }

    @Override
    public OrderDto updateOrderStatus(Long storeId, Long orderId, String statusStr)
            throws UserExceptions {

        Order order = requireOrder(storeId, orderId);

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UserExceptions.InvalidOrderStatusException(
                    "Invalid status '" + statusStr + "'. Valid: "
                            + Arrays.toString(OrderStatus.values()));
        }

        OrderStatus current = order.getStatus();
        validateStatusTransition(current, newStatus);

        if (newStatus == OrderStatus.CANCELLED
                && (current == OrderStatus.PENDING || current == OrderStatus.PROCESSING)) {
            restoreStock(order);
        }

        order.setStatus(newStatus);
        Order saved = orderRepository.save(order);
        log.info("Order {} status: {} -> {}", order.getOrderNumber(), current, newStatus);
        return mapper.toOrderDto(saved);
    }

    @Override
    public OrderDto cancelOrder(Long storeId, Long orderId, String reason) throws UserExceptions {
        Order order = requireOrder(storeId, orderId);

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new UserExceptions.InvalidOrderStatusException(
                    "Completed orders cannot be cancelled. Use REFUNDED instead.");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new UserExceptions.InvalidOrderStatusException("Order is already cancelled.");
        }

        restoreStock(order);
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancellationReason(reason);

        log.info("Order {} cancelled. Reason: {}", order.getOrderNumber(), reason);
        return mapper.toOrderDto(orderRepository.save(order));
    }

    private void restoreStock(Order order) {
        if (order.getBranch() == null) return;
        Long branchId = order.getBranch().getId();

        for (OrderItem item : order.getItems()) {
            List<Inventory> results = inventoryRepository
                    .findAllByProductIdAndBranchId(item.getProduct().getId(), branchId);

            if (!results.isEmpty()) {
                Inventory inv = results.get(0);
                int current   = inv.getQuantity() != null ? inv.getQuantity() : 0;
                inv.setQuantity(current + item.getQuantity());
                inventoryRepository.save(inv);
            }
        }
        log.info("Stock restored for order: {}", order.getOrderNumber());
    }

    private void validateStatusTransition(OrderStatus from, OrderStatus to)
            throws UserExceptions.InvalidOrderStatusException {

        Map<OrderStatus, Set<OrderStatus>> allowed = Map.of(
                OrderStatus.PENDING,    Set.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED),
                OrderStatus.PROCESSING, Set.of(OrderStatus.COMPLETED,  OrderStatus.CANCELLED),
                OrderStatus.COMPLETED,  Set.of(OrderStatus.REFUNDED),
                OrderStatus.CANCELLED,  Set.of(),
                OrderStatus.REFUNDED,   Set.of()
        );

        Set<OrderStatus> next = allowed.getOrDefault(from, Set.of());
        if (!next.contains(to)) {
            throw new UserExceptions.InvalidOrderStatusException(
                    "Cannot transition from " + from + " to " + to + ". Allowed: " + next);
        }
    }
}