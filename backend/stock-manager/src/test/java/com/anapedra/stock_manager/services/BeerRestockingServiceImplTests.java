package com.anapedra.stock_manager.services;

import com.anapedra.stock_manager.domain.dtos.BeerRestockingDTO;
import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.BeerRestocking;
import com.anapedra.stock_manager.domain.entities.Category;
import com.anapedra.stock_manager.domain.entities.Stock;
import com.anapedra.stock_manager.repositories.BeerRepository;
import com.anapedra.stock_manager.repositories.BeerRestockingRepository;
import com.anapedra.stock_manager.services.impl.BeerRestockingServiceImpl;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class BeerRestockingServiceImplTests {


private BeerRestockingServiceImpl service;

@Mock
private BeerRestockingRepository beerRestockingRepository;

@Mock
private BeerRepository beerRepository;

private MeterRegistry meterRegistry;

private Long existingId;
private Long nonExistingId;
private BeerRestocking beerRestocking;
private BeerRestockingDTO beerRestockingDTO;
private Beer beer;
private Category category;

@BeforeEach
void setUp() {
    existingId = 1L;
    nonExistingId = 100L;

    meterRegistry = new SimpleMeterRegistry();
    service = new BeerRestockingServiceImpl(
            beerRestockingRepository, beerRepository, meterRegistry
    );

    category = new Category(2L, "Lager", null);

    Stock initialStockObj = new Stock(100, null);

    beer = new Beer(existingId, "Pilsen Extra","", 5.0, 10.0,
            LocalDate.of(2023,10,1), LocalDate.of(2024,10,1));
    beer.setStock(initialStockObj);
    beer.getCategories().add(category);

    beerRestocking = new BeerRestocking(existingId, 50, Instant.now(), beer);
    beerRestockingDTO = new BeerRestockingDTO(existingId, 50);

    // Mocks essenciais
    when(beerRestockingRepository.findById(existingId)).thenReturn(Optional.of(beerRestocking));
    lenient().when(beerRestockingRepository.findById(nonExistingId)).thenReturn(Optional.empty());
    when(beerRestockingRepository.findAll()).thenReturn(Arrays.asList(beerRestocking));
    when(beerRestockingRepository.save(any(BeerRestocking.class))).thenReturn(beerRestocking);

    when(beerRepository.findById(existingId)).thenReturn(Optional.of(beer));
    lenient().when(beerRepository.findById(nonExistingId)).thenReturn(Optional.empty());

    doNothing().when(beerRestockingRepository).deleteById(existingId);
    when(beerRestockingRepository.existsById(existingId)).thenReturn(true);
    when(beerRestockingRepository.existsById(nonExistingId)).thenReturn(false);
}

@Test
void findAllShouldReturnListOfBeerRestockingDTO() {
    List<BeerRestockingDTO> result = service.findAll();

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    verify(beerRestockingRepository, times(1)).findAll();
}

@Test
void findByIdShouldReturnBeerRestockingDTOWhenIdExists() {
    BeerRestockingDTO result = service.findById(existingId);

    assertNotNull(result);
    assertEquals(existingId, result.getBeerId());
    assertEquals(50, result.getQuantity());
    verify(beerRestockingRepository, times(1)).findById(existingId);
}

@Test
void findByIdShouldThrowRuntimeExceptionWhenIdDoesNotExist() {
    RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
        service.findById(nonExistingId);
    });

    assertEquals("Book restocking not found", thrown.getMessage());
    verify(beerRestockingRepository, times(1)).findById(nonExistingId);
}

@Test
void createShouldReturnBeerRestockingDTOWhenSuccessful() {
    Integer quantityToRestock = 7;
    Integer initialStock = beer.getStock().getQuantity();
    Integer finalStock = initialStock + quantityToRestock;

    BeerRestockingDTO inputDTO = new BeerRestockingDTO(existingId, quantityToRestock);

    BeerRestocking savedBeerRestocking = new BeerRestocking(existingId, quantityToRestock, Instant.now(), beer);
    when(beerRestockingRepository.save(any(BeerRestocking.class))).thenReturn(savedBeerRestocking);

    BeerRestockingDTO result = service.create(inputDTO);

    assertNotNull(result);
    assertEquals(existingId, result.getBeerId());
    assertEquals(quantityToRestock, result.getQuantity());
    assertEquals(finalStock, beer.getStock().getQuantity());

    verify(beerRepository, times(1)).findById(existingId);
    verify(beerRestockingRepository, times(1)).save(any(BeerRestocking.class));
}

@Test
void createShouldThrowRuntimeExceptionWhenBeerDoesNotExist() {
    BeerRestockingDTO inputDTO = new BeerRestockingDTO(nonExistingId, 60);
    RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
        service.create(inputDTO);
    });

    assertEquals("Book not found", thrown.getMessage());
    verify(beerRepository, times(1)).findById(nonExistingId);
    verify(beerRestockingRepository, never()).save(any());
}

@Test
void updateShouldReturnBeerRestockingDTOWhenIdExistsAndBeerExists() {
    BeerRestockingDTO updateDTO = new BeerRestockingDTO(existingId, 75);
    updateDTO.setBeerId(existingId);

    BeerRestockingDTO result = service.update(existingId, updateDTO);

    assertNotNull(result);
    assertEquals(existingId, result.getBeerId());
    verify(beerRestockingRepository, times(1)).findById(existingId);
    verify(beerRestockingRepository, times(1)).save(any(BeerRestocking.class));
    verify(beerRepository, times(1)).findById(existingId);
}

@Test
void updateShouldThrowRuntimeExceptionWhenRestockingIdDoesNotExist() {
    BeerRestockingDTO updateDTO = new BeerRestockingDTO(nonExistingId, 75);

    RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
        service.update(nonExistingId, updateDTO);
    });

    assertEquals("Book restocking not found", thrown.getMessage());
    verify(beerRestockingRepository, times(1)).findById(nonExistingId);
    verify(beerRepository, never()).findById(any());
}

@Test
void updateShouldThrowRuntimeExceptionWhenBeerIdInDTODoesNotExist() {
    BeerRestockingDTO updateDTO = new BeerRestockingDTO(nonExistingId, 75);
    updateDTO.setBeerId(nonExistingId);

    RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
        service.update(existingId, updateDTO);
    });

    assertEquals("Book not found", thrown.getMessage());
    verify(beerRestockingRepository, times(1)).findById(existingId);
    verify(beerRepository, times(1)).findById(nonExistingId);
    verify(beerRestockingRepository, never()).save(any());
}

@Test
void deleteShouldDoNothingWhenIdExists() {
    assertDoesNotThrow(() -> service.delete(existingId));
    verify(beerRestockingRepository, times(1)).existsById(existingId);
    verify(beerRestockingRepository, times(1)).deleteById(existingId);
}

@Test
void deleteShouldThrowRuntimeExceptionWhenIdDoesNotExist() {
    RuntimeException thrown = assertThrows(RuntimeException.class, () -> service.delete(nonExistingId));

    assertEquals("Book restocking not found", thrown.getMessage());
    verify(beerRestockingRepository, times(1)).existsById(nonExistingId);
    verify(beerRestockingRepository, never()).deleteById(any());
}


}
