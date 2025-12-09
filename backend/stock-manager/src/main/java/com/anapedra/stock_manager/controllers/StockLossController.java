package com.anapedra.stock_manager.controllers;

import com.anapedra.stock_manager.domain.dtos.StockLossDTO;
import com.anapedra.stock_manager.services.StockLossService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controlador REST responsável por gerenciar as operações de registro e consulta
 * de perdas de estoque (StockLoss).
 *
 * <p>Expõe endpoints para registrar perdas de estoque e listar entradas de perdas
 * com opções avançadas de filtragem.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/v1/losses")
@CrossOrigin(origins = "*")
public class StockLossController {

    /**
     * Logger para registro de eventos e rastreamento de execução.
     */
    private static final Logger logger = LoggerFactory.getLogger(StockLossController.class);

    /**
     * Serviço responsável pela lógica de negócio e operações de perdas de estoque.
     */
    private final StockLossService stockLossService;

    /**
     * Construtor para injeção de dependência do serviço de perdas de estoque.
     *
     * @param stockLossService O serviço de perdas de estoque.
     */
    public StockLossController(StockLossService stockLossService) {
        this.stockLossService = stockLossService;
    }

    // ================= POST =================
    /**
     * Registra uma nova perda de estoque.
     *
     * <p>Esta operação exige um DTO válido e, se bem-sucedida, retorna o
     * registro de perda criado com status HTTP 201 CREATED.</p>
     *
     * @param dto O {@link StockLossDTO} contendo a quantidade perdida e o ID da cerveja.
     * @return {@link ResponseEntity} contendo o {@link StockLossDTO} registrado.
     */
    @Operation(summary = "Register stock loss", description = "Registers a new stock loss entry for a specific beer.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Loss successfully registered"),
        @ApiResponse(responseCode = "400", description = "Invalid input data ou estoque insuficiente"),
        @ApiResponse(responseCode = "404", description = "Cerveja não encontrada")
    })
    @PostMapping
    public ResponseEntity<StockLossDTO> registerLoss(
            @Parameter(description = "Stock loss details") @Valid @RequestBody StockLossDTO dto
    ) {
        logger.warn("POST /losses iniciado. Tentativa de registrar perda de {} unidades para a cerveja ID: {}",
                    dto.getQuantityLost(), dto.getBeerId());

        StockLossDTO result = stockLossService.registerLoss(dto);

        logger.info("POST /losses finalizado. Status: 201 CREATED. Perda ID {} registrada.", result.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // ================= GET =================
    /**
     * Retorna uma lista paginada de registros de perdas de estoque, permitindo
     * filtragem por diversos critérios.
     *
     * @param reasonCode O código da razão de perda (opcional).
     * @param beerId O ID da cerveja (opcional).
     * @param beerName O nome parcial da cerveja (opcional).
     * @param categoryId O ID da categoria (opcional).
     * @param startDate Data de início do período de busca (opcional).
     * @param endDate Data de fim do período de busca (opcional).
     * @param pageable O objeto de paginação e ordenação.
     * @return {@link ResponseEntity} contendo uma {@link Page} de {@link StockLossDTO}.
     */
    @Operation(summary = "List stock losses with filters", description = "Returns a paginated list of stock losses filtered by beer, category, reason code, or date range.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Losses retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<Page<StockLossDTO>> findLosses(
            @Parameter(description = "Reason code for the loss", example = "1") @RequestParam(value = "reasonCode", required = false) Integer reasonCode,
            @Parameter(description = "Beer ID", example = "10") @RequestParam(value = "beerId", required = false) Long beerId,
            @Parameter(description = "Beer name", example = "IPA") @RequestParam(value = "beerName", required = false) String beerName,
            @Parameter(description = "Category ID", example = "5") @RequestParam(value = "categoryId", required = false) Long categoryId,
            @Parameter(description = "Start date (yyyy-MM-dd)", example = "2025-12-01") @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", example = "2025-12-09") @RequestParam(value = "endDate", required = false) LocalDate endDate,
            Pageable pageable
    ) {
        logger.info("GET /losses iniciado. Filtros: Razão={}, BeerID={}, Data Inicial: {}, Page={}",
                    reasonCode, beerId, startDate, pageable.getPageNumber());

        Page<StockLossDTO> page = stockLossService.findLossesByFilters(
                reasonCode,
                beerId,
                beerName,
                categoryId,
                startDate,
                endDate,
                pageable
        );

        logger.info("GET /losses finalizado. Status: 200 OK. Total de registros de perda: {}", page.getTotalElements());
        return ResponseEntity.ok(page);
    }
}