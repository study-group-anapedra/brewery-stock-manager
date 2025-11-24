package com.anapedra.stock_manager.domein.entities;

import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.Category;
import com.anapedra.stock_manager.repositories.BeerRepository;
import com.anapedra.stock_manager.repositories.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional; // Import necessário

import java.time.LocalDate;
import java.util.Optional;

@DataJpaTest
public class BeerCategoryRepositoryTest {

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Long beerId;
    private Long categoryId1;
    private Long categoryId2;

    @BeforeEach
    void setUp() throws Exception {
        entityManager.clear();

        // 1. Criar e Persistir Categorias (Entidades gerenciadas)
        Category category1 = new Category(null, "Lager", "Cervejas leves e refrescantes");
        Category category2 = new Category(null, "IPA", "Cervejas lupuladas e amargas");

        category1 = entityManager.persist(category1);
        category2 = entityManager.persist(category2);

        categoryId1 = category1.getId();
        categoryId2 = category2.getId();

        // 2. Criar a Cerveja
        Beer beer = new Beer(
            null,
            "Minha Cerveja M2M",
            "http://img.com/beer_m2m.jpg",
            5.0,
            8.00,
            LocalDate.of(2025, 6, 1),
            LocalDate.of(2026, 6, 1)
        );

        // 3. Estabelecer o relacionamento Many-to-Many
        // Beer (Proprietário) -> Category
        beer.getCategories().add(category1);
        beer.getCategories().add(category2);

        // **CORREÇÃO CRUCIAL:** Estabelecer a bidirecionalidade na memória para o lado INVERSO (Category)
        // Isso é necessário porque o Category é LAZY e o teste depende que essa associação seja conhecida.
        // Se a Entidade Category tiver um método helper (addBeer), use-o. Senão, adicione diretamente:
        category1.getBeers().add(beer); 
        category2.getBeers().add(beer); 


        // 4. Persistir a Cerveja (A Beer é o lado PROPRIETÁRIO, ela salvará a tabela de junção)
        beer = entityManager.persist(beer);
        beerId = beer.getId();

        entityManager.flush(); // Força a escrita no banco de dados
        entityManager.clear(); // Limpa o cache para garantir que as buscas venham do H2
    }

    // ----------------------------------------------------
    // TESTE 1: Lado Proprietário (EAGER)
    // ----------------------------------------------------

    @Test
    void whenFetchingBeer_shouldLoadAllCategories() {
        // Busca a cerveja (FetchType.EAGER em Beer)
        Optional<Beer> result = beerRepository.findById(beerId);
        
        Assertions.assertTrue(result.isPresent());
        Beer beer = result.get();
        
        // Verifica se a cerveja tem 2 categorias
        Assertions.assertEquals(2, beer.getCategories().size());
        
        // Verifica se as categorias corretas estão carregadas
        boolean containsLager = beer.getCategories().stream().anyMatch(c -> c.getName().equals("Lager"));
        Assertions.assertTrue(containsLager);
    }
    
    // ----------------------------------------------------
    // TESTE 2: Lado Inverso (LAZY)
    // ----------------------------------------------------

    @Test
    @Transactional // CRUCIAL para evitar LazyInitializationException ao acessar category.getBeers().size()
    void whenFetchingCategory_shouldLoadAssociatedBeers() {
        // Busca a Categoria 1
        Optional<Category> result = categoryRepository.findById(categoryId1);

        Assertions.assertTrue(result.isPresent());
        Category category = result.get();

        // 1. Verifica se a categoria está associada a 1 cerveja
        // Este acesso força o carregamento LAZY dentro do contexto transacional.
        Assertions.assertEquals(1, category.getBeers().size());
        
        // 2. Verifica o nome da cerveja associada
        Beer associatedBeer = category.getBeers().iterator().next();
        Assertions.assertEquals("Minha Cerveja M2M", associatedBeer.getName());
    }
    
    // O teste 'whenDeletingBeer_shouldClearJunctionTableButKeepCategory' foi removido conforme solicitado.
}