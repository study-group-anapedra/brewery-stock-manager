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

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/beers")
public class BeerController {

    @Autowired
    private StockService beerService;

    @Autowired
    private BeerService service;

    // ------------------------------------------------------------
    // 1. Filtro padrão com paginação (JPQL)
    // ------------------------------------------------------------
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

        PageRequest pageable = PageRequest.of(page, size);

        Page<BeerStockDTO> result = beerService.findAllBeer(
                categoryId,
                categoryDescription,
                beerDescription,
                minQuantity,
                maxQuantity,
                pageable
        );

        return ResponseEntity.ok(result);
    }

    // ------------------------------------------------------------
    // 2. Consulta usando função PL/pgSQL (NativeQuery)
    // ------------------------------------------------------------
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
        List<BeerStockDTO> list = beerService.findUsingPlpgsqlFunction(
                beerId,
                beerDescription,
                minQuantity,
                maxQuantity,
                daysUntilExpiry,
                pageSize,
                pageNumber
        );

        return ResponseEntity.ok(list);
    }

    // ------------------------------------------------------------
    // 3. Relatório — Cervejas vencidas
    // ------------------------------------------------------------
    @GetMapping("/expired")
    public ResponseEntity<List<BeerStockDTO>> getExpiredBeers(
            @RequestParam String referenceDate
    ) {

        LocalDate date = LocalDate.parse(referenceDate);

        List<BeerStockDTO> list = beerService.getExpiredBeersReport(date);

        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<BeerInsertDTO> insert(@RequestBody BeerInsertDTO dto) {
        BeerInsertDTO newDto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newDto.getId())
                .toUri();

        return ResponseEntity.created(uri).body(newDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
