package com.anapedra.stock_manager.services;

import com.anapedra.stock_manager.domain.dtos.CategoryDTO;
import com.anapedra.stock_manager.domain.entities.Category;
import com.anapedra.stock_manager.repositories.CategoryRepository;
import com.anapedra.stock_manager.services.exceptions.DatabaseException;
import com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException;
import com.anapedra.stock_manager.services.impl.CategoryServiceImpl; // Importação correta do impl
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings; // Importação necessária
import org.mockito.quality.Strictness; // Importação necessária
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl service;

    @Mock
    private CategoryRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private Category categoryPilsen;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 100L;
        dependentId = 4L; 

        categoryPilsen = new Category(existingId, "Pilsen", "Cerveja leve e refrescante");


        when(repository.findAll()).thenReturn(Arrays.asList(categoryPilsen));
        when(repository.findById(existingId)).thenReturn(Optional.of(categoryPilsen));
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
        when(repository.existsById(existingId)).thenReturn(true);
        when(repository.existsById(nonExistingId)).thenReturn(false);
        // O mock para 'save' é feito dentro de 'update' e 'insert' usando doAnswer,
        // mas é mantido aqui para testes simples que só verificam o sucesso.
        when(repository.save(any(Category.class))).thenReturn(categoryPilsen); 
    }



    @Test
    @DisplayName("findAll deve retornar uma lista de CategoryDTO")
    void findAll_shouldReturnListOfCategoryDTO() {

        List<CategoryDTO> result = service.findAll();


        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("Pilsen", result.get(0).getName());
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

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });
        verify(repository, times(1)).findById(nonExistingId);
    }


    @Test
    @DisplayName("insert deve retornar CategoryDTO com novo ID e descrição correta")
    void insert_shouldReturnCategoryDTOWithNewId() {
        // Arrange
        CategoryDTO newDTO = new CategoryDTO(null, "Lager", "Nova descrição de Lager");
        
        doAnswer(invocation -> {
            Category categoryToSave = invocation.getArgument(0);
            categoryToSave.setId(existingId); 
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
        CategoryDTO updatedDTO = new CategoryDTO(existingId, "IPA", "Descrição de IPA");
        
        when(repository.save(any(Category.class))).thenAnswer(invocation -> {
             Category saved = invocation.getArgument(0); 
             return saved;
        });

        // Act
        CategoryDTO result = service.update(existingId, updatedDTO);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals("Descrição de IPA", result.getDescription()); 
        verify(repository, times(1)).findById(existingId);
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("update deve lançar ResourceNotFoundException quando ID não existe")
    void update_shouldThrowResourceNotFoundException_whenIdDoesNotExist() {

        CategoryDTO dummyDTO = new CategoryDTO(nonExistingId, "Inexistente", "Descrição");

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, dummyDTO);
        });
        verify(repository, times(1)).findById(nonExistingId);
        verify(repository, never()).save(any());
    }


    @Test
    @DisplayName("delete deve ser bem-sucedido quando ID existe e não é dependente")
    void delete_shouldDoNothing_whenIdExists() {
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        verify(repository, times(1)).existsById(existingId);
        verify(repository, times(1)).deleteById(existingId);
    }

    @Test
    @DisplayName("delete deve lançar ResourceNotFoundException quando ID não existe")
    void delete_shouldThrowResourceNotFoundException_whenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });

        verify(repository, times(1)).existsById(nonExistingId);
        verify(repository, never()).deleteById(nonExistingId);
    }

    @Test
    @DisplayName("delete deve lançar DatabaseException quando há falha de integridade referencial")
    void delete_shouldThrowDatabaseException_whenIntegrityViolation() {
        when(repository.existsById(dependentId)).thenReturn(true);
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });

        verify(repository, times(1)).existsById(dependentId);
        verify(repository, times(1)).deleteById(dependentId);
    }
}