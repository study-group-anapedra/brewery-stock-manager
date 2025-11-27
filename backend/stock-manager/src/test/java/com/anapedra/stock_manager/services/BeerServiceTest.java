package com.anapedra.stock_manager.services;

import com.anapedra.stock_manager.domain.dtos.BeerFilterDTO;
import com.anapedra.stock_manager.domain.dtos.BeerInsertDTO;
import com.anapedra.stock_manager.domain.dtos.CategoryDTO;
import com.anapedra.stock_manager.domain.dtos.StockInputDTO;
import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.Category;
import com.anapedra.stock_manager.domain.entities.Stock;
import com.anapedra.stock_manager.repositories.BeerRepository;
import com.anapedra.stock_manager.repositories.CategoryRepository;
import com.anapedra.stock_manager.services.exceptions.DatabaseException;
import com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException;
import com.anapedra.stock_manager.services.impl.BeerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class BeerServiceTest {

    @InjectMocks
    private BeerServiceImpl beerService;

    @Mock
    private BeerRepository beerRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private Beer beer;
    private BeerInsertDTO beerInsertDTO;
    private PageImpl<Beer> page;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 2L;

        // Criando entidades mock
        Category category = new Category(5L, "IPA",null);
        Stock stock = new Stock(50, null);
        
        // CORREÇÃO AQUI: Removendo 'stock' do construtor da Beer.
        beer = new Beer(existingId, "IPA Teste", "url/img", 5.5, 12.0, LocalDate.now(), LocalDate.now().plusYears(1));
        
        beer.setStock(stock); // Associa o estoque
        beer.getCategories().add(category);
        stock.setBeer(beer);

        // Criando DTOs mock
        StockInputDTO stockDTO = new StockInputDTO(50);
        CategoryDTO categoryDTO = new CategoryDTO(5L, "IPA",null);
        beerInsertDTO = new BeerInsertDTO(beer);
        beerInsertDTO.setStock(stockDTO); // Garante que o DTO tem o Stock
        beerInsertDTO.getCategories().clear();
        beerInsertDTO.getCategories().add(categoryDTO);

        // Configuração de Mocks para Paging
        page = new PageImpl<>(List.of(beer));
        Pageable pageable = PageRequest.of(0, 10);

        // Comportamentos padrão dos Mocks
        when(beerRepository.findById(existingId)).thenReturn(Optional.of(beer));
        when(beerRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        when(beerRepository.save(any(Beer.class))).thenReturn(beer);

        when(beerRepository.existsById(existingId)).thenReturn(true);
        when(beerRepository.existsById(nonExistingId)).thenReturn(false);
        when(beerRepository.existsById(dependentId)).thenReturn(true); // Para teste de integridade

        doNothing().when(beerRepository).deleteById(existingId);
        doThrow(DataIntegrityViolationException.class).when(beerRepository).deleteById(dependentId);

        // Mocks para Category/Stock
        when(categoryRepository.findAllById(anySet())).thenReturn(List.of(category));

        // Mocks para findAllBeer (comportamento de filtro)
        when(beerRepository.findAllBeer(any(), any(), any(), any(), any(), any())).thenReturn(page);
        when(beerRepository.findAll(any(Pageable.class))).thenReturn(page);
    }

    // --- Testes FIND ALL (Filtragem) ---

    @Test
    @DisplayName("findAllBeer deve retornar Page de BeerFilterDTO com sucesso")
    void findAllBeer_shouldReturnPageBeerFilterDTO_successfully() {
        Page<BeerFilterDTO> result = beerService.findAllBeer(null, null, null, null, null, PageRequest.of(0, 10));
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // Ajustei a verificação do número de argumentos, pois o BeerService tem 6 argumentos de filtro + 1 Pageable
        verify(beerRepository, times(1)).findAllBeer(any(), any(), any(), any(), any(), any(Pageable.class)); 
    }

    // --- Testes FIND BY ID ---

    @Test
    @DisplayName("findById deve retornar BeerFilterDTO quando ID existir")
    void findById_shouldReturnBeerFilterDTO_whenIdExists() {
        BeerFilterDTO result = beerService.findById(existingId);
        assertNotNull(result);
        assertEquals(existingId, result.getId());
        verify(beerRepository, times(1)).findById(existingId);
    }

    @Test
    @DisplayName("findById deve lançar ResourceNotFoundException quando ID não existir")
    void findById_shouldThrowResourceNotFoundException_whenIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> {
            beerService.findById(nonExistingId);
        });
        verify(beerRepository, times(1)).findById(nonExistingId);
    }

    // --- Testes INSERT ---

    @Test
    @DisplayName("insert deve retornar BeerInsertDTO com ID quando inserir com sucesso")
    void insert_shouldReturnBeerInsertDTO_withSuccess() {
        BeerInsertDTO result = beerService.insert(beerInsertDTO);
        assertNotNull(result);
        assertEquals(existingId, result.getId());
        verify(beerRepository, times(1)).save(any(Beer.class));
    }

    // --- Testes UPDATE ---

    @Test
    @DisplayName("update deve retornar BeerInsertDTO quando ID existir")
    void update_shouldReturnBeerInsertDTO_whenIdExists() {
        BeerInsertDTO result = beerService.update(existingId, beerInsertDTO);
        assertNotNull(result);
        assertEquals(existingId, result.getId());
        verify(beerRepository, times(1)).save(any(Beer.class));
    }

    @Test
    @DisplayName("update deve lançar ResourceNotFoundException quando ID não existir")
    void update_shouldThrowResourceNotFoundException_whenIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> {
            beerService.update(nonExistingId, beerInsertDTO);
        });
        verify(beerRepository, times(1)).findById(nonExistingId);
        verify(beerRepository, never()).save(any(Beer.class));
    }

    // --- Testes DELETE ---

    @Test
    @DisplayName("delete não deve retornar nada quando ID existir (sucesso)")
    void delete_shouldDoNothing_whenIdExists() {
        assertDoesNotThrow(() -> {
            beerService.delete(existingId);
        });
        verify(beerRepository, times(1)).existsById(existingId);
        verify(beerRepository, times(1)).deleteById(existingId);
    }

    @Test
    @DisplayName("delete deve lançar ResourceNotFoundException quando ID não existir")
    void delete_shouldThrowResourceNotFoundException_whenIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> {
            beerService.delete(nonExistingId);
        });
        verify(beerRepository, times(1)).existsById(nonExistingId);
        verify(beerRepository, never()).deleteById(nonExistingId);
    }

    @Test
    @DisplayName("delete deve lançar DatabaseException quando houver violação de integridade")
    void delete_shouldThrowDatabaseException_whenIntegrityViolation() {
        assertThrows(DatabaseException.class, () -> {
            beerService.delete(dependentId);
        });
        verify(beerRepository, times(1)).existsById(dependentId);
        verify(beerRepository, times(1)).deleteById(dependentId);
    }
}