package com.anapedra.stock_manager.controllers;

import com.anapedra.stock_manager.domain.dtos.BeerFilterDTO;
import com.anapedra.stock_manager.domain.dtos.BeerInsertDTO;
import com.anapedra.stock_manager.services.BeerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/beers")
@CrossOrigin(origins = "*")
public class BeerController {

    private final BeerService beerService;

    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    // Rota: GET /beers
    // Permissão: ADMIN ou CLIENT (Leitura)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
    @GetMapping
    public ResponseEntity<Page<BeerFilterDTO>> findAll(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "categoryDescription", defaultValue = "") String categoryDescription,
            @RequestParam(value = "beerDescription", defaultValue = "") String beerDescription,
            @RequestParam(value = "minQuantity", required = false) Integer minQuantity,
            @RequestParam(value = "maxQuantity", required = false) Integer maxQuantity,
            Pageable pageable) {

        Page<BeerFilterDTO> page = beerService.findAllBeer(
                categoryId,
                categoryDescription,
                beerDescription,
                minQuantity,
                maxQuantity,
                pageable
        );
        return ResponseEntity.ok(page);
    }


    // Rota: GET /beers/{id}
    // Permissão: ADMIN ou CLIENT (Leitura)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<BeerFilterDTO> findById(@PathVariable Long id) {
        BeerFilterDTO dto = beerService.findById(id);
        return ResponseEntity.ok(dto);
    }


    // Rota: POST /beers
    // Permissão: ADMIN (Escrita)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<BeerInsertDTO> insert(@Valid @RequestBody BeerInsertDTO dto) {
        BeerInsertDTO newDto = beerService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newDto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(newDto);
    }



    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<BeerInsertDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody BeerInsertDTO dto) {
        BeerInsertDTO updated = beerService.update(id, dto);
        return ResponseEntity.ok(updated);
    }



    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        beerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}