package com.codexaa.service.impl;

import com.codexaa.domain.OrderStatus;
import com.codexaa.domain.ShiftStatus;
import com.codexaa.dto.ShiftDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.model.*;
import com.codexaa.repository.*;
import com.codexaa.service.ShiftService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository   shiftRepository;
    private final UserRepository    userRepository;
    private final BranchRepository  branchRepository;
    private final StoreRepository   storeRepository;
    private final OrderRepository   orderRepository;

    // ── Start shift ───────────────────────────────────────────────────────────

    @Override
    public ShiftDto startShift(Long storeId, Long cashierId, ShiftDto.StartRequest req)
            throws UserExceptions {

        // Check no open shift already exists for this cashier
        shiftRepository.findByCashierIdAndStatus(cashierId, ShiftStatus.OPEN)
                .ifPresent(s -> {
                    throw new RuntimeException(
                            "You already have an open shift: " + s.getShiftNumber()
                                    + ". Please close it before starting a new one.");
                });

        User cashier = userRepository.findById(cashierId)
                .orElseThrow(() -> new UserExceptions.NotFoundException("Cashier not found: " + cashierId));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new UserExceptions.NotFoundException("Store not found: " + storeId));

        Long branchId = req.getBranchId() != null ? req.getBranchId() : cashier.getBranch() != null
                ? cashier.getBranch().getId() : null;

        if (branchId == null) {
            throw new UserExceptions("Branch ID is required to start a shift");
        }

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new UserExceptions.NotFoundException("Branch not found: " + branchId));

        Shift shift = Shift.builder()
                .shiftNumber("SHF-" + System.currentTimeMillis())
                .cashier(cashier)
                .branch(branch)
                .store(store)
                .status(ShiftStatus.OPEN)
                .startTime(LocalDateTime.now())
                .notes(req.getNotes())
                .build();

        Shift saved = shiftRepository.save(shift);
        log.info("Shift started: {} by cashier: {}", saved.getShiftNumber(), cashierId);
        return toDto(saved);
    }

    // ── Close shift ───────────────────────────────────────────────────────────

    @Override
    public ShiftDto closeShift(Long storeId, Long shiftId, Long cashierId, ShiftDto.CloseRequest req)
            throws UserExceptions {

        Shift shift = shiftRepository.findByIdAndStoreId(shiftId, storeId)
                .orElseThrow(() -> new UserExceptions.NotFoundException("Shift not found: " + shiftId));

        if (shift.getStatus() == ShiftStatus.CLOSED) {
            throw new UserExceptions("Shift is already closed");
        }

        if (!shift.getCashier().getId().equals(cashierId)) {
            throw new UserExceptions("You can only close your own shift");
        }

        // Calculate totals from orders placed during this shift window
        List<Order> shiftOrders = orderRepository
                .findByStoreIdOrderByCreatedAtDesc(storeId)
                .stream()
                .filter(o -> o.getBranch() != null
                        && o.getBranch().getId().equals(shift.getBranch().getId())
                        && !o.getCreatedAt().isBefore(shift.getStartTime()))
                .collect(Collectors.toList());

        BigDecimal revenue   = shiftOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount  = shiftOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .map(o -> o.getDiscount() != null ? o.getDiscount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax       = shiftOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .map(o -> o.getTax() != null ? o.getTax() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal refunds   = shiftOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.REFUNDED)
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netSales  = revenue.subtract(discount).subtract(refunds);

        long completed  = shiftOrders.stream().filter(o -> o.getStatus() == OrderStatus.COMPLETED).count();
        long cancelled  = shiftOrders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count();
        long refunded   = shiftOrders.stream().filter(o -> o.getStatus() == OrderStatus.REFUNDED).count();

        shift.setStatus(ShiftStatus.CLOSED);
        shift.setEndTime(LocalDateTime.now());
        shift.setTotalRevenue(revenue);
        shift.setTotalDiscount(discount);
        shift.setTotalTax(tax);
        shift.setTotalRefunds(refunds);
        shift.setNetSales(netSales);
        shift.setTotalOrders(shiftOrders.size());
        shift.setCompletedOrders(completed);
        shift.setCancelledOrders(cancelled);
        shift.setRefundedOrders(refunded);
        if (req != null && req.getNotes() != null) shift.setNotes(req.getNotes());

        Shift saved = shiftRepository.save(shift);
        log.info("Shift closed: {} | revenue: {} | orders: {}", saved.getShiftNumber(), revenue, shiftOrders.size());
        return toDto(saved);
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public ShiftDto getActiveShift(Long cashierId) throws UserExceptions {
        return shiftRepository.findByCashierIdAndStatus(cashierId, ShiftStatus.OPEN)
                .map(this::toDto)
                .orElseThrow(() -> new UserExceptions.NotFoundException("No active shift found"));
    }

    @Override
    @Transactional(readOnly = true)
    public ShiftDto getShiftById(Long storeId, Long shiftId) throws UserExceptions {
        return toDto(shiftRepository.findByIdAndStoreId(shiftId, storeId)
                .orElseThrow(() -> new UserExceptions.NotFoundException("Shift not found: " + shiftId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftDto> getShiftsByStore(Long storeId) throws UserExceptions {
        storeRepository.findById(storeId)
                .orElseThrow(() -> new UserExceptions.NotFoundException("Store not found: " + storeId));
        return shiftRepository.findByStoreIdOrderByStartTimeDesc(storeId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftDto> getShiftsByBranch(Long branchId) throws UserExceptions {
        return shiftRepository.findByBranchIdOrderByStartTimeDesc(branchId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftDto> getShiftsByCashier(Long cashierId) {
        return shiftRepository.findByCashierIdOrderByStartTimeDesc(cashierId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private ShiftDto toDto(Shift s) {
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
        dto.setTotalDiscount(s.getTotalDiscount());
        dto.setTotalTax(s.getTotalTax());
        dto.setTotalRefunds(s.getTotalRefunds());
        dto.setNetSales(s.getNetSales());
        dto.setTotalOrders(s.getTotalOrders());
        dto.setCompletedOrders(s.getCompletedOrders());
        dto.setCancelledOrders(s.getCancelledOrders());
        dto.setRefundedOrders(s.getRefundedOrders());
        dto.setNotes(s.getNotes());
        dto.setCreatedAt(s.getCreatedAt());
        return dto;
    }
}