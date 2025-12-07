package com.anapedra.stock_manager.controllers;

import com.anapedra.stock_manager.domain.dtos.BeerRestockingDTO;
import com.anapedra.stock_manager.services.BeerRestockingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/restock")
public class BeerRestockingController {

    // 1. Definição do Logger
    private static final Logger logger = LoggerFactory.getLogger(BeerRestockingController.class);

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
        logger.info("CONTROLLER: GET /restock iniciado. Buscando todas as entradas.");
        List<BeerRestockingDTO> list = restockingService.findAll();
        logger.info("CONTROLLER: GET /restock finalizado. Status: 200 OK. Total de entradas: {}", list.size());
        return ResponseEntity.ok().body(list);
    }

    /**
     * Endpoint: GET /restock/{id}
     * Obtém uma entrada de estoque por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BeerRestockingDTO> findById(@PathVariable Long id) {
        logger.info("CONTROLLER: GET /restock/{} iniciado.", id);
        BeerRestockingDTO dto = restockingService.findById(id);
        logger.info("CONTROLLER: GET /restock/{} finalizado. Status: 200 OK.", id);
        return ResponseEntity.ok().body(dto);
    }

    /**
     * Endpoint: POST /restock
     * Cria uma nova entrada de estoque.
     * Retorna 201 Created.
     */
    @PostMapping
    public ResponseEntity<BeerRestockingDTO> create(@RequestBody BeerRestockingDTO dto) {
        logger.info("CONTROLLER: POST /restock iniciado. Cerveja ID: {}, Quantidade: {}", 
                    dto.getBeerId(), dto.getQuantity());
                    
        BeerRestockingDTO newDto = restockingService.create(dto);
        
        // Cria a URI do novo recurso (melhor prática REST)
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newDto.getBeerId())
                .toUri();
        
        logger.info("CONTROLLER: POST /restock finalizado. Status: 201 CREATED. Nova entrada ID: {}", newDto.getBeerId());
        return ResponseEntity.created(uri).body(newDto);
    }

    /**
     * Endpoint: PUT /restock/{id}
     * Atualiza uma entrada de estoque existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BeerRestockingDTO> update(@PathVariable Long id, @RequestBody BeerRestockingDTO dto) {
        logger.info("CONTROLLER: PUT /restock/{} iniciado. Atualizando entrada.", id);
        BeerRestockingDTO updatedDto = restockingService.update(id, dto);
        logger.info("CONTROLLER: PUT /restock/{} finalizado. Status: 200 OK.", id);
        return ResponseEntity.ok().body(updatedDto);
    }

    /**
     * Endpoint: DELETE /restock/{id}
     * Deleta uma entrada de estoque por ID.
     * Retorna 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.warn("CONTROLLER: DELETE /restock/{} iniciado. Tentativa de exclusão.", id);
        restockingService.delete(id);
        logger.info("CONTROLLER: DELETE /restock/{} finalizado. Status: 204 NO CONTENT.", id);
        return ResponseEntity.noContent().build();
    }
}