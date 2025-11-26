package com.anapedra.stock_manager.services;


import com.anapedra.stock_manager.domain.dtos.CategoryDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> findAll();
    CategoryDTO findById(Long id);
    CategoryDTO insert(CategoryDTO dto);
    CategoryDTO update(Long id, CategoryDTO dto);
    void delete(Long id);
}
