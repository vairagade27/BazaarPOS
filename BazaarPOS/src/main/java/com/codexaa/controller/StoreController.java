package com.codexaa.controller;

import com.codexaa.domain.StoreStatus;
import com.codexaa.dto.StoreDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.mapper.StoreMapper;
import com.codexaa.model.User;
import com.codexaa.response.ApiResponse;
import com.codexaa.service.StoreService;
import com.codexaa.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<StoreDto> createStore(
            @RequestBody StoreDto storeDto,
            @RequestHeader("Authorization") String jwt
    ) throws UserExceptions {

        User user = userService.getUserFromJwtToken(jwt);
        return ResponseEntity.ok(storeService.createStore(storeDto, user));
    }

    @GetMapping
    public ResponseEntity<List<StoreDto>> getAllStores() {
        return ResponseEntity.ok(storeService.getAllStores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreDto> getStoreById(@PathVariable Long id)
            throws UserExceptions {

        return ResponseEntity.ok(storeService.getStoreById(id));
    }

    @GetMapping("/admin")
    public ResponseEntity<StoreDto> getStoreByAdmin()
            throws UserExceptions {

        return ResponseEntity.ok(storeService.getStoreByAdmin());
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoreDto> updateStore(
            @PathVariable Long id,
            @RequestBody StoreDto storeDto
    ) throws UserExceptions {

        return ResponseEntity.ok(storeService.updateStore(id, storeDto));
    }

    @PutMapping("/{id}/moderate")
    public ResponseEntity<StoreDto> moderateStore(
            @PathVariable Long id,
            @RequestParam StoreStatus status
    ) throws UserExceptions {

        return ResponseEntity.ok(storeService.moderateStore(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStore(@PathVariable Long id)
            throws UserExceptions {

        storeService.deleteStore(id);
        return ResponseEntity.ok("Store deleted successfully");
    }
}