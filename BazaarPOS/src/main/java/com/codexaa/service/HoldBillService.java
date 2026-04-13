package com.codexaa.service;

import com.codexaa.dto.HoldBillDto;
import com.codexaa.exception.UserExceptions;

import java.util.List;

public interface HoldBillService {
    HoldBillDto       holdBill(Long storeId, Long cashierId, HoldBillDto.Request request) throws UserExceptions;
    List<HoldBillDto> getHeldBills(Long storeId, Long cashierId) throws UserExceptions;
    void              discardHeldBill(Long storeId, Long cashierId, String holdId) throws UserExceptions;
}