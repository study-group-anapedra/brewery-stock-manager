package com.anapedra.stock_manager.domein.entities;

import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.BeerRestocking;
import com.anapedra.stock_manager.domain.entities.Category;

import com.anapedra.stock_manager.repositories.BeerRestockingRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.time.LocalDate;


@DataJpaTest
public class BeerRestockingRepositoryTest {

    @Autowired
    private BeerRestockingRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Long existingId;
    private Long nonExistingId;
    private Beer existingBeer;
    

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 100L;

        // 1. Criar e persistir as dependências necessárias (Category)
        Category category = new Category(null, "Pilsen",null);
        entityManager.persist(category);


        existingBeer = new Beer(
                null,
                "Antarctica",
                "",
                4.5,
                10.0,
                LocalDate.now().plusYears(1),
                LocalDate.now()
        );

        // ADICIONAR CATEGORIA (necessário para persistência M-to-M)
        existingBeer.getCategories().add(category);

        // 3. Persistir a Beer (NÃO PRECISA MAIS DE STOCK, POIS O CAMPO ESTÁ COMENTADO)
        existingBeer = entityManager.persist(existingBeer);

        // 4. Persistir a entidade BeerRestocking
        BeerRestocking restocking = new BeerRestocking(null, 10, Instant.now(), existingBeer);
        entityManager.persist(restocking);

        entityManager.flush();
    }


    
    @Test
    public void save_shouldPersistWithAutoIncrementId_whenIdIsNull() {
        // Arrange
        BeerRestocking newRestocking = new BeerRestocking(null, 25, Instant.now(), existingBeer);

        // Act
        BeerRestocking savedRestocking = repository.save(newRestocking);

        // Assert
        Assertions.assertNotNull(savedRestocking.getId());
        Assertions.assertTrue(savedRestocking.getId() > 0);
        Assertions.assertEquals(25, savedRestocking.getQuantity());
        Assertions.assertEquals(existingBeer.getName(), savedRestocking.getBeer().getName());
    }
    
    // ... (restante dos testes)
}