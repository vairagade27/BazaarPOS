package com.codexaa.service.impl;

import com.codexaa.domain.UserRole;
import com.codexaa.dto.CategoryDTO;
import com.codexaa.exception.UserExceptions;
import com.codexaa.mapper.CategoryMapper;
import com.codexaa.mapper.ProductMapper;
import com.codexaa.model.Category;
import com.codexaa.model.Product;
import com.codexaa.model.Store;
import com.codexaa.model.User;
import com.codexaa.repository.CategoryRepository;
import com.codexaa.repository.StoreRepository;
import com.codexaa.service.CategoryService;
import com.codexaa.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private  final CategoryRepository categoryRepository;
    private final UserService userService;
    private final StoreRepository storeRepository;
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) throws UserExceptions {

        User user=userService.getCurrentUser();
        Store store=storeRepository.findById(categoryDTO.getId()).orElseThrow(()-> new UserExceptions("Store Not found"));
        Category category = Category.builder()
                .name(categoryDTO.getName())
                .store(store)
                .build();

        Category saved = categoryRepository.save(category);

        return CategoryMapper.toDTO(saved);
    }

    @Override
    public List<CategoryDTO> getCategoryByStore(Long StoreId) {
        List<Category> categories = categoryRepository.findBytoreId(StoreId);

        return categories.stream()
                .map(CategoryMapper::toDTO)
                .collect(Collectors.toList());

    }
    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) throws UserExceptions {

        User user = userService.getCurrentUser();

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new UserExceptions("Category Not Found"));

        // Ownership check
        if (!category.getStore().getStoreAdmin().getId().equals(user.getId())) {
            throw new UserExceptions("You are not allowed to update this category");
        }

        // Update only if not null (safer for partial updates)
        if (categoryDTO.getName() != null) {
            category.setName(categoryDTO.getName());
        }

        Category updatedCategory = categoryRepository.save(category);

        return CategoryMapper.toDTO(updatedCategory);
    }


    @Override
    public void deleteCategory(Long id) throws UserExceptions {

        User user = userService.getCurrentUser();

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new UserExceptions("Category Not Found"));

        if (!category.getStore().getStoreAdmin().getId().equals(user.getId())) {
            throw new UserExceptions("You are not allowed to delete this category");
        }

        categoryRepository.delete(category);
    }

    private void  checkAuthority(User user,Store store) throws Exception {
        boolean isAdmin=user.getRole().equals(UserRole.ROLE_STORE_ADMIN);
        boolean isMagager=user.getRole().equals(UserRole.ROLE_BRANCH_MANAGER);
        boolean isSameStore=user.equals(store.getStoreAdmin());

        if(!(isAdmin && isSameStore) && !isMagager){
            throw new Exception("You dont have permision to manege this category");
        }
    }
}
