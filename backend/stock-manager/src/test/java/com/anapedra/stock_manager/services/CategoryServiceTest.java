package com.anapedra.stock_manager.services;

import com.anapedra.stock_manager.domain.dtos.CategoryDTO;
import com.anapedra.stock_manager.domain.entities.Category;
import com.anapedra.stock_manager.repositories.CategoryRepository;
import com.anapedra.stock_manager.services.exceptions.DatabaseException;
import com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException;
import com.anapedra.stock_manager.services.impl.CategoryServiceImpl;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;

import jakarta.persistence.EntityNotFoundException; // Necessário para mock de getReferenceById

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CategoryServiceTest {

    private CategoryServiceImpl service;

    @Mock
    private CategoryRepository repository;

    private MeterRegistry meterRegistry;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private Category categoryPilsen;
    
    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 100L;
        dependentId = 4L;

        // Note: Category no DTO original tem apenas 'description', mas a entidade completa tem mais campos.
        // Assumindo que o construtor da entidade aceita ID, Name e Description para propósitos de mock.
        categoryPilsen = new Category(existingId, "Pilsen", "Cerveja leve e refrescante");


        meterRegistry = new SimpleMeterRegistry(); 

        service = new CategoryServiceImpl(repository, meterRegistry);

        // Mocks do repository
        when(repository.findAll()).thenReturn(List.of(categoryPilsen));
        when(repository.findById(existingId)).thenReturn(Optional.of(categoryPilsen));
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
        
        // CORREÇÃO ESSENCIAL: Mocking getReferenceById para o método update
        when(repository.getReferenceById(existingId)).thenReturn(categoryPilsen);
        when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
        
        when(repository.existsById(existingId)).thenReturn(true);
        when(repository.existsById(nonExistingId)).thenReturn(false);
        
        // Simular o save (o serviço chama save após atualizar a entidade obtida via getReferenceById)
        when(repository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0)); 
    }

    @Test
    @DisplayName("findAll deve retornar uma lista de CategoryDTO")
    void findAll_shouldReturnListOfCategoryDTO() {
        List<CategoryDTO> result = service.findAll();
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        // Ajuste a verificação conforme o DTO (se o DTO tem Description, teste a Description)
        Assertions.assertEquals("Cerveja leve e refrescante", result.get(0).getDescription()); 
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("findById deve retornar CategoryDTO quando ID existe")
    void findById_shouldReturnCategoryDTO_whenIdExists() {
        CategoryDTO result = service.findById(existingId);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
        verify(repository, times(1)).findById(existingId);
    }

    @Test
    @DisplayName("findById deve lançar ResourceNotFoundException quando ID não existe")
    void findById_shouldThrowResourceNotFoundException_whenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));
        verify(repository, times(1)).findById(nonExistingId);
    }

    @Test
    @DisplayName("insert deve retornar CategoryDTO com novo ID e descrição correta")
    void insert_shouldReturnCategoryDTOWithNewId() {
        CategoryDTO newDTO = new CategoryDTO(null, "Lager", "Nova descrição de Lager");

        // Mock para garantir que o save retorne a entidade com o ID (simulando a geração de ID pelo DB)
        doAnswer(invocation -> {
            Category categoryToSave = invocation.getArgument(0);
            categoryToSave.setId(existingId);
            categoryToSave.setDescription(newDTO.getDescription()); // Garante que a descrição correta é retornada
            return categoryToSave;
        }).when(repository).save(any(Category.class));

        CategoryDTO result = service.insert(newDTO);

        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals("Nova descrição de Lager", result.getDescription());
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("update deve retornar CategoryDTO atualizada quando ID existe")
    void update_shouldReturnUpdatedCategoryDTO_whenIdExists() {
        // DTO com os novos dados
        CategoryDTO updatedDTO = new CategoryDTO(existingId, "IPA", "Descrição de IPA Atualizada");

        CategoryDTO result = service.update(existingId, updatedDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
        // Verifica se a descrição do DTO retornado é a que foi atualizada
        Assertions.assertEquals("Descrição de IPA Atualizada", result.getDescription()); 

        // CORREÇÃO DE VERIFICAÇÃO: O serviço usa getReferenceById(id)
        verify(repository, times(1)).getReferenceById(existingId); 
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("update deve lançar ResourceNotFoundException quando ID não existe")
    void update_shouldThrowResourceNotFoundException_whenIdDoesNotExist() {
        CategoryDTO dummyDTO = new CategoryDTO(nonExistingId, "Inexistente", "Descrição");
        
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistingId, dummyDTO));
        
        // CORREÇÃO DE VERIFICAÇÃO: O serviço usa getReferenceById(id)
        verify(repository, times(1)).getReferenceById(nonExistingId); 
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("delete deve ser bem-sucedido quando ID existe e não é dependente")
    void delete_shouldDoNothing_whenIdExists() {
        Assertions.assertDoesNotThrow(() -> service.delete(existingId));
        verify(repository, times(1)).existsById(existingId);
        verify(repository, times(1)).deleteById(existingId);
    }

    @Test
    @DisplayName("delete deve lançar ResourceNotFoundException quando ID não existe")
    void delete_shouldThrowResourceNotFoundException_whenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
        verify(repository, times(1)).existsById(nonExistingId);
        verify(repository, never()).deleteById(nonExistingId);
    }

    @Test
    @DisplayName("delete deve lançar DatabaseException quando há falha de integridade referencial")
    void delete_shouldThrowDatabaseException_whenIntegrityViolation() {
        when(repository.existsById(dependentId)).thenReturn(true);
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

        Assertions.assertThrows(DatabaseException.class, () -> service.delete(dependentId));

        verify(repository, times(1)).existsById(dependentId);
        verify(repository, times(1)).deleteById(dependentId);
    }
}