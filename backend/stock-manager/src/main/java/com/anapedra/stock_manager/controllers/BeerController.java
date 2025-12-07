package com.anapedra.stock_manager.controllers;

import com.anapedra.stock_manager.domain.dtos.BeerInsertDTO;
import com.anapedra.stock_manager.domain.dtos.BeerStockDTO;
import com.anapedra.stock_manager.services.BeerService;
import com.anapedra.stock_manager.services.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import org.slf4j.Logger; // Import do Logger
import org.slf4j.LoggerFactory; // Import do LoggerFactory

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/beers")
public class BeerController {

    private static final Logger logger = LoggerFactory.getLogger(BeerController.class);

    @Autowired
    private StockService beerService;

    @Autowired
    private BeerService service;


    @GetMapping
    public ResponseEntity<Page<BeerStockDTO>> findAllBeer(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "") String categoryDescription,
            @RequestParam(defaultValue = "") String beerDescription,
            @RequestParam(required = false) Integer minQuantity,
            @RequestParam(required = false) Integer maxQuantity,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        logger.info("CONTROLLER: GET /beers iniciado. Filtros: CatID={}, Desc='{}', Page={}",
                    categoryId, beerDescription, page);

        PageRequest pageable = PageRequest.of(page, size);

        Page<BeerStockDTO> result = beerService.findAllBeer(
                categoryId,
                categoryDescription,
                beerDescription,
                minQuantity,
                maxQuantity,
                pageable
        );

        logger.info("CONTROLLER: GET /beers finalizado. Status: 200 OK. Total de itens: {}", result.getTotalElements());
        return ResponseEntity.ok(result);
    }


    @GetMapping("/native")
    public ResponseEntity<List<BeerStockDTO>> filtrarUsandoFuncao(
            @RequestParam(required = false) Long beerId,
            @RequestParam(defaultValue = "") String beerDescription,
            @RequestParam(required = false) Integer minQuantity,
            @RequestParam(required = false) Integer maxQuantity,
            @RequestParam(required = false) Integer daysUntilExpiry,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "0") Integer pageNumber
    ) {
        logger.info("CONTROLLER: GET /beers/native iniciado. Funcao PL/pgSQL: BeerID={}, Expira em {} dias, Page={}",
                    beerId, daysUntilExpiry, pageNumber);
                    
        List<BeerStockDTO> list = beerService.findUsingPlpgsqlFunction(
                beerId,
                beerDescription,
                minQuantity,
                maxQuantity,
                daysUntilExpiry,
                pageSize,
                pageNumber
        );

        logger.info("CONTROLLER: GET /beers/native finalizado. Status: 200 OK. Itens retornados: {}", list.size());
        return ResponseEntity.ok(list);
    }


    @GetMapping("/expired")
    public ResponseEntity<List<BeerStockDTO>> getExpiredBeers(
            @RequestParam String referenceDate
    ) {
        logger.info("CONTROLLER: GET /beers/expired iniciado. Data de referência: {}", referenceDate);

        LocalDate date = LocalDate.parse(referenceDate);

        List<BeerStockDTO> list = beerService.getExpiredBeersReport(date);

        logger.info("CONTROLLER: GET /beers/expired finalizado. Status: 200 OK. Total de cervejas vencidas: {}", list.size());
        return ResponseEntity.ok(list);
    }


    @PostMapping
    public ResponseEntity<BeerInsertDTO> insert(@RequestBody BeerInsertDTO dto) {
        logger.info("CONTROLLER: POST /beers iniciado. Inserindo nova cerveja: {}", dto.getName());
        
        BeerInsertDTO newDto = service.insert(dto);
        
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newDto.getId())
                .toUri();

        logger.info("CONTROLLER: POST /beers finalizado. Status: 201 CREATED. Nova cerveja ID: {}", newDto.getId());
        return ResponseEntity.created(uri).body(newDto);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.warn("CONTROLLER: DELETE /beers/{} iniciado. Tentativa de exclusão.", id);
        
        service.delete(id);
        
        logger.info("CONTROLLER: DELETE /beers/{} finalizado. Status: 204 NO CONTENT.", id);
        return ResponseEntity.noContent().build();
    }
}