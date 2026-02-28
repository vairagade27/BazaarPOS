package com.codexaa.controller;

import com.codexaa.dto.ProductDTO;
import com.codexaa.exception.UserExceptions;
import com.codexaa.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ProductDTO create(@RequestBody ProductDTO dto) throws UserExceptions {
        return productService.create(dto);
    }

    @GetMapping("/store/{storeId}")
    public Page<ProductDTO> getByStore(
            @PathVariable Long storeId,
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return productService.getByStore(storeId, keyword, pageable);
    }

    @PutMapping("/{id}")
    public ProductDTO update(
            @PathVariable Long id,
            @RequestBody ProductDTO dto) throws UserExceptions {
        return productService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}