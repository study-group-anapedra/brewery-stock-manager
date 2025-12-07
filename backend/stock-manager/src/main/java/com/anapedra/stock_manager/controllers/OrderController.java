package com.anapedra.stock_manager.controllers;

import com.anapedra.stock_manager.domain.dtos.OrderDTO;
import com.anapedra.stock_manager.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

@RestController
@RequestMapping(value = "/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @GetMapping
    public ResponseEntity<Page<OrderDTO>> findAll(
            @RequestParam(value = "clientId", required = false) Long clientId,
            @RequestParam(value = "nameClient", required = false) String nameClient,
            @RequestParam(value = "cpfClient", required = false) String cpfClient,
            @RequestParam(value = "minDate", required = false) String minDate,
            @RequestParam(value = "maxDate", required = false) String maxDate,
            Pageable pageable
    ) {
        logger.info("CONTROLLER: GET /orders iniciado com filtros. Client ID: {}, Data Min: {}, Page: {}",
                    clientId, minDate, pageable.getPageNumber());
                    
        Page<OrderDTO> list = orderService.find(clientId, nameClient, cpfClient, minDate, maxDate, pageable);
        
        logger.info("CONTROLLER: GET /orders finalizado. Status: 200 OK. Total de pedidos: {}", list.getTotalElements());
        return ResponseEntity.ok(list);
    }


    @PostMapping
    public ResponseEntity<OrderDTO> save(@Valid @RequestBody OrderDTO dto) {
        logger.info("CONTROLLER: POST /orders iniciado. Tentativa de criação de novo pedido.");
        
        OrderDTO newOrder = orderService.save(dto);
        
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newOrder.getId())
                .toUri();
                

        logger.info("CONTROLLER: POST /orders finalizado. Status: 201 CREATED. Novo pedido ID: {}", newOrder.getId());
        return ResponseEntity.created(uri).body(newOrder);
    }



    @GetMapping(value = "/{id}")
    public ResponseEntity<OrderDTO> findById(@PathVariable Long id) {
        logger.info("CONTROLLER: GET /orders/{} iniciado.", id);
        
        OrderDTO dto = orderService.findById(id);
        
        logger.info("CONTROLLER: GET /orders/{} finalizado. Status: 200 OK.", id);
        return ResponseEntity.ok(dto);
    }


    @PutMapping(value = "/{id}")
    public ResponseEntity<OrderDTO> update(@PathVariable Long id, @Valid @RequestBody OrderDTO dto) {
        logger.info("CONTROLLER: PUT /orders/{} iniciado. Tentativa de atualização.", id);
        
        OrderDTO updated = orderService.update(id, dto);
        
        logger.info("CONTROLLER: PUT /orders/{} finalizado. Status: 200 OK.", id);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.warn("CONTROLLER: DELETE /orders/{} iniciado. Tentativa de exclusão.", id);
        
        orderService.delete(id);
        
        logger.info("CONTROLLER: DELETE /orders/{} finalizado. Status: 204 NO CONTENT.", id);
        return ResponseEntity.noContent().build();
    }
}