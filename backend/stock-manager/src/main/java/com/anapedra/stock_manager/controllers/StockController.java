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

@RestController
@RequestMapping(value = "/stock")
public class StockController {

    private static final Logger logger = LoggerFactory.getLogger(StockController.class);

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }


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



    @GetMapping(value = "/{id}")
    public ResponseEntity<BeerStockDTO> findById(@PathVariable Long id) {
        logger.info("CONTROLLER: GET /stock/{} iniciado.", id);
        
        BeerStockDTO dto = stockService.findById(id);
        
        logger.info("CONTROLLER: GET /stock/{} finalizado. Status: 200 OK.", id);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping(value = "/expired")
    public ResponseEntity<List<BeerStockDTO>> getExpiredBeersReport(
            @RequestParam("referenceDate") LocalDate referenceDate) {
        
        logger.info("CONTROLLER: GET /stock/expired iniciado. Data de referência: {}", referenceDate);
        
        List<BeerStockDTO> list = stockService.getExpiredBeersReport(referenceDate);
        
        logger.info("CONTROLLER: GET /stock/expired finalizado. Status: 200 OK. Total de itens vencidos: {}", list.size());
        return ResponseEntity.ok(list);
    }

}