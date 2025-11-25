package com.anapedra.stock_manager.domein.entities;

import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.Category;
import com.anapedra.stock_manager.domain.entities.StockLoss;
import com.anapedra.stock_manager.domain.enums.LossReason;
import com.anapedra.stock_manager.repositories.StockLossRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.Instant;
import java.util.Optional;

@DataJpaTest
public class StockLossRepositoryTest {

    @Autowired
    private StockLossRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Long existingId;
    private Long nonExistingId;
    private Beer beer1;
    private Beer beer2;
    private Category categoryPilsen;

    private final LocalDate PAST_DATE = LocalDate.now().minusDays(10);
    private final LocalDate CURRENT_DATE = LocalDate.now();

    // --- Configuração Inicial ---

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 100L;
        
        // 1. Criar e persistir Categorias
        categoryPilsen = new Category(null, "Pilsen",null);
        Category categoryIPA = new Category(null, "IPA",null);
        entityManager.persist(categoryPilsen);
        entityManager.persist(categoryIPA);

        // 2. Criar e persistir Cervejas
        // Assumindo construtor de Beer: (id, name, urlImg, alcoholContent, price, manufactureDate, expirationDate)
        beer1 = new Beer(null, "Antarctica", "", 4.5, 10.0, LocalDate.now().minusYears(1), LocalDate.now().plusYears(1));
        beer1.getCategories().add(categoryPilsen);
        beer1 = entityManager.persist(beer1);

        beer2 = new Beer(null, "Colorado Appia", "", 5.5, 15.0, LocalDate.now().minusYears(1), LocalDate.now().plusYears(1));
        beer2.getCategories().add(categoryIPA);
        beer2 = entityManager.persist(beer2);
        
        // 3. Persistir Registros de Perda (StockLoss)
        
        // Loss 1 (ID 1) - Motivo: DAMAGED, Cerveja: beer1, Data: CURRENT_DATE
        StockLoss loss1 = new StockLoss(null, beer1, 5, LossReason.DAMAGED, CURRENT_DATE, "Quebra na paletização.");
        entityManager.persist(loss1);
        
        // Loss 2 (ID 2) - Motivo: EXPIRED, Cerveja: beer2, Data: PAST_DATE
        StockLoss loss2 = new StockLoss(null, beer2, 10, LossReason.EXPIRED, PAST_DATE, "Lote vencido.");
        entityManager.persist(loss2);
        
        // Loss 3 (ID 3) - Motivo: DAMAGED, Cerveja: beer1, Data: PAST_DATE
        StockLoss loss3 = new StockLoss(null, beer1, 2, LossReason.DAMAGED, PAST_DATE, "Pequena avaria.");
        entityManager.persist(loss3);

        entityManager.flush(); 
    }

    // --- Testes de Persistência Básica ---

    @Test
    public void save_shouldPersistWithAutoIncrementId_whenIdIsNull() {
        // Arrange
        StockLoss newLoss = new StockLoss(null, beer2, 1, LossReason.OTHER, CURRENT_DATE, "Motivo indefinido.");

        // Act
        StockLoss savedLoss = repository.save(newLoss);

        // Assert
        Assertions.assertNotNull(savedLoss.getId());
        Assertions.assertTrue(savedLoss.getId() > existingId);
        Assertions.assertEquals(LossReason.OTHER, savedLoss.getLossReason());
    }



    @Test
    public void deleteById_shouldDeleteObject_whenIdExists() {
        // Act
        repository.deleteById(existingId); // Loss 1

        // Assert
        Optional<StockLoss> result = repository.findById(existingId);
        Assertions.assertFalse(result.isPresent());
    }




    private final Pageable pageable = PageRequest.of(0, 10);

    @Test
    public void findLossesByFilters_shouldReturnAllLosses_whenAllFiltersAreNull() {
        // Act
        Page<StockLoss> result = repository.findLossesByFilters(
            null, null, null, null, null, null, pageable
        );

        // Assert
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(3, result.getTotalElements());
    }

    @Test
    public void findLossesByFilters_shouldFilterByReasonCode() {
        // Act: Filtrar por DAMAGED (Code 1)
        Page<StockLoss> result = repository.findLossesByFilters(
            LossReason.DAMAGED.getCode(), null, null, null, null, null, pageable
        );

        // Assert: Espera Loss 1 e Loss 3
        Assertions.assertEquals(2, result.getTotalElements());
        Assertions.assertTrue(result.getContent().stream().allMatch(sl -> sl.getLossReason() == LossReason.DAMAGED));
    }

    @Test
    public void findLossesByFilters_shouldFilterByBeerId() {
        // Act: Filtrar por beer2
        Page<StockLoss> result = repository.findLossesByFilters(
            null, beer2.getId(), null, null, null, null, pageable
        );

        // Assert: Espera Loss 2
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(beer2.getName(), result.getContent().get(0).getBeer().getName());
    }

    @Test
    public void findLossesByFilters_shouldFilterByBeerNameIgnoringCaseAndTrim() {
        // Act: Buscar por "antartica" (case-insensitive, parcial)
        Page<StockLoss> result = repository.findLossesByFilters(
            null, null, "TARCTI", null, null, null, pageable
        );

        // Assert: Espera Loss 1 e Loss 3 (ambas Antarctica)
        Assertions.assertEquals(2, result.getTotalElements());
        Assertions.assertTrue(result.getContent().stream().allMatch(sl -> sl.getBeer().getName().equals("Antarctica")));
    }



    @Test
    public void findLossesByFilters_shouldFilterByStartDate() {
        // Act: A partir da data atual (CURRENT_DATE) - Espera Loss 1
        Page<StockLoss> result = repository.findLossesByFilters(
            null, null, null, null, CURRENT_DATE, null, pageable
        );

        // Assert: Espera Loss 1
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertTrue(result.getContent().stream().allMatch(sl -> sl.getLossDate().isEqual(CURRENT_DATE)));
    }

    @Test
    public void findLossesByFilters_shouldFilterByEndDate() {
        // Act: Até a data passada (PAST_DATE) - Espera Loss 2 e Loss 3
        Page<StockLoss> result = repository.findLossesByFilters(
            null, null, null, null, null, PAST_DATE, pageable
        );

        // Assert: Espera Loss 2 e Loss 3
        Assertions.assertEquals(2, result.getTotalElements());
        Assertions.assertTrue(result.getContent().stream().allMatch(sl -> sl.getLossDate().isBefore(CURRENT_DATE) || sl.getLossDate().isEqual(PAST_DATE)));
    }

    @Test
    public void findLossesByFilters_shouldFilterByCombinedFilters() {
        // Act: Filtrar por DAMAGED (Code 1) e data até PAST_DATE. Espera Loss 3.
        Page<StockLoss> result = repository.findLossesByFilters(
            LossReason.DAMAGED.getCode(), null, null, null, null, PAST_DATE, pageable
        );

        // Assert: Espera Loss 3
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertTrue(result.getContent().stream().allMatch(sl -> sl.getLossReason() == LossReason.DAMAGED && sl.getLossDate().isEqual(PAST_DATE)));
    }
}