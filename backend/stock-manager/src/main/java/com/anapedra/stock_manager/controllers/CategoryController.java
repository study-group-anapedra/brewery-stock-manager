package com.anapedra.stock_manager.controllers;

import com.anapedra.stock_manager.domain.dtos.CategoryDTO;
import com.anapedra.stock_manager.services.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Controlador REST responsável por gerenciar as operações CRUD (Create, Read, Update, Delete)
 * relacionadas à entidade Categoria (Category).
 *
 * <p>Expõe endpoints para listar, buscar por ID, criar, atualizar e deletar categorias.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    /**
     * Logger para registro de eventos e rastreamento de execução.
     */
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    /**
     * Serviço responsável pela lógica de negócio e operações de categorias.
     */
    private final CategoryService categoryService;

    /**
     * Construtor para injeção de dependência do serviço de categorias.
     *
     * @param categoryService O serviço de categorias.
     */
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // ================= GET ALL =================
    /**
     * Retorna uma lista de todas as categorias registradas no sistema.
     *
     * @return {@link ResponseEntity} contendo uma {@link List} de {@link CategoryDTO}.
     */
    @Operation(summary = "List all categories", description = "Returns a list of all categories.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all categories")
    })
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> findAll() {
        logger.info("GET /categories iniciado.");
        List<CategoryDTO> categories = categoryService.findAll();
        logger.info("GET /categories finalizado. Total de categorias: {}", categories.size());
        return ResponseEntity.ok(categories);
    }

    // ================= GET BY ID =================
    /**
     * Retorna os detalhes de uma categoria específica pelo seu ID.
     *
     * @param id O ID da categoria a ser buscada.
     * @return {@link ResponseEntity} contendo o {@link CategoryDTO} da categoria.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     */
    @Operation(summary = "Get category by ID", description = "Returns a single category by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> findById(
            @Parameter(description = "ID da categoria", example = "1") @PathVariable Long id
    ) {
        logger.info("GET /categories/{} iniciado.", id);
        CategoryDTO category = categoryService.findById(id);
        logger.info("GET /categories/{} finalizado.", id);
        return ResponseEntity.ok(category);
    }

    // ================= POST =================
    /**
     * Cria e persiste uma nova categoria.
     *
     * @param categoryDTO O {@link CategoryDTO} contendo os dados da nova categoria.
     * @return {@link ResponseEntity} contendo a {@link CategoryDTO} criada e o status HTTP 201 Created.
     */
    @Operation(summary = "Create a new category", description = "Adds a new category.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<CategoryDTO> insert(
            @Parameter(description = "Dados da categoria") @RequestBody CategoryDTO categoryDTO
    ) {
        logger.info("POST /categories iniciado. Inserindo: {}", categoryDTO.getDescription());
        CategoryDTO created = categoryService.insert(categoryDTO);
        logger.info("POST /categories finalizado. Nova categoria ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ================= PUT =================
    /**
     * Atualiza uma categoria existente pelo seu ID.
     *
     * @param id O ID da categoria a ser atualizada.
     * @param categoryDTO O {@link CategoryDTO} com os dados atualizados.
     * @return {@link ResponseEntity} contendo a {@link CategoryDTO} atualizada e o status HTTP 200 OK.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     */
    @Operation(summary = "Update a category", description = "Updates an existing category by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category successfully updated"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> update(
            @Parameter(description = "ID da categoria", example = "1") @PathVariable Long id,
            @Parameter(description = "Dados atualizados da categoria") @RequestBody CategoryDTO categoryDTO
    ) {
        logger.info("PUT /categories/{} iniciado. Atualizando descrição para: {}", id, categoryDTO.getDescription());
        CategoryDTO updated = categoryService.update(id, categoryDTO);
        logger.info("PUT /categories/{} finalizado.", id);
        return ResponseEntity.ok(updated);
    }

    // ================= DELETE =================
    /**
     * Exclui uma categoria pelo seu ID.
     *
     * @param id O ID da categoria a ser excluída.
     * @return {@link ResponseEntity} com status HTTP 204 No Content.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     * @throws com.anapedra.stock_manager.services.exceptions.DatabaseException Se houver violação de integridade referencial.
     */
    @Operation(summary = "Delete a category", description = "Deletes a category by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "400", description = "Cannot delete due to data integrity violation")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da categoria", example = "1") @PathVariable Long id
    ) {
        logger.warn("DELETE /categories/{} iniciado.", id);
        categoryService.delete(id);
        logger.info("DELETE /categories/{} finalizado.", id);
        return ResponseEntity.noContent().build();
    }
}