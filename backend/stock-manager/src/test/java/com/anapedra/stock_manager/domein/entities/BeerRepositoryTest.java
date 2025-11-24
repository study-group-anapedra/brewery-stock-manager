package com.anapedra.stock_manager.domein.entities;

import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.repositories.BeerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import java.time.LocalDate;
import java.util.Optional;

@DataJpaTest
public class BeerRepositoryTest {

    @Autowired
    private BeerRepository repository;

    @Autowired
    private TestEntityManager entityManager; // Ajuda a manipular dados no H2

    private Long existingId;
    private Long nonExistingId;
    private int countTotalBeers;

    @BeforeEach
    void setUp() throws Exception {
        // IDs para testes
        nonExistingId = 1000L;
        
        // 1. Persiste uma entidade para garantir que o banco tenha dados iniciais
        Beer beerSeed = new Beer(
            null, 
            "Pilsen Lager Test", 
            "http://img.com/pilsen.jpg", 
            4.8, 
            6.50, 
            LocalDate.of(2025, 3, 1), 
            LocalDate.of(2026, 3, 1)
        );
        
        // Persiste e captura o ID gerado (que será o existingId)
        entityManager.persist(beerSeed);
        existingId = beerSeed.getId(); 
        
        // 2. Conta o total de entidades após a persistência
        countTotalBeers = (int) repository.count();
    }

    // ---------------------- TESTES DE LEITURA (Find) ----------------------
    
    @Test
    void findById_shouldReturnBeer_whenIdExists() {
        Optional<Beer> result = repository.findById(existingId);
        // Garante que a cerveja foi encontrada
        Assertions.assertTrue(result.isPresent()); 
        Assertions.assertEquals("Pilsen Lager Test", result.get().getName());
    }

    @Test
    void findById_shouldReturnEmpty_whenIdDoesNotExist() {
        Optional<Beer> result = repository.findById(nonExistingId);
        // Garante que a busca por ID inexistente retorna vazio
        Assertions.assertFalse(result.isPresent());
    }

    // ---------------------- TESTES DE CRIAÇÃO/ATUALIZAÇÃO (Save) ----------------------
    
    @Test
    void save_shouldPersistWithAutoincrement_whenIdIsNull() {
        Beer newBeer = new Beer(
            null, 
            "New Test Beer for Save", 
            "http://img.com/new.jpg", 
            4.5, 
            12.00, 
            LocalDate.of(2025, 5, 1), 
            LocalDate.of(2025, 11, 1)
        );
        
        newBeer = repository.save(newBeer);

        // Verifica se o ID foi gerado e a contagem aumentou
        Assertions.assertNotNull(newBeer.getId());
        Assertions.assertEquals(countTotalBeers + 1, repository.count());
    }
    
    // ---------------------- TESTES DE DELEÇÃO (Delete) ----------------------
    
    @Test
    void delete_shouldDeleteObject_whenIdExists() {
        repository.deleteById(existingId);
        
        // Tenta buscar o objeto deletado
        Optional<Beer> result = repository.findById(existingId);
        
        // Garante que não foi encontrado e que a contagem diminuiu
        Assertions.assertFalse(result.isPresent());
        Assertions.assertEquals(countTotalBeers - 1, repository.count());
    }


    // ---------------------- TESTE DE CONTADOR (Count) ----------------------
    
    @Test
    void findAll_shouldReturnCorrectCount() {
        long count = repository.count();
        Assertions.assertEquals(countTotalBeers, count);
    }
}