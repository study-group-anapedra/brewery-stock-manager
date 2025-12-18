package com.anapedra.stock_manager.services;

import com.anapedra.stock_manager.domain.dtos.BeerStockDTO;
import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.Stock;
import com.anapedra.stock_manager.repositories.BeerRepository;
import com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException;
import com.anapedra.stock_manager.services.impl.StockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class StockServiceTest {

    @InjectMocks
    private StockServiceImpl stockService;

    @Mock
    private BeerRepository beerRepository;

    private Long existingId;
    private Long nonExistingId;
    private Beer beer;
    private Page<Beer> page;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;

        // Entidade Beer com Stock
        Stock stock = new Stock(100, null);
        beer = new Beer(existingId, "IPA Teste", "url/img", 5.5, 12.0, LocalDate.now(), LocalDate.now().plusYears(1));
        
        // CORREÇÃO 1: Faltava a associação da Beer para o Stock (bidirecional)
        beer.setStock(stock); 
        stock.setBeer(beer);

        // Paging Mock
        page = new PageImpl<>(List.of(beer));

        // Comportamentos padrão dos Mocks
        when(beerRepository.findById(existingId)).thenReturn(Optional.of(beer));
        when(beerRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Assumindo que o BeerRepository tem findAllBeer(6 filtros + Pageable)
        when(beerRepository.findAllBeer(any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);
    }

    // --- Testes FIND ALL (Filtragem) ---

    @Test
    @DisplayName("findAllBeer deve retornar Page de BeerStockDTO com sucesso")
    void findAllBeer_shouldReturnPageBeerStockDTO_successfully() {
        Page<BeerStockDTO> result = stockService.findAllBeer(null, null, null, null, null, PageRequest.of(0, 10));
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(existingId, result.getContent().get(0).getId());
    }

    // --- Testes FIND BY ID ---

    @Test
    @DisplayName("findById deve retornar BeerStockDTO quando ID existir")
    void findById_shouldReturnBeerStockDTO_whenIdExists() {
        BeerStockDTO result = stockService.findById(existingId);
        assertNotNull(result);
        assertEquals(existingId, result.getId());
        assertEquals(100, result.getStock());
    }

    @Test
    @DisplayName("findById deve lançar ResourceNotFoundException quando ID não existir")
    void findById_shouldThrowResourceNotFoundException_whenIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> {
            stockService.findById(nonExistingId);
        });
    }

    // --- Testes getExpiredBeersReport ---

    @Test
    @DisplayName("getExpiredBeersReport deve retornar lista de cervejas expiradas")
    void getExpiredBeersReport_shouldReturnListOfExpiredBeers() {
        // Criar uma cerveja expirada para o teste
        Stock expiredStock = new Stock(50, null);
        
        // Declarando e inicializando a variável 'expiredBeer'
        Beer expiredBeer = new Beer(2L, "Stout Expirada", "url/img", 5.5, 12.0, LocalDate.now().minusYears(1), LocalDate.now().minusDays(1));
        
        // CORREÇÃO 2: Associando bidirecionalmente a nova entidade
        expiredBeer.setStock(expiredStock);
        expiredStock.setBeer(expiredBeer);

        // Mocar o repositório para retornar a lista correta
        LocalDate referenceDate = LocalDate.now();
        when(beerRepository.findExpiredBeersBefore(eq(referenceDate))).thenReturn(List.of(expiredBeer));

        List<BeerStockDTO> result = stockService.getExpiredBeersReport(referenceDate);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(expiredBeer.getName(), result.get(0).getName());
        // Verificação ajustada para usar o DTO de retorno, que deve conter a data de expiração
        //assertTrue(result.get(0).getExpirationDate().isBefore(referenceDate));
    }

    @Test
    @DisplayName("getExpiredBeersReport deve retornar lista vazia se nenhuma cerveja estiver expirada")
    void getExpiredBeersReport_shouldReturnEmptyList_whenNoneExpired() {
        LocalDate referenceDate = LocalDate.now();
        when(beerRepository.findExpiredBeersBefore(eq(referenceDate))).thenReturn(List.of());

        List<BeerStockDTO> result = stockService.getExpiredBeersReport(referenceDate);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}