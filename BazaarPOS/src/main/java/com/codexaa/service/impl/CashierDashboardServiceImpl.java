package com.codexaa.service.impl;

import com.codexaa.domain.OrderStatus;
import com.codexaa.domain.ShiftStatus;
import com.codexaa.dto.CashierDashboardDto;
import com.codexaa.dto.CashierDashboardDto.TopProductDto;
import com.codexaa.dto.ShiftDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.model.Inventory;
import com.codexaa.model.Order;
import com.codexaa.model.OrderItem;
import com.codexaa.model.Shift;
import com.codexaa.repository.InventoryRepository;
import com.codexaa.repository.OrderRepository;
import com.codexaa.repository.ShiftRepository;
import com.codexaa.service.CashierDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CashierDashboardServiceImpl implements CashierDashboardService {

    private final OrderRepository     orderRepository;
    private final InventoryRepository inventoryRepository;
    private final ShiftRepository     shiftRepository;

    private static final int LOW_STOCK_THRESHOLD = 10;

    @Override
    public CashierDashboardDto getDashboard(Long storeId, Long branchId, Long cashierId)
            throws UserExceptions {

        LocalDateTime now        = LocalDateTime.now();
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime weekStart  = LocalDate.now().minusDays(6).atStartOfDay();

        // ── All store orders ──────────────────────────────────────────────────
        List<Order> allOrders = orderRepository.findByStoreIdOrderByCreatedAtDesc(storeId);

        // ── Today's orders for this branch ────────────────────────────────────
        List<Order> todayBranchOrders = allOrders.stream()
                .filter(o -> o.getBranch() != null
                        && o.getBranch().getId().equals(branchId)
                        && !o.getCreatedAt().isBefore(todayStart))
                .collect(Collectors.toList());

        long todayOrders     = todayBranchOrders.size();
        long pendingOrders   = todayBranchOrders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING   || o.getStatus() == OrderStatus.PROCESSING).count();
        long completedOrders = todayBranchOrders.stream().filter(o -> o.getStatus() == OrderStatus.COMPLETED).count();
        long cancelledOrders = todayBranchOrders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count();

        BigDecimal todayRevenue = todayBranchOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ── Inventory alerts for this branch ──────────────────────────────────
        List<Inventory> branchInventory = inventoryRepository.findByBranchId(branchId);
        long lowStock    = branchInventory.stream().filter(i -> i.getQuantity() != null && i.getQuantity() > 0  && i.getQuantity() <= LOW_STOCK_THRESHOLD).count();
        long outOfStock  = branchInventory.stream().filter(i -> i.getQuantity() == null || i.getQuantity() == 0).count();

        // ── Active shift ──────────────────────────────────────────────────────
        ShiftDto activeShift = shiftRepository
                .findByCashierIdAndStatus(cashierId, ShiftStatus.OPEN)
                .map(this::toShiftDto)
                .orElse(null);

        // ── Top products today (from completed branch orders) ─────────────────
        Map<String, int[]> productMap = new LinkedHashMap<>();
        Map<String, BigDecimal> revenueMap = new LinkedHashMap<>();

        todayBranchOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .flatMap(o -> o.getItems() != null ? o.getItems().stream() : java.util.stream.Stream.empty())
                .forEach(item -> {
                    String name = item.getProductName() != null ? item.getProductName() : "Product #" + item.getProduct().getId();
                    productMap.merge(name, new int[]{item.getQuantity() != null ? item.getQuantity() : 0}, (a, b) -> new int[]{a[0] + b[0]});
                    revenueMap.merge(name, item.getLineTotal() != null ? item.getLineTotal() : BigDecimal.ZERO, BigDecimal::add);
                });

        List<TopProductDto> topProducts = productMap.entrySet().stream()
                .map(e -> TopProductDto.builder()
                        .productName(e.getKey())
                        .quantitySold(e.getValue()[0])
                        .revenue(revenueMap.getOrDefault(e.getKey(), BigDecimal.ZERO))
                        .build())
                .sorted(Comparator.comparingInt(TopProductDto::getQuantitySold).reversed())
                .limit(5)
                .collect(Collectors.toList());

        // ── Weekly chart for this branch (last 7 days) ────────────────────────
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEE");
        Map<String, Double> revenueByDay = new LinkedHashMap<>();
        Map<String, Long>   ordersByDay  = new LinkedHashMap<>();
        List<String>        labels       = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            String label = LocalDate.now().minusDays(i).format(fmt);
            labels.add(label);
            revenueByDay.put(label, 0.0);
            ordersByDay.put(label, 0L);
        }

        allOrders.stream()
                .filter(o -> o.getBranch() != null
                        && o.getBranch().getId().equals(branchId)
                        && !o.getCreatedAt().isBefore(weekStart)
                        && o.getStatus() == OrderStatus.COMPLETED)
                .forEach(o -> {
                    String day = o.getCreatedAt().toLocalDate().format(fmt);
                    revenueByDay.merge(day, o.getTotalPrice() != null ? o.getTotalPrice().doubleValue() : 0.0, Double::sum);
                    ordersByDay.merge(day, 1L, Long::sum);
                });

        return CashierDashboardDto.builder()
                .todayOrders(todayOrders)
                .pendingOrders(pendingOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .todayRevenue(todayRevenue)
                .lowStockItems(lowStock)
                .outOfStockItems(outOfStock)
                .activeShift(activeShift)
                .topProducts(topProducts)
                .chartLabels(labels)
                .chartRevenue(new ArrayList<>(revenueByDay.values()))
                .chartOrders(new ArrayList<>(ordersByDay.values()))
                .build();
    }

    private ShiftDto toShiftDto(Shift s) {
        ShiftDto dto = new ShiftDto();
        dto.setId(s.getId());
        dto.setShiftNumber(s.getShiftNumber());
        dto.setStatus(s.getStatus());
        dto.setCashierId(s.getCashier() != null ? s.getCashier().getId() : null);
        dto.setCashierName(s.getCashier() != null ? s.getCashier().getFullName() : null);
        dto.setBranchId(s.getBranch() != null ? s.getBranch().getId() : null);
        dto.setBranchName(s.getBranch() != null ? s.getBranch().getName() : null);
        dto.setStoreId(s.getStore() != null ? s.getStore().getId() : null);
        dto.setStartTime(s.getStartTime());
        dto.setEndTime(s.getEndTime());
        dto.setTotalRevenue(s.getTotalRevenue());
        dto.setTotalOrders(s.getTotalOrders());
        dto.setNotes(s.getNotes());
        dto.setCreatedAt(s.getCreatedAt());
        return dto;
    }
}