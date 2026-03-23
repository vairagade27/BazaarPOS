package com.codexaa.controller;

import com.codexaa.dto.ShiftDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.model.User;
import com.codexaa.service.ShiftService;
import com.codexaa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;
    private final UserService  userService;

    /**
     * POST /api/shifts/{storeId}/start
     * Body: { "branchId": 5, "notes": "Morning shift" }
     * Cashier starts their shift.
     */
    @PostMapping("/{storeId}/start")
    @PreAuthorize("hasAnyRole('CASHIER','BRANCH_MANAGER','STORE_MANAGER','STORE_ADMIN')")
    public ResponseEntity<ShiftDto> startShift(
            @PathVariable Long storeId,
            @RequestBody ShiftDto.StartRequest req) throws UserExceptions {
        User current = userService.getCurrentUser();
        return ResponseEntity.ok(shiftService.startShift(storeId, current.getId(), req));
    }

    /**
     * PUT /api/shifts/{storeId}/{shiftId}/close
     * Body: { "notes": "End of morning shift" }
     * Cashier closes their own shift — totals calculated automatically.
     */
    @PutMapping("/{storeId}/{shiftId}/close")
    @PreAuthorize("hasAnyRole('CASHIER','BRANCH_MANAGER','STORE_MANAGER','STORE_ADMIN')")
    public ResponseEntity<ShiftDto> closeShift(
            @PathVariable Long storeId,
            @PathVariable Long shiftId,
            @RequestBody(required = false) ShiftDto.CloseRequest req) throws UserExceptions {
        User current = userService.getCurrentUser();
        return ResponseEntity.ok(shiftService.closeShift(storeId, shiftId, current.getId(), req));
    }

    /**
     * GET /api/shifts/active
     * Returns the currently open shift for the logged-in cashier.
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('CASHIER','BRANCH_MANAGER','STORE_MANAGER','STORE_ADMIN')")
    public ResponseEntity<ShiftDto> getActiveShift() throws UserExceptions {
        User current = userService.getCurrentUser();
        return ResponseEntity.ok(shiftService.getActiveShift(current.getId()));
    }

    /**
     * GET /api/shifts/my
     * Returns all shifts for the logged-in cashier.
     */
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('CASHIER','BRANCH_MANAGER','STORE_MANAGER','STORE_ADMIN')")
    public ResponseEntity<List<ShiftDto>> myShifts() throws UserExceptions {
        User current = userService.getCurrentUser();
        return ResponseEntity.ok(shiftService.getShiftsByCashier(current.getId()));
    }

    /**
     * GET /api/shifts/{storeId}/{shiftId}
     * Get a specific shift by ID (store-scoped).
     */
    @GetMapping("/{storeId}/{shiftId}")
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER','BRANCH_MANAGER','CASHIER')")
    public ResponseEntity<ShiftDto> getShiftById(
            @PathVariable Long storeId,
            @PathVariable Long shiftId) throws UserExceptions {
        return ResponseEntity.ok(shiftService.getShiftById(storeId, shiftId));
    }

    /**
     * GET /api/shifts/{storeId}
     * All shifts for a store — managers and admins only.
     */
    @GetMapping("/{storeId}")
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER','BRANCH_MANAGER')")
    public ResponseEntity<List<ShiftDto>> getShiftsByStore(
            @PathVariable Long storeId) throws UserExceptions {
        return ResponseEntity.ok(shiftService.getShiftsByStore(storeId));
    }

    /**
     * GET /api/shifts/branch/{branchId}
     * All shifts for a specific branch.
     */
    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('STORE_ADMIN','STORE_MANAGER','BRANCH_MANAGER')")
    public ResponseEntity<List<ShiftDto>> getShiftsByBranch(
            @PathVariable Long branchId) throws UserExceptions {
        return ResponseEntity.ok(shiftService.getShiftsByBranch(branchId));
    }
}