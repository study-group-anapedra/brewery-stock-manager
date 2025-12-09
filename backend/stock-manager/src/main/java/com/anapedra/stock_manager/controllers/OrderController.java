package com.anapedra.stock_manager.controllers;

import com.anapedra.stock_manager.domain.dtos.OrderDTO;
import com.anapedra.stock_manager.services.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Controlador REST responsável por gerenciar as operações CRUD (Create, Read, Update, Delete)
 * relacionadas à entidade Pedido (Order).
 *
 * <p>Expõe endpoints para listar pedidos com filtros, buscar por ID, criar, atualizar
 * e deletar pedidos.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    /**
     * Logger para registro de eventos e rastreamento de execução.
     */
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    /**
     * Serviço responsável pela lógica de negócio e operações de pedidos.
     */
    private final OrderService orderService;

    /**
     * Construtor para injeção de dependência do serviço de pedidos.
     *
     * @param orderService O serviço de pedidos.
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ================= GET ALL =================
    /**
     * Retorna uma lista paginada de todos os pedidos, aplicando filtros opcionais.
     *
     * @param clientId ID do cliente (opcional).
     * @param nameClient Nome do cliente (filtro parcial, opcional).
     * @param cpfClient CPF do cliente (filtro parcial, opcional).
     * @param minDate Data mínima para o período de busca (formato yyyy-MM-dd, opcional).
     * @param maxDate Data máxima para o período de busca (formato yyyy-MM-dd, opcional).
     * @param pageable O objeto de paginação e ordenação.
     * @return {@link ResponseEntity} contendo uma {@link Page} de {@link OrderDTO}.
     */
    @Operation(summary = "List orders with filters", description = "Returns a paginated list of orders filtered by client ID, name, CPF, or date range.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved orders")
    })
    @GetMapping
    public ResponseEntity<Page<OrderDTO>> findAll(
            @Parameter(description = "ID do cliente", example = "1") @RequestParam(value = "clientId", required = false) Long clientId,
            @Parameter(description = "Nome do cliente", example = "Ana Santana") @RequestParam(value = "nameClient", required = false) String nameClient,
            @Parameter(description = "CPF do cliente", example = "123.456.789-00") @RequestParam(value = "cpfClient", required = false) String cpfClient,
            @Parameter(description = "Data mínima (yyyy-MM-dd)", example = "2025-12-01") @RequestParam(value = "minDate", required = false) String minDate,
            @Parameter(description = "Data máxima (yyyy-MM-dd)", example = "2025-12-09") @RequestParam(value = "maxDate", required = false) String maxDate,
            Pageable pageable
    ) {
        logger.info("GET /orders iniciado com filtros. Client ID: {}, Data Min: {}, Page: {}", clientId, minDate, pageable.getPageNumber());
        Page<OrderDTO> list = orderService.find(clientId, nameClient, cpfClient, minDate, maxDate, pageable);
        logger.info("GET /orders finalizado. Total de pedidos: {}", list.getTotalElements());
        return ResponseEntity.ok(list);
    }

    // ================= GET BY ID =================
    /**
     * Retorna os detalhes de um pedido específico pelo seu ID.
     *
     * @param id O ID do pedido a ser buscado.
     * @return {@link ResponseEntity} contendo o {@link OrderDTO} do pedido.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     */
    @Operation(summary = "Get order by ID", description = "Returns a single order by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> findById(
            @Parameter(description = "ID do pedido", example = "1") @PathVariable Long id
    ) {
        logger.info("GET /orders/{} iniciado.", id);
        OrderDTO dto = orderService.findById(id);
        logger.info("GET /orders/{} finalizado.", id);
        return ResponseEntity.ok(dto);
    }

    // ================= POST =================
    /**
     * Cria e persiste um novo pedido.
     *
     * @param dto O {@link OrderDTO} contendo os dados do pedido a ser criado.
     * @return {@link ResponseEntity} contendo o {@link OrderDTO} criado e o status HTTP 201 Created.
     */
    @Operation(summary = "Create a new order", description = "Creates a new order.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input ou estoque insuficiente")
    })
    @PostMapping
    public ResponseEntity<OrderDTO> save(
            @Parameter(description = "Dados do pedido") @Valid @RequestBody OrderDTO dto
    ) {
        logger.info("POST /orders iniciado. Criando novo pedido.");
        OrderDTO newOrder = orderService.save(dto);
        // Cria a URI do novo recurso
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newOrder.getId()).toUri();
        logger.info("POST /orders finalizado. Novo pedido ID: {}", newOrder.getId());
        return ResponseEntity.created(uri).body(newOrder);
    }

    // ================= PUT =================
    /**
     * Atualiza um pedido existente.
     *
     * @param id O ID do pedido a ser atualizado.
     * @param dto O {@link OrderDTO} com os dados atualizados.
     * @return {@link ResponseEntity} contendo o {@link OrderDTO} atualizado e o status HTTP 200 OK.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     */
    @Operation(summary = "Update an order", description = "Updates an existing order by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order successfully updated"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input ou estoque insuficiente")
    })
    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> update(
            @Parameter(description = "ID do pedido", example = "1") @PathVariable Long id,
            @Parameter(description = "Dados atualizados do pedido") @Valid @RequestBody OrderDTO dto
    ) {
        logger.info("PUT /orders/{} iniciado.", id);
        OrderDTO updated = orderService.update(id, dto);
        logger.info("PUT /orders/{} finalizado.", id);
        return ResponseEntity.ok(updated);
    }

    // ================= DELETE =================
    /**
     * Exclui um pedido pelo seu ID.
     *
     * @param id O ID do pedido a ser excluído.
     * @return {@link ResponseEntity} com status HTTP 204 No Content.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     */
    @Operation(summary = "Delete an order", description = "Deletes an order by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - violação de regras de negócio")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do pedido", example = "1") @PathVariable Long id
    ) {
        logger.warn("DELETE /orders/{} iniciado.", id);
        orderService.delete(id);
        logger.info("DELETE /orders/{} finalizado.", id);
        return ResponseEntity.noContent().build();
    }
}