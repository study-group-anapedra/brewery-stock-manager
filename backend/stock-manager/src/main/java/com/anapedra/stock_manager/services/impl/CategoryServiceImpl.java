package com.anapedra.stock_manager.services.impl;

import com.anapedra.stock_manager.domain.dtos.CategoryDTO;
import com.anapedra.stock_manager.domain.entities.Category;
import com.anapedra.stock_manager.repositories.CategoryRepository;
import com.anapedra.stock_manager.services.CategoryService;
import com.anapedra.stock_manager.services.exceptions.DatabaseException;
import com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final Timer categoryCreationUpdateTimer;

    public CategoryServiceImpl(CategoryRepository categoryRepository, MeterRegistry registry) {
        this.categoryRepository = categoryRepository;
        this.categoryCreationUpdateTimer = Timer.builder("stock_manager.category.creation_update_time")
                .description("Tempo de execução da criação ou atualização de categorias")
                .register(registry);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(CategoryDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
        return new CategoryDTO(category);
    }

    @Override
    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {
        return categoryCreationUpdateTimer.record(() -> {
            Category category = new Category();
            category.setDescription(dto.getDescription());
            Category savedCategory = categoryRepository.save(category);
            return new CategoryDTO(savedCategory);
        });
    }

    @Override
    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        return categoryCreationUpdateTimer.record(() -> {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
            category.setDescription(dto.getDescription());
            Category updatedCategory = categoryRepository.save(category);
            return new CategoryDTO(updatedCategory);
        });
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try {
            categoryRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }
}