package com.codexaa.service;

import com.codexaa.domain.UserRole;
import com.codexaa.dto.UserDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.model.User;

import java.util.List;

public interface EmployeeService {

    UserDto createStoreEmployee(UserDto employee, Long storeId) throws UserExceptions;
    UserDto createBranchEmployee(UserDto employee, Long branchId) throws UserExceptions;
    User updateEmployee(Long employeeId,UserDto employeeDetails) throws UserExceptions;

    void deleteEmployee(Long emoloyeeId) throws UserExceptions;
    List<User> findStoreEmployee(Long storeId , UserRole role) throws UserExceptions;
    List<User> findBranchEmployee(Long storeId , UserRole role) throws UserExceptions;



}
