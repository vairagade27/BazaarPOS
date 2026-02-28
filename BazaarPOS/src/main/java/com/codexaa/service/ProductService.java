package com.codexaa.service;

import com.codexaa.dto.ProductDTO;
import com.codexaa.exception.UserExceptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductDTO create(ProductDTO dto) throws UserExceptions;

    Page<ProductDTO> getByStore(Long storeId, String keyword, Pageable pageable);

    ProductDTO update(Long id, ProductDTO dto) throws UserExceptions;

    void delete(Long id);
}