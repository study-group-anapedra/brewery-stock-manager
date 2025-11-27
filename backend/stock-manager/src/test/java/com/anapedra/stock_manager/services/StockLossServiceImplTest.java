package com.anapedra.stock_manager.services;

import com.anapedra.stock_manager.domain.dtos.StockLossDTO;
import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.Stock;
import com.anapedra.stock_manager.domain.entities.StockLoss;
import com.anapedra.stock_manager.domain.enums.LossReason;
import com.anapedra.stock_manager.repositories.BeerRepository;
import com.anapedra.stock_manager.repositories.StockLossRepository;
import com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException;
import com.anapedra.stock_manager.services.impl.StockLossServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StockLossServiceImplTest {

    @Mock
    private BeerRepository beerRepository;

    @Mock
    private StockLossRepository stockLossRepository;

    @InjectMocks
    private StockLossServiceImpl service;

    private Beer beer;
    private StockLossDTO dto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Stock stock = new Stock();
        stock.setQuantity(50);

        beer = new Beer();
        beer.setId(1L);
        beer.setName("IPA Test");
        beer.setStock(stock);

        dto = new StockLossDTO();
        dto.setBeerId(1L);
        dto.setQuantityLost(10);
        dto.setLossDate(LocalDate.now());
        dto.setDescription("Teste");
    }

   
    @Test
    void registerLoss_ShouldThrow_WhenBeerDoesNotExist() {
        when(beerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.registerLoss(dto));
    }

    @Test
    void registerLoss_ShouldThrow_WhenQuantityLostIsGreaterThanStock() {
        dto.setQuantityLost(999);

        when(beerRepository.findById(1L)).thenReturn(Optional.of(beer));

        assertThrows(IllegalArgumentException.class, () -> service.registerLoss(dto));
    }

    // ----------------------------------------------------------
    // FIND LOSSES BY FILTERS
    // ----------------------------------------------------------

    @Test
    void findLossesByFilters_ShouldReturnPage_WhenItemsExist() {
        Page<StockLoss> page = new PageImpl<>(
                java.util.List.of(new StockLoss(1L, beer, 5, LossReason.EXPIRED, LocalDate.now(), "teste"))
        );

        when(stockLossRepository.findLossesByFilters(
                any(), any(), anyString(), any(), any(), any(), any(Pageable.class)
        )).thenReturn(page);

        Pageable pageable = PageRequest.of(0, 10);

        Page<StockLossDTO> result = service.findLossesByFilters(
                1, 1L, "IPA", 2L,
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(5),
                pageable
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("IPA Test", result.getContent().get(0).getBeerName());
    }

    @Test
    void findLossesByFilters_ShouldReturnEmptyPage_WhenNoResults() {
        when(stockLossRepository.findLossesByFilters(
                any(), any(), any(), any(), any(), any(), any()
        )).thenReturn(Page.empty());

        Pageable pageable = PageRequest.of(0, 10);

        Page<StockLossDTO> result = service.findLossesByFilters(
                null, null, null, null, null, null, pageable
        );

        assertTrue(result.isEmpty());
    }
}
