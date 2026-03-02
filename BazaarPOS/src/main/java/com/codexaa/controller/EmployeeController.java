package com.codexaa.controller;

import com.codexaa.domain.UserRole;
import com.codexaa.dto.UserDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.model.User;
import com.codexaa.response.ApiResponse;
import com.codexaa.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;


    @PostMapping("/store/{storeId}")
    public UserDto createStoreEmployee(
            @PathVariable Long storeId,
            @RequestBody UserDto employeeDto) throws UserExceptions {

        return employeeService.createStoreEmployee(employeeDto, storeId);
    }


    @PostMapping("/branch/{branchId}")
    public UserDto createBranchEmployee(
            @PathVariable Long branchId,
            @RequestBody UserDto employeeDto) throws UserExceptions {

        return employeeService.createBranchEmployee(employeeDto, branchId);
    }


    @PutMapping("/{employeeId}")
    public User updateEmployee(
            @PathVariable Long employeeId,
            @RequestBody UserDto employeeDto) throws UserExceptions {

        return employeeService.updateEmployee(employeeId, employeeDto);
    }


    @DeleteMapping("/{employeeId}")
    public ApiResponse deleteEmployee(@PathVariable Long employeeId) throws UserExceptions {

        employeeService.deleteEmployee(employeeId);

        ApiResponse response = new ApiResponse();
        response.setMessage("Employee deleted successfully");

        return response;
    }


    @GetMapping("/store/{storeId}")
    public List<User> getStoreEmployees(
            @PathVariable Long storeId,
            @RequestParam(required = false) UserRole role) throws UserExceptions {

        return employeeService.findStoreEmployee(storeId, role);
    }

    @GetMapping("/branch/{branchId}")
    public List<User> getBranchEmployees(
            @PathVariable Long branchId,
            @RequestParam(required = false) UserRole role) throws UserExceptions {

        return employeeService.findBranchEmployee(branchId, role);
    }
}