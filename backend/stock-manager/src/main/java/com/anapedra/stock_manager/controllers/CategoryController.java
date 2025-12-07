package com.anapedra.stock_manager.controllers;

import com.anapedra.stock_manager.domain.dtos.CategoryDTO;
import com.anapedra.stock_manager.services.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping
    public List<CategoryDTO> findAll() {
        logger.info("CONTROLLER: GET /categories iniciado. Buscando todas as categorias.");
        List<CategoryDTO> categories = categoryService.findAll();
        logger.info("CONTROLLER: GET /categories finalizado. Status: 200 OK. Total de categorias: {}", categories.size());
        return categories;
    }


    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) {
        logger.info("CONTROLLER: GET /categories/{} iniciado.", id);
        ResponseEntity<CategoryDTO> response = ResponseEntity.ok(categoryService.findById(id));
        logger.info("CONTROLLER: GET /categories/{} finalizado. Status: 200 OK.", id);
        return response;
    }


    @PostMapping
    public ResponseEntity<CategoryDTO> insert(@RequestBody CategoryDTO categoryDTO) {
        logger.info("CONTROLLER: POST /categories iniciado. Inserindo nova categoria: {}", categoryDTO.getDescription());
        
        CategoryDTO created = categoryService.insert(categoryDTO);
        
        logger.info("CONTROLLER: POST /categories finalizado. Status: 201 CREATED. Nova categoria ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> update(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        logger.info("CONTROLLER: PUT /categories/{} iniciado. Atualizando descrição para: {}", id, categoryDTO.getDescription());
        
        CategoryDTO updated = categoryService.update(id, categoryDTO);
        
        logger.info("CONTROLLER: PUT /categories/{} finalizado. Status: 200 OK.", id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.warn("CONTROLLER: DELETE /categories/{} iniciado. Tentativa de exclusão.", id);
        
        categoryService.delete(id);
        
        logger.info("CONTROLLER: DELETE /categories/{} finalizado. Status: 204 NO CONTENT.", id);
        return ResponseEntity.noContent().build();
    }
}