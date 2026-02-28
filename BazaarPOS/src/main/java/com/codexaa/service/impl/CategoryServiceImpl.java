package com.codexaa.service.impl;

import com.codexaa.domain.UserRole;
import com.codexaa.dto.CategoryDTO;
import com.codexaa.exception.UserExceptions;
import com.codexaa.mapper.CategoryMapper;
import com.codexaa.model.Category;
import com.codexaa.model.Store;
import com.codexaa.model.User;
import com.codexaa.repository.CategoryRepository;
import com.codexaa.repository.StoreRepository;
import com.codexaa.service.CategoryService;
import com.codexaa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;
    private final UserService userService;

    @Override
    public CategoryDTO createCategory(CategoryDTO dto) throws UserExceptions {

        User user = userService.getCurrentUser();

        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new UserExceptions("Store not found"));

        checkAuthority(user, store);

        if (categoryRepository.existsByNameAndStoreId(dto.getName(), store.getId())) {
            throw new UserExceptions("Category already exists in this store");
        }

        Category category = CategoryMapper.toEntity(dto, store);

        return CategoryMapper.toDTO(categoryRepository.save(category));
    }

    @Override
    public Page<CategoryDTO> getCategoriesByStore(Long storeId, Pageable pageable) {
        return categoryRepository.findByStoreId(storeId, pageable)
                .map(CategoryMapper::toDTO);
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO dto) throws UserExceptions {

        User user = userService.getCurrentUser();

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new UserExceptions("Category not found"));

        checkAuthority(user, category.getStore());

        if (dto.getName() != null &&
                !dto.getName().equals(category.getName()) &&
                categoryRepository.existsByNameAndStoreId(dto.getName(),
                        category.getStore().getId())) {
            throw new UserExceptions("Category already exists in this store");
        }

        CategoryMapper.updateEntity(category, dto);

        return CategoryMapper.toDTO(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long id) throws UserExceptions {

        User user = userService.getCurrentUser();

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new UserExceptions("Category not found"));

        checkAuthority(user, category.getStore());

        categoryRepository.delete(category);
    }

    private void checkAuthority(User user, Store store) throws UserExceptions {

        boolean isStoreAdmin =
                user.getRole().equals(UserRole.ROLE_STORE_ADMIN)
                        && user.getId().equals(store.getStoreAdmin().getId());

        boolean isBranchManager =
                user.getRole().equals(UserRole.ROLE_BRANCH_MANAGER);

        if (!isStoreAdmin && !isBranchManager) {
            throw new UserExceptions("No permission");
        }
    }
}