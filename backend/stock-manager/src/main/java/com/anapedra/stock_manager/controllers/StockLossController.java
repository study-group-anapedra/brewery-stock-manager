package com.anapedra.stock_manager.controllers;

import com.anapedra.stock_manager.domain.dtos.StockLossDTO;
import com.anapedra.stock_manager.services.StockLossService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping(value = "/losses") // Endpoint base
public class StockLossController {

    private final StockLossService stockLossService;

    public StockLossController(StockLossService stockLossService) {
        this.stockLossService = stockLossService;
    }

    @PostMapping
    public ResponseEntity<StockLossDTO> registerLoss(@Valid @RequestBody StockLossDTO dto) {
        
        StockLossDTO result = stockLossService.registerLoss(dto);

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
        Page<StockLossDTO> page = stockLossService.findLossesByFilters(
            reasonCode,
            beerId,
            beerName,
            categoryId,
            startDate,
            endDate,
            pageable
        );
        
        // Status 200 OK
        return ResponseEntity.ok(page);
    }
}