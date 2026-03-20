package com.codexaa.mapper;

import com.codexaa.dto.*;
import com.codexaa.model.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityMapper {

    // ══════════════════════════════════════════════════════════════════════════
    //  USER — uses User.fullName, User.enabled, User.lastLogin (your exact fields)
    // ══════════════════════════════════════════════════════════════════════════

    public UserDto toUserDto(User user) {
        if (user == null) return null;
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

    public List<UserDto> toUserDtoList(List<User> users) {
        if (users == null) return Collections.emptyList();
        return users.stream().map(this::toUserDto).collect(Collectors.toList());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ORDER — uses User.fullName for customerName, Store.brand for storeBrand
    // ══════════════════════════════════════════════════════════════════════════

    public OrderDto toOrderDto(Order order) {
        if (order == null) return null;

        // User.fullName is the name field (not firstName/lastName)
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
                ? order.getItems().stream()
                .map(this::toOrderItemDto)
                .collect(Collectors.toList())
                : Collections.emptyList();

        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus());
        dto.setSubtotal(order.getSubtotal());
        dto.setDiscount(order.getDiscount());
        dto.setTax(order.getTax());
        dto.setTotalPrice(order.getTotalPrice());
        // Store.brand is your store name field
        dto.setStoreId(order.getStore() != null ? order.getStore().getId() : null);
        dto.setStoreBrand(order.getStore() != null ? order.getStore().getBrand() : null);
        dto.setBranchId(order.getBranch() != null ? order.getBranch().getId() : null);
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

    public List<OrderDto> toOrderDtoList(List<Order> orders) {
        if (orders == null) return Collections.emptyList();
        return orders.stream().map(this::toOrderDto).collect(Collectors.toList());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ORDER ITEM — uses Product.name, Product.sku, Product.price
    // ══════════════════════════════════════════════════════════════════════════

    public OrderItemDto toOrderItemDto(OrderItem item) {
        if (item == null) return null;
        OrderItemDto dto = new OrderItemDto();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct() != null ? item.getProduct().getId() : null);
        dto.setProductName(item.getProductName());  // snapshot field
        dto.setProductSku(item.getProductSku());    // snapshot field
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setDiscount(item.getDiscount());
        dto.setLineTotal(item.getLineTotal());
        return dto;
    }
}