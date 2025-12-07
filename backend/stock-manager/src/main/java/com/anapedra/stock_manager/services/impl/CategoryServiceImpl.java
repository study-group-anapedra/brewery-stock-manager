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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

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
        logger.info("SERVICE: Buscando todas as categorias.");
        List<Category> categories = categoryRepository.findAll();
        logger.info("SERVICE: Retornando {} categorias.", categories.size());
        return categories.stream()
                .map(CategoryDTO::new)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        logger.info("SERVICE: Buscando categoria pelo ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("SERVICE WARN: Categoria ID {} não encontrada.", id);
                    return new ResourceNotFoundException("Categoria não encontrada");
                });
        logger.info("SERVICE: Categoria ID {} encontrada.", id);
        return new CategoryDTO(category);
    }


    @Override
    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {
        logger.info("SERVICE: Iniciando inserção da nova categoria: {}", dto.getDescription());
        return categoryCreationUpdateTimer.record(() -> {
            Category category = new Category();
            category.setDescription(dto.getDescription());
            Category savedCategory = categoryRepository.save(category);
            logger.info("SERVICE: Categoria ID {} salva com sucesso.", savedCategory.getId());
            return new CategoryDTO(savedCategory);
        });
    }


    @Override
    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        logger.info("SERVICE: Iniciando atualização da categoria ID: {}", id);
        return categoryCreationUpdateTimer.record(() -> {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("SERVICE WARN: Categoria ID {} não encontrada para atualização.", id);
                        return new ResourceNotFoundException("Categoria não encontrada");
                    });
            category.setDescription(dto.getDescription());
            Category updatedCategory = categoryRepository.save(category);
            logger.info("SERVICE: Categoria ID {} atualizada com sucesso.", id);
            return new CategoryDTO(updatedCategory);
        });
    }


    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        logger.warn("SERVICE: Tentativa de exclusão da categoria ID: {}", id);
        if (!categoryRepository.existsById(id)) {
            logger.error("SERVICE ERROR: Tentativa de exclusão falhou. Recurso ID {} não encontrado.", id);
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try {
            categoryRepository.deleteById(id);
            logger.info("SERVICE: Categoria ID {} excluída com sucesso.", id);
        } catch (DataIntegrityViolationException e) {
            logger.error("SERVICE ERROR: Falha de integridade referencial ao excluir categoria ID {}.", id, e);
            throw new DatabaseException("Falha de integridade referencial");
        }
    }
}