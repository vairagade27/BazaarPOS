package com.codexaa.controller;

import com.codexaa.dto.ProductDTO;
import com.codexaa.exception.UserExceptions;
import com.codexaa.model.User;
import com.codexaa.response.ApiResponse;
import com.codexaa.service.ProductService;
import com.codexaa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;


    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(
            @RequestBody ProductDTO productDTO,
            @RequestHeader("Authorization") String jwt
    ) throws UserExceptions {

        User user = userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(productService.createProduct(productDTO, user));
    }


    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<ProductDTO>> getByStoreId(
            @PathVariable Long storeId
    ) {

        return ResponseEntity.ok(productService.getProductsByStoreId(storeId));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDTO productDTO,
            @RequestHeader("Authorization") String jwt
    ) throws UserExceptions {

        User user = userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(productService.updateProduct(id, productDTO, user));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt
    ) throws UserExceptions {

        User user = userService.getUserFromJwtToken(jwt);

        productService.deleteProduct(id, user);

        ApiResponse response = new ApiResponse();
        response.setMessage("Product deleted successfully");

        return ResponseEntity.ok(response);
    }


    @GetMapping("/store/{storeId}/search")
    public ResponseEntity<List<ProductDTO>> searchByKeyword(
            @PathVariable Long storeId,
            @RequestParam String keyword
    ) {

        return ResponseEntity.ok(
                productService.searchProducts(storeId, keyword)
        );
    }
}