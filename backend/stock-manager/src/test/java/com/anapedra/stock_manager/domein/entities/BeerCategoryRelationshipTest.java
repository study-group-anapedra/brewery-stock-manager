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
import org.springframework.transaction.annotation.Transactional; // Adicionado para garantir o contexto

import java.time.LocalDate;
import java.util.Optional;

@DataJpaTest
public class BeerCategoryRelationshipTest { 

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
        // **USANDO OS MÉTODOS HELPER DA ENTIDADE (RECOMENDADO)**
        // Se você não quiser depender dos helpers, pode usar:
        // beer.getCategories().add(category1); 
        // category1.getBeers().add(beer); // Adicionar o inverso manualmente é crucial se não usar helper
        
        // Vamos usar o método helper `addCategory` na Beer (se estiver descomentado),
        // ou garantir que o relacionamento seja bidirecional na memória:
        
        beer.getCategories().add(category1); 
        beer.getCategories().add(category2);
        
        // **CRUCIAL:** O lado INVERSO (Category) precisa saber do relacionamento na memória.
        // Se a Entidade Category está configurada corretamente, adicionar no lado Beer
        // não é suficiente. Vamos garantir a bidirecionalidade aqui:
        category1.getBeers().add(beer);
        category2.getBeers().add(beer);


        // 4. Persistir a Cerveja (Beer é o lado PROPRIETÁRIO e é EAGER)
        beer = entityManager.persist(beer);
        beerId = beer.getId();

        entityManager.flush(); // Força a escrita no H2 antes dos testes
        entityManager.clear(); // Limpa o cache (1st level cache) para garantir que a busca venha do DB
    }

    // ----------------------------------------------------
    // TESTE 1: Lado Proprietário (Beer -> Categories)
    // ----------------------------------------------------

    @Test
    void whenFetchingBeer_shouldLoadAllCategories() {
        // Busca a cerveja pelo ID (deve ser EAGER, carregando as Categorias)
        Optional<Beer> result = beerRepository.findById(beerId);
        
        Assertions.assertTrue(result.isPresent(), "A cerveja deve ser encontrada.");
        Beer beer = result.get();
        
        // 1. Verifica se a cerveja tem 2 categorias
        Assertions.assertEquals(2, beer.getCategories().size(), 
            "A cerveja deve ter 2 categorias associadas.");
        
        // 2. Verifica se as categorias corretas estão carregadas
        boolean containsLager = beer.getCategories().stream().anyMatch(c -> c.getName().equals("Lager"));
        Assertions.assertTrue(containsLager, "Deve conter a categoria Lager.");
    }
    
    // ----------------------------------------------------
    // TESTE 2: Lado Inverso (Category -> Beers)
    // ----------------------------------------------------
    
    // O @DataJpaTest já roda dentro de uma transação.
    // O fetch no lado da Category é LAZY. A transação do teste deve permitir o acesso.
    @Test
    void whenFetchingCategory_shouldLoadAssociatedBeers() {
        // Busca a Categoria 1
        Optional<Category> result = categoryRepository.findById(categoryId1);

        Assertions.assertTrue(result.isPresent(), "A categoria deve ser encontrada.");
        Category category = result.get();

        // **CRUCIAL**: O acesso a getBeers() (que é LAZY) deve ser feito dentro da transação do teste.
        // O TestEntityManager já faz isso. Se falhar, adicione @Transactional na classe ou no método.
        
        // 1. Verifica se a categoria está associada a 1 cerveja
        Assertions.assertEquals(1, category.getBeers().size(), 
            "A categoria deve ter 1 cerveja associada.");
        
        // 2. Verifica o nome da cerveja associada
        Beer associatedBeer = category.getBeers().iterator().next();
        Assertions.assertEquals("Minha Cerveja M2M", associatedBeer.getName(),
            "O nome da cerveja associada deve corresponder.");
    }

}