package com.codexaa.service;

import com.codexaa.domain.UserRole;
import com.codexaa.dto.UserDto;
import com.codexaa.exception.UserExceptions;

import java.util.List;

public interface EmployeeService {

    UserDto createStoreEmployee(UserDto employee, Long storeId) throws UserExceptions;

    UserDto createBranchEmployee(UserDto employee, Long branchId) throws UserExceptions;

    UserDto updateEmployee(Long employeeId, UserDto employeeDetails) throws UserExceptions;

    void deleteEmployee(Long employeeId) throws UserExceptions;

    List<UserDto> findStoreEmployee(Long storeId, UserRole role) throws UserExceptions;

    List<UserDto> findBranchEmployee(Long branchId, UserRole role) throws UserExceptions;
}