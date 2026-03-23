package com.codexaa.service;

import com.codexaa.dto.CashierDashboardDto;
import com.codexaa.exception.UserExceptions;

public interface CashierDashboardService {
    CashierDashboardDto getDashboard(Long storeId, Long branchId, Long cashierId) throws UserExceptions;
}