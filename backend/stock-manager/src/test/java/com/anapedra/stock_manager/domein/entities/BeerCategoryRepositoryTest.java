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

import java.time.LocalDate;
import java.util.Optional;

@DataJpaTest
public class BeerCategoryRepositoryTest {

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager entityManager; // Para garantir a ordem das persistências

    private Long beerId;
    private Long categoryId1;
    private Long categoryId2;

    @BeforeEach
    void setUp() throws Exception {
        // Limpar o contexto do entityManager para garantir o estado limpo
        entityManager.clear();
        
        // 1. Criar e Persistir Categorias
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
        // Usamos o método helper da Entidade Beer
        beer.getCategories().add(category1); 
        beer.getCategories().add(category2);

        // 4. Persistir a Cerveja (A Beer é o lado PROPRIETÁRIO, ela salvará a tabela de junção)
        beer = entityManager.persist(beer);
        beerId = beer.getId();

        entityManager.flush(); // Força o JPA a executar o SQL antes dos testes
    }

    // ----------------------------------------------------
    // TESTES DO RELACIONAMENTO MANY-TO-MANY
    // ----------------------------------------------------

    @Test
    void whenFetchingBeer_shouldLoadAllCategories() {
        // Busca a cerveja pelo ID
        Optional<Beer> result = beerRepository.findById(beerId);
        
        Assertions.assertTrue(result.isPresent());
        Beer beer = result.get();
        
        // 1. Verifica se a cerveja tem 2 categorias
        Assertions.assertEquals(2, beer.getCategories().size());
        
        // 2. Verifica se as categorias corretas estão carregadas
        boolean containsLager = beer.getCategories().stream().anyMatch(c -> c.getName().equals("Lager"));
        boolean containsIPA = beer.getCategories().stream().anyMatch(c -> c.getName().equals("IPA"));
        
        Assertions.assertTrue(containsLager);
        Assertions.assertTrue(containsIPA);
    }
    
    @Test
    void whenFetchingCategory_shouldLoadAssociatedBeers() {
        // Busca a Categoria 1
        Optional<Category> result = categoryRepository.findById(categoryId1);

        Assertions.assertTrue(result.isPresent());
        Category category = result.get();

        // 1. Verifica se a categoria está associada a 1 cerveja (a que criamos no setUp)
        Assertions.assertEquals(1, category.getBeers().size());
        
        // 2. Verifica o nome da cerveja associada
        Beer associatedBeer = category.getBeers().iterator().next();
        Assertions.assertEquals("Minha Cerveja M2M", associatedBeer.getName());
    }
    
    @Test
    void whenDeletingBeer_shouldClearJunctionTableButKeepCategory() {
        // Deleta a cerveja (como Beer é o PROPRIETÁRIO, isso deve limpar as referências na tabela de junção)
        beerRepository.deleteById(beerId);
        entityManager.flush();
        
        // 1. Verifica se a Cerveja foi realmente deletada
        Assertions.assertFalse(beerRepository.findById(beerId).isPresent());
        
        // 2. Verifica se a Categoria ainda existe (Não deve ser deletada, pois não há CascadeType.REMOVE do lado do Category)
        Assertions.assertTrue(categoryRepository.findById(categoryId1).isPresent());
        Assertions.assertTrue(categoryRepository.findById(categoryId2).isPresent());
        
        // 3. Garante que a Categoria não aponta mais para a cerveja deletada (Apesar de ser Lazy, o flush deve garantir o estado)
        Optional<Category> categoryResult = categoryRepository.findById(categoryId1);
        Assertions.assertTrue(categoryResult.isPresent());
        
        // Re-buscamos para garantir que o Set<Beer> está vazio após o delete do Beer
        Category categoryAfterDelete = categoryResult.get();
        // O set<Beer> deve estar vazio ou não carregar o objeto deletado
        Assertions.assertEquals(0, categoryAfterDelete.getBeers().size(), "A lista de cervejas da categoria deveria estar vazia após deletar a cerveja associada.");

    }

}