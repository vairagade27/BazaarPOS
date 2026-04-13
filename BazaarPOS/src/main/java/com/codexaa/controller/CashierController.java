package com.codexaa.controller;

import com.codexaa.dto.CashierDashboardDto;
import com.codexaa.dto.HoldBillDto;
import com.codexaa.dto.InventoryDto;
import com.codexaa.dto.OrderDto;
import com.codexaa.dto.ProductDTO;
import com.codexaa.exception.UserExceptions;
import com.codexaa.model.User;
import com.codexaa.service.CashierDashboardService;
import com.codexaa.service.HoldBillService;
import com.codexaa.service.InventoryService;
import com.codexaa.service.OrderService;
import com.codexaa.service.StoreAdminService;
import com.codexaa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cashier")
@PreAuthorize("hasAnyRole('CASHIER','BRANCH_MANAGER','STORE_MANAGER','STORE_ADMIN')")
@RequiredArgsConstructor
public class CashierController {

    private final StoreAdminService       storeAdminService;
    private final InventoryService        inventoryService;
    private final OrderService            orderService;
    private final UserService             userService;
    private final CashierDashboardService cashierDashboardService;
    private final HoldBillService         holdBillService;

    // ── Dashboard ─────────────────────────────────────────────────────────────

    // GET /api/cashier/{storeId}/dashboard
    @GetMapping("/{storeId}/dashboard")
    public ResponseEntity<CashierDashboardDto> getDashboard(
            @PathVariable Long storeId) throws UserExceptions {
        User current  = userService.getCurrentUser();
        Long branchId = current.getBranch() != null ? current.getBranch().getId() : null;
        if (branchId == null) {
            throw new UserExceptions("No branch assigned to your account");
        }
        return ResponseEntity.ok(
                cashierDashboardService.getDashboard(storeId, branchId, current.getId()));
    }

    // ── Products & Inventory ──────────────────────────────────────────────────

    // GET /api/cashier/{storeId}/products
    // Supports optional ?search=name|sku|barcode query param for fast product search
    @GetMapping("/{storeId}/products")
    public ResponseEntity<List<ProductDTO>> getProducts(
            @PathVariable Long storeId,
            @RequestParam(required = false) String search) throws UserExceptions {
        List<ProductDTO> products = storeAdminService.getProducts(storeId);
        if (search != null && !search.isBlank()) {
            String q = search.trim().toLowerCase();
            products = products.stream()
                    .filter(p ->
                            (p.getName() != null && p.getName().toLowerCase().contains(q)) ||
                                    (p.getSku()  != null && p.getSku().toLowerCase().contains(q))  ||
                                    (p.getBarcode() != null && p.getBarcode().toLowerCase().contains(q)))
                    .toList();
        }
        return ResponseEntity.ok(products);
    }

    // GET /api/cashier/{storeId}/inventory/branch/{branchId}
    @GetMapping("/{storeId}/inventory/branch/{branchId}")
    public ResponseEntity<List<InventoryDto>> getInventoryByBranch(
            @PathVariable Long storeId,
            @PathVariable Long branchId) {
        return ResponseEntity.ok(inventoryService.getAllInventoryByBranchId(branchId));
    }

    // ── Orders ────────────────────────────────────────────────────────────────

    // POST /api/cashier/{storeId}/orders
    // CreateRequest already carries: branchId, customerId, guestCustomerName,
    // guestCustomerPhone, shippingAddress, notes (use for paymentMode), discount, tax, items
    @PostMapping("/{storeId}/orders")
    public ResponseEntity<OrderDto> createOrder(
            @PathVariable Long storeId,
            @RequestBody OrderDto.CreateRequest request) throws UserExceptions {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(storeId, request));
    }

    // GET /api/cashier/{storeId}/orders
    @GetMapping("/{storeId}/orders")
    public ResponseEntity<List<OrderDto>> getOrders(
            @PathVariable Long storeId) throws UserExceptions {
        return ResponseEntity.ok(orderService.getOrders(storeId));
    }

    // GET /api/cashier/{storeId}/orders/{orderId}
    @GetMapping("/{storeId}/orders/{orderId}")
    public ResponseEntity<OrderDto> getOrderById(
            @PathVariable Long storeId,
            @PathVariable Long orderId) throws UserExceptions {
        return ResponseEntity.ok(orderService.getOrderById(storeId, orderId));
    }

    // PUT /api/cashier/{storeId}/orders/{orderId}/status
    @PutMapping("/{storeId}/orders/{orderId}/status")
    @PreAuthorize("hasAnyRole('BRANCH_MANAGER','STORE_MANAGER','STORE_ADMIN')")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long storeId,
            @PathVariable Long orderId,
            @RequestParam String status) throws UserExceptions {
        return ResponseEntity.ok(orderService.updateOrderStatus(storeId, orderId, status));
    }

    // PUT /api/cashier/{storeId}/orders/{orderId}/cancel
    @PutMapping("/{storeId}/orders/{orderId}/cancel")
    @PreAuthorize("hasAnyRole('BRANCH_MANAGER','STORE_MANAGER','STORE_ADMIN')")
    public ResponseEntity<OrderDto> cancelOrder(
            @PathVariable Long storeId,
            @PathVariable Long orderId,
            @RequestParam(required = false) String reason) throws UserExceptions {
        return ResponseEntity.ok(orderService.cancelOrder(storeId, orderId, reason));
    }

    // ── Hold Bill (POS feature) ───────────────────────────────────────────────

    // POST /api/cashier/{storeId}/hold
    // Saves the current cart state server-side so the cashier can resume it later.
    // The held bill is NOT an order — it has no effect on inventory until converted.
    @PostMapping("/{storeId}/hold")
    public ResponseEntity<HoldBillDto> holdBill(
            @PathVariable Long storeId,
            @RequestBody HoldBillDto.Request request) throws UserExceptions {
        User current = userService.getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(holdBillService.holdBill(storeId, current.getId(), request));
    }

    // GET /api/cashier/{storeId}/hold
    // Returns all held bills for the current cashier in this store
    @GetMapping("/{storeId}/hold")
    public ResponseEntity<List<HoldBillDto>> getHeldBills(
            @PathVariable Long storeId) throws UserExceptions {
        User current = userService.getCurrentUser();
        return ResponseEntity.ok(holdBillService.getHeldBills(storeId, current.getId()));
    }

    // DELETE /api/cashier/{storeId}/hold/{holdId}
    // Resume (consume) or discard a held bill
    @DeleteMapping("/{storeId}/hold/{holdId}")
    public ResponseEntity<Void> discardHeldBill(
            @PathVariable Long storeId,
            @PathVariable String holdId) throws UserExceptions {
        User current = userService.getCurrentUser();
        holdBillService.discardHeldBill(storeId, current.getId(), holdId);
        return ResponseEntity.noContent().build();
    }
}