package com.codexaa.service.impl;

import com.codexaa.domain.UserRole;
import com.codexaa.dto.UserDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.mapper.UserMapper;
import com.codexaa.model.Branch;
import com.codexaa.model.Store;
import com.codexaa.model.User;
import com.codexaa.repository.BranchRepository;
import com.codexaa.repository.StoreRepository;
import com.codexaa.repository.UserRepository;
import com.codexaa.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto createStoreEmployee(UserDto employee, Long storeId) throws UserExceptions {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new UserExceptions("Store not found"));

        Branch branch = null;

        if (employee.getRole() == UserRole.ROLE_BRANCH_MANAGER) {

            if (employee.getBranchId() == null) {
                throw new UserExceptions("Branch ID is required for Branch Manager");
            }

            branch = branchRepository.findById(employee.getBranchId())
                    .orElseThrow(() -> new UserExceptions("Branch not found"));

            if (!branch.getStore().getId().equals(storeId)) {

                throw new UserExceptions("Branch does not belong to this store");
            }
        }

        User user = UserMapper.toEntity(employee);
        user.setStore(store);
        user.setBranch(branch);
        user.setPassword(passwordEncoder.encode(employee.getPassword()));

        User savedUser = userRepository.save(user);

        if (employee.getRole() == UserRole.ROLE_BRANCH_MANAGER && branch != null) {
            branch.setManager(savedUser);
            branchRepository.save(branch);
        }

        return UserMapper.mapToDto(savedUser);
    }

    @Override
    public UserDto createBranchEmployee(UserDto employee, Long branchId) throws UserExceptions {

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new UserExceptions("Branch not found"));

        if (employee.getRole() == UserRole.ROLE_CASHIER) {

            User user = UserMapper.toEntity(employee);
            user.setBranch(branch);
            user.setStore(branch.getStore());
            user.setPassword(passwordEncoder.encode(employee.getPassword()));

            return UserMapper.mapToDto(userRepository.save(user));
        }

        throw new UserExceptions("Only CASHIER role allowed for branch employees");
    }

    @Override
    public User updateEmployee(Long employeeId, UserDto employeeDetails) throws UserExceptions {

        User user = userRepository.findById(employeeId)
                .orElseThrow(() -> new UserExceptions("Employee not found"));

        if (employeeDetails.getBranchId() != null) {
            Branch branch = branchRepository.findById(employeeDetails.getBranchId())
                    .orElseThrow(() -> new UserExceptions("Branch not found"));
            user.setBranch(branch);
        }

        user.setEmail(employeeDetails.getEmail());
        user.setFullName(employeeDetails.getFullName());

        if (employeeDetails.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(employeeDetails.getPassword()));
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteEmployee(Long employeeId) throws UserExceptions {

        User user = userRepository.findById(employeeId)
                .orElseThrow(() -> new UserExceptions("Employee not found"));

        userRepository.delete(user);
    }

    @Override
    public List<User> findStoreEmployee(Long storeId, UserRole role) throws UserExceptions {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new UserExceptions("Store not found"));

        return userRepository.findByStore(store)
                .stream()
                .filter(user -> role == null || user.getRole() == role)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findBranchEmployee(Long branchId, UserRole role) throws UserExceptions {

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new UserExceptions("Branch not found"));

        return userRepository.findByBranch(branch)
                .stream()
                .filter(user -> role == null || user.getRole() == role)
                .collect(Collectors.toList());
    }
}