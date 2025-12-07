package com.anapedra.stock_manager.controllers;

import com.anapedra.stock_manager.domain.dtos.StockLossDTO;
import com.anapedra.stock_manager.services.StockLossService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

@RestController
@RequestMapping(value = "/losses")
public class StockLossController {

    private static final Logger logger = LoggerFactory.getLogger(StockLossController.class);

    private final StockLossService stockLossService;

    public StockLossController(StockLossService stockLossService) {
        this.stockLossService = stockLossService;
    }


    @PostMapping
    public ResponseEntity<StockLossDTO> registerLoss(@Valid @RequestBody StockLossDTO dto) {

        logger.warn("CONTROLLER: POST /losses iniciado. Tentativa de registrar perda de {} unidades para a cerveja ID: {}", 
                    dto.getQuantityLost(), dto.getBeerId());
                    
        StockLossDTO result = stockLossService.registerLoss(dto);

        logger.info("CONTROLLER: POST /losses finalizado. Status: 201 CREATED. Perda ID {} registrada.", result.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }


    @GetMapping
    public ResponseEntity<Page<StockLossDTO>> findLosses(
        @RequestParam(value = "reasonCode", required = false) Integer reasonCode,
        @RequestParam(value = "beerId", required = false) Long beerId,
        @RequestParam(value = "beerName", required = false) String beerName,
        @RequestParam(value = "categoryId", required = false) Long categoryId,
        @RequestParam(value = "startDate", required = false) LocalDate startDate,
        @RequestParam(value = "endDate", required = false) LocalDate endDate,
        Pageable pageable) {
        
        logger.info("CONTROLLER: GET /losses iniciado. Filtros: Razão={}, BeerID={}, Data Inicial: {}, Page={}",
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
        
        logger.info("CONTROLLER: GET /losses finalizado. Status: 200 OK. Total de registros de perda: {}", page.getTotalElements());
        return ResponseEntity.ok(page);
    }
}