package com.codexaa.controller;

import com.codexaa.dto.CategoryDTO;
import com.codexaa.exception.UserExceptions;
import com.codexaa.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryDTO create(@RequestBody CategoryDTO dto) throws UserExceptions {
        return categoryService.createCategory(dto);
    }

    @GetMapping("/store/{storeId}")
    public Page<CategoryDTO> getByStore(
            @PathVariable Long storeId,
            Pageable pageable) {
        return categoryService.getCategoriesByStore(storeId, pageable);
    }

    @PutMapping("/{id}")
    public CategoryDTO update(
            @PathVariable Long id,
            @RequestBody CategoryDTO dto) throws UserExceptions {
        return categoryService.updateCategory(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws UserExceptions {
        categoryService.deleteCategory(id);
    }
}