package com.codexaa.service.impl;

import com.codexaa.dto.ProductDTO;
import com.codexaa.exception.UserExceptions;
import com.codexaa.mapper.ProductMapper;
import com.codexaa.model.Category;
import com.codexaa.model.Product;
import com.codexaa.model.Store;
import com.codexaa.repository.CategoryRepository;
import com.codexaa.repository.ProductRepository;
import com.codexaa.repository.StoreRepository;
import com.codexaa.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ProductDTO create(ProductDTO dto) throws UserExceptions {

        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new UserExceptions("Store not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new UserExceptions("Category not found"));

        if (productRepository.existsBySkuAndStoreId(dto.getSku(), store.getId())) {
            throw new UserExceptions("SKU already exists in store");
        }

        Product product = ProductMapper.toEntity(dto, store, category);

        return ProductMapper.toDTO(productRepository.save(product));
    }

    @Override
    public Page<ProductDTO> getByStore(Long storeId, String keyword, Pageable pageable) {

        if (keyword == null || keyword.isBlank()) {
            return productRepository.findByStoreId(storeId, pageable)
                    .map(ProductMapper::toDTO);
        }

        return productRepository.searchByKeyword(storeId, keyword, pageable)
                .map(ProductMapper::toDTO);
    }

    @Override
    public ProductDTO update(Long id, ProductDTO dto) throws UserExceptions {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new UserExceptions("Product not found"));

        ProductMapper.updateEntity(product, dto);

        return ProductMapper.toDTO(productRepository.save(product));
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}