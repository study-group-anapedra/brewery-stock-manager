package com.anapedra.stock_manager.controllers;

import com.anapedra.stock_manager.domain.dtos.OrderDTO;
import com.anapedra.stock_manager.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDTO> save(@Valid @RequestBody OrderDTO dto) {
        OrderDTO newOrder = orderService.save(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newOrder.getId())
                .toUri();
        return ResponseEntity.created(uri).body(newOrder);
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<OrderDTO> findById(@PathVariable Long id) {
        OrderDTO dto = orderService.findById(id);
        return ResponseEntity.ok(dto);
    }


//    @GetMapping
//    public ResponseEntity<Page<OrderDTO>> findAll(Pageable pageable) {
//        Page<OrderDTO> list = orderService.findAll(pageable);
//        return ResponseEntity.ok(list);
//    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<OrderDTO> update(@PathVariable Long id, @Valid @RequestBody OrderDTO dto) {
        OrderDTO updated = orderService.update(id, dto);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<OrderDTO>> findAll(

            @RequestParam(value = "clientId",defaultValue = "0")Long clientId,
            @RequestParam(value = "nameClient",defaultValue = "") String nameClient,
            @RequestParam(value = "cpfClient",defaultValue = "") String cpfClient,
            Pageable pageable) {
        Page<OrderDTO> list = orderService.find(clientId,nameClient,cpfClient,pageable);
        return ResponseEntity.ok().body(list);
    }
}