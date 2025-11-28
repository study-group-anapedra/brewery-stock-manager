package com.anapedra.stock_manager.controllers;

import com.anapedra.stock_manager.domain.dtos.BeerRestockingDTO;
import com.anapedra.stock_manager.services.BeerRestockingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/restock")
public class BeerRestockingController {

    private final BeerRestockingService restockingService;

    public BeerRestockingController(BeerRestockingService restockingService) {
        this.restockingService = restockingService;
    }

    /**
     * Endpoint: GET /restock
     * Obtém todas as entradas de estoque.
     */
    @GetMapping
    public ResponseEntity<List<BeerRestockingDTO>> findAll() {
        List<BeerRestockingDTO> list = restockingService.findAll();
        return ResponseEntity.ok().body(list);
    }

    /**
     * Endpoint: GET /restock/{id}
     * Obtém uma entrada de estoque por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BeerRestockingDTO> findById(@PathVariable Long id) {
        BeerRestockingDTO dto = restockingService.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    /**
     * Endpoint: POST /restock
     * Cria uma nova entrada de estoque.
     * Retorna 201 Created.
     */
    @PostMapping
    public ResponseEntity<BeerRestockingDTO> create(@RequestBody BeerRestockingDTO dto) {
        BeerRestockingDTO newDto = restockingService.create(dto);
        
        // Cria a URI do novo recurso (melhor prática REST)
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newDto.getId())
                .toUri();
        
        // Retorna 201 Created
        return ResponseEntity.created(uri).body(newDto);
    }

    /**
     * Endpoint: PUT /restock/{id}
     * Atualiza uma entrada de estoque existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BeerRestockingDTO> update(@PathVariable Long id, @RequestBody BeerRestockingDTO dto) {
        BeerRestockingDTO updatedDto = restockingService.update(id, dto);
        return ResponseEntity.ok().body(updatedDto);
    }

    /**
     * Endpoint: DELETE /restock/{id}
     * Deleta uma entrada de estoque por ID.
     * Retorna 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        restockingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}