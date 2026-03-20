package com.codexaa.service;

import com.codexaa.dto.OrderDto;
import com.codexaa.exception.UserExceptions;

import java.util.List;

public interface OrderService {

    List<OrderDto> getOrders(Long storeId) throws UserExceptions;

    OrderDto getOrderById(Long storeId, Long orderId) throws UserExceptions;

    OrderDto createOrder(Long storeId, OrderDto.CreateRequest request) throws UserExceptions;

    OrderDto updateOrderStatus(Long storeId, Long orderId, String status) throws UserExceptions;

    OrderDto cancelOrder(Long storeId, Long orderId, String reason) throws UserExceptions;
}