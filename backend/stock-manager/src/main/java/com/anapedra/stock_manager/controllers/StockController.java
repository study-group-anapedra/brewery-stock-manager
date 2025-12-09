package com.anapedra.stock_manager.controllers;

import com.anapedra.stock_manager.domain.dtos.BeerStockDTO;
import com.anapedra.stock_manager.services.StockService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST responsável por gerenciar as consultas relacionadas ao estoque
 * de cervejas (Stock).
 *
 * <p>Expõe endpoints para buscar o estoque total com filtros, buscar o estoque
 * de uma cerveja específica e gerar relatórios de cervejas vencidas.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping(value = "/api/v1/stock")
public class StockController {

    /**
     * Logger para registro de eventos e rastreamento de execução.
     */
    private static final Logger logger = LoggerFactory.getLogger(StockController.class);

    /**
     * Serviço responsável pela lógica de negócio e operações de estoque.
     */
    private final StockService stockService;

    /**
     * Construtor para injeção de dependência do serviço de estoque.
     *
     * @param stockService O serviço de estoque.
     */
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    /**
     * Retorna uma lista paginada de todos os registros de estoque de cervejas,
     * aplicando filtros opcionais.
     *
     * @param categoryId ID da categoria da cerveja (opcional).
     * @param categoryDescription Descrição da categoria (filtro por nome parcial, opcional).
     * @param beerDescription Descrição da cerveja (filtro por nome parcial, opcional).
     * @param minQuantity Quantidade mínima em estoque para filtro (opcional).
     * @param maxQuantity Quantidade máxima em estoque para filtro (opcional).
     * @param pageable Objeto de paginação e ordenação.
     * @return {@link ResponseEntity} contendo uma {@link Page} de {@link BeerStockDTO}.
     */
    @GetMapping
    public ResponseEntity<Page<BeerStockDTO>> findAll(

            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "categoryDescription", defaultValue = "") String categoryDescription,
            @RequestParam(value = "beerDescription", defaultValue = "") String beerDescription,
            @RequestParam(value = "minQuantity", required = false) Integer minQuantity,
            @RequestParam(value = "maxQuantity", required = false) Integer maxQuantity,
            Pageable pageable) {

        logger.info("CONTROLLER: GET /stock iniciado. Filtros: CatID={}, QtdMin={}, Page={}",
                    categoryId, minQuantity, pageable.getPageNumber());
                    
        Page<BeerStockDTO> list = stockService.findAllBeer(
            categoryId,
            categoryDescription.trim(), 
            beerDescription.trim(), 
            minQuantity,
            maxQuantity,
            pageable
        );
        
        // Log de saída
        logger.info("CONTROLLER: GET /stock finalizado. Status: 200 OK. Itens retornados: {}", list.getTotalElements());
        return ResponseEntity.ok().body(list);
    }

    /**
     * Retorna os detalhes de estoque para uma cerveja específica.
     *
     * @param id O ID da cerveja.
     * @return {@link ResponseEntity} contendo o {@link BeerStockDTO} da cerveja.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<BeerStockDTO> findById(@PathVariable Long id) {
        logger.info("CONTROLLER: GET /stock/{} iniciado.", id);
        
        BeerStockDTO dto = stockService.findById(id);
        
        logger.info("CONTROLLER: GET /stock/{} finalizado. Status: 200 OK.", id);
        return ResponseEntity.ok().body(dto);
    }

    /**
     * Gera um relatório listando todas as cervejas cujo prazo de validade
     * expirou ou está prestes a expirar, com base em uma data de referência.
     *
     * @param referenceDate A data de corte para verificar a validade (formato yyyy-MM-dd).
     * @return {@link ResponseEntity} contendo uma {@link List} de {@link BeerStockDTO} vencidas.
     */
    @GetMapping(value = "/expired")
    public ResponseEntity<List<BeerStockDTO>> getExpiredBeersReport(
            @RequestParam("referenceDate") LocalDate referenceDate) {
        
        logger.info("CONTROLLER: GET /stock/expired iniciado. Data de referência: {}", referenceDate);
        
        List<BeerStockDTO> list = stockService.getExpiredBeersReport(referenceDate);
        
        logger.info("CONTROLLER: GET /stock/expired finalizado. Status: 200 OK. Total de itens vencidos: {}", list.size());
        return ResponseEntity.ok(list);
    }
}