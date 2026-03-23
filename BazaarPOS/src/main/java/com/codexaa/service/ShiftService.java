package com.codexaa.service;

import com.codexaa.dto.ShiftDto;
import com.codexaa.exception.UserExceptions;

import java.util.List;

public interface ShiftService {

    ShiftDto startShift(Long storeId, Long cashierId, ShiftDto.StartRequest req) throws UserExceptions;

    ShiftDto closeShift(Long storeId, Long shiftId, Long cashierId, ShiftDto.CloseRequest req) throws UserExceptions;

    ShiftDto getActiveShift(Long cashierId) throws UserExceptions;

    ShiftDto getShiftById(Long storeId, Long shiftId) throws UserExceptions;

    List<ShiftDto> getShiftsByStore(Long storeId) throws UserExceptions;

    List<ShiftDto> getShiftsByBranch(Long branchId) throws UserExceptions;

    List<ShiftDto> getShiftsByCashier(Long cashierId);
}