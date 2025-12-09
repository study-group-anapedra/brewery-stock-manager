package com.anapedra.stock_manager.controllers;

import com.anapedra.stock_manager.domain.dtos.BeerInsertDTO;
import com.anapedra.stock_manager.domain.dtos.BeerStockDTO;
import com.anapedra.stock_manager.services.BeerService;
import com.anapedra.stock_manager.services.StockService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST responsável por gerenciar as operações CRUD e de consulta
 * avançada relacionadas à entidade Cerveja (Beer) e seu estoque.
 *
 * <p>Expõe endpoints para listar cervejas com filtros, usar funções nativas do banco,
 * registrar novas cervejas e excluir registros.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/v1/beers")
public class BeerController {

    /**
     * Logger para registro de eventos e rastreamento de execução.
     */
    private static final Logger logger = LoggerFactory.getLogger(BeerController.class);

    /**
     * Serviço responsável pelas operações de consulta avançada e estoque.
     */
    @Autowired
    private StockService stockService;

    /**
     * Serviço responsável pelas operações CRUD básicas da entidade Beer.
     */
    @Autowired
    private BeerService beerService;

    // ================= GET ALL BEERS =================
    /**
     * Retorna uma lista paginada de cervejas, permitindo a aplicação de diversos filtros
     * relacionados à descrição da cerveja, categoria e quantidade em estoque.
     *
     * @param categoryId ID da categoria para filtro (opcional).
     * @param categoryDescription Descrição da categoria (filtro por nome parcial, opcional).
     * @param beerDescription Descrição da cerveja (filtro por nome parcial, opcional).
     * @param minQuantity Quantidade mínima em estoque (opcional).
     * @param maxQuantity Quantidade máxima em estoque (opcional).
     * @param page Número da página (padrão: 0).
     * @param size Tamanho da página (padrão: 10).
     * @return {@link ResponseEntity} contendo uma {@link Page} de {@link BeerStockDTO}.
     */
    @Operation(summary = "List all beers", description = "Returns a paginated list of beers, optionally filtered by category, name, or quantity.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved beers"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @GetMapping
    public ResponseEntity<Page<BeerStockDTO>> findAllBeer(
            @Parameter(description = "Category ID to filter", example = "1") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Category description filter", example = "Lager") @RequestParam(defaultValue = "") String categoryDescription,
            @Parameter(description = "Beer description filter", example = "Pale Ale") @RequestParam(defaultValue = "") String beerDescription,
            @Parameter(description = "Minimum quantity filter", example = "10") @RequestParam(required = false) Integer minQuantity,
            @Parameter(description = "Maximum quantity filter", example = "50") @RequestParam(required = false) Integer maxQuantity,
            @Parameter(description = "Page number", example = "0") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") Integer size
    ) {
        logger.info("GET /beers iniciado. Filtros: CatID={}, Desc='{}', Page={}", categoryId, beerDescription, page);

        PageRequest pageable = PageRequest.of(page, size);
        Page<BeerStockDTO> result = stockService.findAllBeer(categoryId, categoryDescription, beerDescription, minQuantity, maxQuantity, pageable);

        logger.info("GET /beers finalizado. Total de itens: {}", result.getTotalElements());
        return ResponseEntity.ok(result);
    }

    // ================= GET USING NATIVE FUNCTION =================
    /**
     * Retorna uma lista de cervejas filtradas através de uma função nativa PL/pgSQL
     * no banco de dados.
     *
     * @param beerId ID da cerveja para filtro (opcional).
     * @param beerDescription Descrição da cerveja (filtro por nome parcial, opcional).
     * @param minQuantity Quantidade mínima em estoque (opcional).
     * @param maxQuantity Quantidade máxima em estoque (opcional).
     * @param daysUntilExpiry Dias restantes até o vencimento para filtro (opcional).
     * @param pageSize Tamanho da página.
     * @param pageNumber Número da página.
     * @return {@link ResponseEntity} contendo uma {@link List} de {@link BeerStockDTO}.
     */
    @Operation(summary = "Filter beers using PL/pgSQL function", description = "Uses native PostgreSQL function to filter beers by ID, quantity, expiry, etc.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved beers"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @GetMapping("/native")
    public ResponseEntity<List<BeerStockDTO>> filtrarUsandoFuncao(
            @Parameter(description = "Beer ID filter", example = "2") @RequestParam(required = false) Long beerId,
            @Parameter(description = "Beer description filter", example = "Pale Ale") @RequestParam(defaultValue = "") String beerDescription,
            @Parameter(description = "Minimum quantity filter", example = "10") @RequestParam(required = false) Integer minQuantity,
            @Parameter(description = "Maximum quantity filter", example = "50") @RequestParam(required = false) Integer maxQuantity,
            @Parameter(description = "Days until expiry filter", example = "7") @RequestParam(required = false) Integer daysUntilExpiry,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "Page number", example = "0") @RequestParam(defaultValue = "0") Integer pageNumber
    ) {
        logger.info("GET /beers/native iniciado. BeerID={}, Expira em {} dias, Page={}", beerId, daysUntilExpiry, pageNumber);

        List<BeerStockDTO> list = stockService.findUsingPlpgsqlFunction(beerId, beerDescription, minQuantity, maxQuantity, daysUntilExpiry, pageSize, pageNumber);

        logger.info("GET /beers/native finalizado. Itens retornados: {}", list.size());
        return ResponseEntity.ok(list);
    }

    // ================= GET EXPIRED BEERS =================
    /**
     * Retorna um relatório de todas as cervejas que estão vencidas (ou com data de
     * validade anterior ou igual) à data de referência fornecida.
     *
     * @param referenceDate Data de referência em formato ISO (yyyy-MM-dd) para verificar o vencimento.
     * @return {@link ResponseEntity} contendo uma {@link List} de {@link BeerStockDTO} vencidas.
     */
    @Operation(summary = "Get expired beers", description = "Returns all beers that are expired as of the reference date.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved expired beers"),
            @ApiResponse(responseCode = "400", description = "Invalid date format")
    })
    @GetMapping("/expired")
    public ResponseEntity<List<BeerStockDTO>> getExpiredBeers(
            @Parameter(description = "Reference date in ISO format (yyyy-MM-dd)", example = "2025-12-09") @RequestParam String referenceDate
    ) {
        logger.info("GET /beers/expired iniciado. Data de referência: {}", referenceDate);

        LocalDate date = LocalDate.parse(referenceDate);
        List<BeerStockDTO> list = stockService.getExpiredBeersReport(date);

        logger.info("GET /beers/expired finalizado. Total de cervejas vencidas: {}", list.size());
        return ResponseEntity.ok(list);
    }

    // ================= POST NEW BEER =================
    /**
     * Insere uma nova cerveja no sistema (sem estoque inicial).
     *
     * @param dto O {@link BeerInsertDTO} contendo os dados da cerveja a ser inserida.
     * @return {@link ResponseEntity} contendo o {@link BeerInsertDTO} criado e o status HTTP 201 Created.
     */
    @Operation(summary = "Insert a new beer", description = "Adds a new beer to the inventory and returns its data.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Beer successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid beer data")
    })
    @PostMapping
    public ResponseEntity<BeerInsertDTO> insert(
            @Parameter(description = "Beer data to insert") @RequestBody BeerInsertDTO dto
    ) {
        logger.info("POST /beers iniciado. Inserindo nova cerveja: {}", dto.getName());

        BeerInsertDTO newDto = beerService.insert(dto);

        // Constrói a URI do novo recurso
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newDto.getId())
                .toUri();

        logger.info("POST /beers finalizado. Nova cerveja ID: {}", newDto.getId());
        return ResponseEntity.created(uri).body(newDto);
    }

    // ================= DELETE BEER =================
    /**
     * Exclui uma cerveja pelo seu ID.
     *
     * @param id O ID da cerveja a ser excluída.
     * @return {@link ResponseEntity} com status HTTP 204 No Content.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     * @throws com.anapedra.stock_manager.services.exceptions.DatabaseException Se houver violação de integridade referencial.
     */
    @Operation(summary = "Delete a beer", description = "Deletes a beer by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Beer successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Beer not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the beer to delete", example = "1") @PathVariable Long id
    ) {
        logger.warn("DELETE /beers/{} iniciado.", id);

        beerService.delete(id);

        logger.info("DELETE /beers/{} finalizado. Status: 204 NO CONTENT.", id);
        return ResponseEntity.noContent().build();
    }
}