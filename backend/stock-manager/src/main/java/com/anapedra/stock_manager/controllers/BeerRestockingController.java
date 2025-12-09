package com.anapedra.stock_manager.controllers;

import com.anapedra.stock_manager.domain.dtos.BeerRestockingDTO;
import com.anapedra.stock_manager.services.BeerRestockingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST responsável por gerenciar as operações CRUD (Create, Read, Update, Delete)
 * relacionadas à entidade de Reabastecimento de Cerveja (BeerRestocking).
 *
 * <p>Expõe endpoints para registrar entradas de reabastecimento, buscar, atualizar e deletar
 * esses registros.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/v1/restock")
public class BeerRestockingController {

    /**
     * Logger para registro de eventos e rastreamento de execução.
     */
    private static final Logger logger = LoggerFactory.getLogger(BeerRestockingController.class);

    /**
     * Serviço responsável pela lógica de negócio e operações de reabastecimento.
     */
    private final BeerRestockingService restockingService;

    /**
     * Construtor para injeção de dependência do serviço de reabastecimento.
     *
     * @param restockingService O serviço de reabastecimento de cerveja.
     */
    public BeerRestockingController(BeerRestockingService restockingService) {
        this.restockingService = restockingService;
    }

    // ================= GET ALL =================
    /**
     * Retorna uma lista de todos os registros de reabastecimento de cerveja.
     *
     * @return {@link ResponseEntity} contendo uma {@link List} de {@link BeerRestockingDTO}.
     */
    @Operation(summary = "List all restocking entries", description = "Returns a list of all beer restocking entries.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all entries")
    })
    @GetMapping
    public ResponseEntity<List<BeerRestockingDTO>> findAll() {
        logger.info("GET /restock iniciado.");
        List<BeerRestockingDTO> list = restockingService.findAll();
        logger.info("GET /restock finalizado. Total de entradas: {}", list.size());
        return ResponseEntity.ok(list);
    }

    // ================= GET BY ID =================
    /**
     * Retorna os detalhes de um registro de reabastecimento específico pelo seu ID.
     *
     * @param id O ID do registro de reabastecimento a ser buscado.
     * @return {@link ResponseEntity} contendo o {@link BeerRestockingDTO} encontrado.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     */
    @Operation(summary = "Get restocking entry by ID", description = "Returns a single restocking entry by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entry found"),
            @ApiResponse(responseCode = "404", description = "Entry not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BeerRestockingDTO> findById(
            @Parameter(description = "ID of the restocking entry", example = "1") @PathVariable Long id
    ) {
        logger.info("GET /restock/{} iniciado.", id);
        BeerRestockingDTO dto = restockingService.findById(id);
        logger.info("GET /restock/{} finalizado.", id);
        return ResponseEntity.ok(dto);
    }

    // ================= POST =================
    /**
     * Cria e persiste um novo registro de reabastecimento, atualizando o estoque da cerveja.
     *
     * @param dto O {@link BeerRestockingDTO} contendo os dados do reabastecimento.
     * @return {@link ResponseEntity} contendo o {@link BeerRestockingDTO} criado e o status HTTP 201 Created.
     */
    @Operation(summary = "Create a new restocking entry", description = "Adds a new beer restocking entry to the inventory.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Entry successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<BeerRestockingDTO> create(
            @Parameter(description = "Restocking entry data") @RequestBody BeerRestockingDTO dto
    ) {
        logger.info("POST /restock iniciado. BeerID={}, Quantidade={}", dto.getBeerId(), dto.getQuantity());

        BeerRestockingDTO newDto = restockingService.create(dto);

        // Constrói a URI do novo recurso criado
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newDto.getBeerId()) // O ID da nova entrada, não o BeerId
                .toUri();

        logger.info("POST /restock finalizado. Nova entrada ID={}", newDto.getBeerId());
        return ResponseEntity.created(uri).body(newDto);
    }

    // ================= PUT =================
    /**
     * Atualiza um registro de reabastecimento existente.
     *
     * @param id O ID do registro de reabastecimento a ser atualizado.
     * @param dto O {@link BeerRestockingDTO} com os dados atualizados.
     * @return {@link ResponseEntity} contendo o {@link BeerRestockingDTO} atualizado e o status HTTP 200 OK.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     */
    @Operation(summary = "Update a restocking entry", description = "Updates an existing restocking entry by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entry successfully updated"),
            @ApiResponse(responseCode = "404", description = "Entry not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BeerRestockingDTO> update(
            @Parameter(description = "ID of the entry to update", example = "1") @PathVariable Long id,
            @Parameter(description = "Updated restocking entry data") @RequestBody BeerRestockingDTO dto
    ) {
        logger.info("PUT /restock/{} iniciado.", id);
        BeerRestockingDTO updatedDto = restockingService.update(id, dto);
        logger.info("PUT /restock/{} finalizado.", id);
        return ResponseEntity.ok(updatedDto);
    }

    // ================= DELETE =================
    /**
     * Exclui um registro de reabastecimento pelo seu ID.
     *
     * @param id O ID do registro de reabastecimento a ser excluído.
     * @return {@link ResponseEntity} com status HTTP 204 No Content.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     */
    @Operation(summary = "Delete a restocking entry", description = "Deletes a restocking entry by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Entry successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Entry not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the entry to delete", example = "1") @PathVariable Long id
    ) {
        logger.warn("DELETE /restock/{} iniciado.", id);
        restockingService.delete(id);
        logger.info("DELETE /restock/{} finalizado.", id);
        return ResponseEntity.noContent().build();
    }
}