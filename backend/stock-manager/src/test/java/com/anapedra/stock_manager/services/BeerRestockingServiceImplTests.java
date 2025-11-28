package com.anapedra.stock_manager.services;

import com.anapedra.stock_manager.domain.dtos.BeerRestockingDTO;
import com.anapedra.stock_manager.domain.dtos.MinCategoryDTO;
import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.BeerRestocking;
import com.anapedra.stock_manager.domain.entities.Category;

import com.anapedra.stock_manager.repositories.BeerRepository;
import com.anapedra.stock_manager.repositories.BeerRestockingRepository;
import com.anapedra.stock_manager.services.impl.BeerRestockingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
public class BeerRestockingServiceImplTests {

    @InjectMocks
    private BeerRestockingServiceImpl service;

    @Mock
    private BeerRestockingRepository beerRestockingRepository;
    
    @Mock
    private BeerRepository beerRepository;

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
        
        // --- 1. Inicialização de Entidades Base ---
        
        // Dados Mockados de Categoria
        category = new Category(2L, "Lager",null);
        
        // Dados Mockados de Beer
        // Note: O construtor usado aqui é uma suposição baseada na sua DTO e Entidade.
        // Assumindo: Beer(Long id, String name, Double alcoholContent, Integer stock, LocalDate manufactureDate, LocalDate expirationDate)
        beer = new Beer(existingId, "Pilsen Extra","" ,5.0, 10.0,LocalDate.of(2023, 10, 1), LocalDate.of(2024, 10, 1));
        beer.getCategories().add(category);
        
        // Dados Mockados de BeerRestocking (Entidade)
        // Assumindo: BeerRestocking(Long id, Integer quantity, Instant moment, Beer beer)
        beerRestocking = new BeerRestocking(existingId, 50, Instant.now(), beer);
        
        // Dados Mockados de BeerRestockingDTO
        beerRestockingDTO = new BeerRestockingDTO(existingId, 50, Instant.now(), 
                beer.getName(), beer.getAlcoholContent(), beer.getManufactureDate(), beer.getExpirationDate());
        beerRestockingDTO.getCategoryNames().add(new MinCategoryDTO(category));


        // --- 2. Configuração dos Mocks (Comportamento do Repositório) ---
        
        // findById
        when(beerRestockingRepository.findById(existingId)).thenReturn(Optional.of(beerRestocking));
        when(beerRestockingRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // findAll
        List<BeerRestocking> list = Arrays.asList(beerRestocking);
        when(beerRestockingRepository.findAll()).thenReturn(list);
        
        // create e update (save)
        when(beerRestockingRepository.save(any(BeerRestocking.class))).thenReturn(beerRestocking);
        
        // BeerRepository - Usado em copyDtoToEntity (DTO.getId() é o ID da Beer)
        when(beerRepository.findById(existingId)).thenReturn(Optional.of(beer));
        when(beerRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // delete
        doNothing().when(beerRestockingRepository).deleteById(existingId);
        when(beerRestockingRepository.existsById(existingId)).thenReturn(true);
        when(beerRestockingRepository.existsById(nonExistingId)).thenReturn(false);
    }
    
    // --- Testes para findAll ---
    
    @Test
    void findAllShouldReturnListOfBeerRestockingDTO() {
        // ACT
        List<BeerRestockingDTO> result = service.findAll();

        // ASSERT
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(beerRestockingRepository, times(1)).findAll();
    }
    
    // --- Testes para findById ---

    @Test
    void findByIdShouldReturnBeerRestockingDTOWhenIdExists() {
        // ACT
        BeerRestockingDTO result = service.findById(existingId);

        // ASSERT
        assertNotNull(result);
        assertEquals(existingId, result.getId());
        assertEquals("Pilsen Extra", result.getBeerName());
        verify(beerRestockingRepository, times(1)).findById(existingId);
    }

    @Test
    void findByIdShouldThrowRuntimeExceptionWhenIdDoesNotExist() {
        // ACT & ASSERT
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            service.findById(nonExistingId);
        });
        
