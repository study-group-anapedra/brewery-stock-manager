package com.anapedra.stock_manager.domein.entities;

import com.anapedra.stock_manager.domain.entities.Category;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CategoryUnitTest {

    private static final Long DEFAULT_ID = 1L;
    private static final String DEFAULT_NAME = "Lagers";
    private static final String DEFAULT_DESC = "Cervejas de baixa fermentação.";
    private static final String UPDATED_NAME = "Pilsen";

    /**
     * Testa o construtor padrão (sem argumentos).
     */
    @Test
    void testDefaultConstructor() {
        Category category = new Category();
        
        assertNull(category.getId());
        assertNull(category.getName());
        assertNull(category.getDescription());
    }

    /**
     * Testa o construtor de conveniência (todos os campos).
     */
    @Test
    void testConvenienceConstructor() {
        Category category = new Category(DEFAULT_ID, DEFAULT_NAME, DEFAULT_DESC);
        
        assertEquals(DEFAULT_ID, category.getId());
        assertEquals(DEFAULT_NAME, category.getName());
        assertEquals(DEFAULT_DESC, category.getDescription());
    }

    /**
     * Testa os métodos Getters e Setters (mutabilidade).
     */
    @Test
    void testGettersAndSetters() {
        Category category = new Category();
        
        // Setters
        category.setId(DEFAULT_ID);
        category.setName(UPDATED_NAME);
        category.setDescription(DEFAULT_DESC);
        
        // Getters
        assertEquals(DEFAULT_ID, category.getId());
        assertEquals(UPDATED_NAME, category.getName());
        assertEquals(DEFAULT_DESC, category.getDescription());
    }
    
    /**
     * Testa a implementação de equals() e hashCode() baseada apenas no ID.
     */
    @Test
    void testEqualsAndHashCode() {
        // Categoria 1 (ID 1)
        Category cat1 = new Category(1L, "Lager", "Desc");
        // Categoria 2 (ID 1, mas dados diferentes) -> Deve ser igual a cat1
        Category cat2 = new Category(1L, "Ale", "Outra Desc");
        // Categoria 3 (ID 2) -> Deve ser diferente de cat1
        Category cat3 = new Category(2L, "Lager", "Desc");
        // Categoria 4 (Sem ID) -> Não pode ser igual a nenhuma com ID
        Category cat4 = new Category(null, "Lager", "Desc");
        
        // 1. Igualdade pelo mesmo ID
        assertEquals(cat1, cat2, "Categorias com o mesmo ID devem ser iguais.");
        assertEquals(cat1.hashCode(), cat2.hashCode(), "HashCodes devem ser iguais para objetos iguais.");
        
        // 2. Desigualdade por ID diferente
        assertNotEquals(cat1, cat3, "Categorias com IDs diferentes devem ser desiguais.");
        
        // 3. Desigualdade com objeto sem ID (JPA Safety)
        assertNotEquals(cat1, cat4, "Objeto sem ID não pode ser igual a objeto com ID.");
        
        // 4. Teste de Reflexividade e Nulo
        assertEquals(cat1, cat1);
        assertNotEquals(cat1, null);
        assertNotEquals(cat1, new Object());
    }
}