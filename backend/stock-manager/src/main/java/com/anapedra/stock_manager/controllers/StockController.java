package com.anapedra.stock_manager.controllers;

import com.anapedra.stock_manager.domain.dtos.BeerStockDTO;
import com.anapedra.stock_manager.services.StockService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/stock")
public class StockController {

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
        Page<BeerStockDTO> list = stockService.findAllBeer(
            categoryId,
            categoryDescription.trim(), 
            beerDescription.trim(), 
            minQuantity,
            maxQuantity,
            pageable
        );
        return ResponseEntity.ok().body(list);
    }




    //Por id da Beer
    @GetMapping(value = "/{id}")
    public ResponseEntity<BeerStockDTO> findById(@PathVariable Long id) {
        BeerStockDTO dto = stockService.findById(id);
        return ResponseEntity.ok().body(dto);
    }


    @GetMapping(value = "/expired")
    public ResponseEntity<List<BeerStockDTO>> getExpiredBeersReport(
            @RequestParam("referenceDate") LocalDate referenceDate) {
        List<BeerStockDTO> list = stockService.getExpiredBeersReport(referenceDate);
        return ResponseEntity.ok(list);
    }



}