        assertEquals("Book restocking not found", thrown.getMessage());
        verify(beerRestockingRepository, times(1)).findById(nonExistingId);
    }
    
    // --- Testes para create ---

    @Test
    void createShouldReturnBeerRestockingDTOWhenSuccessful() {
        // ARRANGE
        BeerRestockingDTO inputDTO = new BeerRestockingDTO(null, 60, null, 
                null, null, null, null);
        inputDTO.setId(existingId); // ID da Beer

        // ACT
        BeerRestockingDTO result = service.create(inputDTO);

        // ASSERT
        assertNotNull(result);
        assertEquals(existingId, result.getId()); 
        assertEquals(50, result.getQuantity()); // Retorna a quantidade da entidade mockada salva
        assertNotNull(result.getMoment());
        verify(beerRestockingRepository, times(1)).save(any(BeerRestocking.class));
        verify(beerRepository, times(1)).findById(existingId);
    }

    @Test
    void createShouldThrowRuntimeExceptionWhenBeerDoesNotExist() {
        // ARRANGE
        BeerRestockingDTO inputDTO = new BeerRestockingDTO(null, 60, null, null, null, null, null);
        inputDTO.setId(nonExistingId); // ID de Beer não existente

        // ACT & ASSERT
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            service.create(inputDTO);
        });
        
        assertEquals("Book not found", thrown.getMessage());
        verify(beerRepository, times(1)).findById(nonExistingId);
        verify(beerRestockingRepository, never()).save(any());
    }
    
    // --- Testes para update ---

    @Test
    void updateShouldReturnBeerRestockingDTOWhenIdExistsAndBeerExists() {
        // ARRANGE
        BeerRestockingDTO updateDTO = new BeerRestockingDTO(existingId, 75, Instant.now(), 
                "Nova Beer", 6.5, LocalDate.of(2023, 12, 1), LocalDate.of(2024, 12, 1));
        updateDTO.setId(existingId); // ID da Beer

        // ACT
        BeerRestockingDTO result = service.update(existingId, updateDTO);

        // ASSERT
        assertNotNull(result);
        assertEquals(existingId, result.getId());
        verify(beerRestockingRepository, times(1)).findById(existingId); // Para buscar o Restocking
        verify(beerRestockingRepository, times(1)).save(any(BeerRestocking.class));
        verify(beerRepository, times(1)).findById(existingId); // Para buscar a Beer
    }

    @Test
    void updateShouldThrowRuntimeExceptionWhenRestockingIdDoesNotExist() {
        // ARRANGE
        BeerRestockingDTO updateDTO = new BeerRestockingDTO(nonExistingId, 75, Instant.now(), 
                "Nova Beer", 6.5, LocalDate.of(2023, 12, 1), LocalDate.of(2024, 12, 1));

        // ACT & ASSERT
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            service.update(nonExistingId, updateDTO);
        });
        
        assertEquals("Book restocking not found", thrown.getMessage());
        verify(beerRestockingRepository, times(1)).findById(nonExistingId);
        verify(beerRepository, never()).findById(any());
    }

    @Test
    void updateShouldThrowRuntimeExceptionWhenBeerIdInDTODoesNotExist() {
        // ARRANGE
        BeerRestockingDTO updateDTO = new BeerRestockingDTO(nonExistingId, 75, Instant.now(), 
                "Nova Beer", 6.5, LocalDate.of(2023, 12, 1), LocalDate.of(2024, 12, 1));
        updateDTO.setId(nonExistingId); // ID de Beer não existente
        
        // Mockando findById para simular que o Restocking existe
        when(beerRestockingRepository.findById(existingId)).thenReturn(Optional.of(beerRestocking));

        // ACT & ASSERT
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            service.update(existingId, updateDTO); // Usa existingId para Restocking
        });
        
        assertEquals("Book not found", thrown.getMessage());
        verify(beerRestockingRepository, times(1)).findById(existingId);
        verify(beerRepository, times(1)).findById(nonExistingId); // Usa nonExistingId para Beer
        verify(beerRestockingRepository, never()).save(any());
    }
    
    // --- Testes para delete ---

    @Test
    void deleteShouldDoNothingWhenIdExists() {
        // ACT
        assertDoesNotThrow(() -> service.delete(existingId));

        // ASSERT
        verify(beerRestockingRepository, times(1)).existsById(existingId);
        verify(beerRestockingRepository, times(1)).deleteById(existingId);
    }

    @Test
    void deleteShouldThrowRuntimeExceptionWhenIdDoesNotExist() {
        // ACT & ASSERT
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            service.delete(nonExistingId);
        });
        
        assertEquals("Book restocking not found", thrown.getMessage());
        verify(beerRestockingRepository, times(1)).existsById(nonExistingId);
        verify(beerRestockingRepository, never()).deleteById(any());
    }
}