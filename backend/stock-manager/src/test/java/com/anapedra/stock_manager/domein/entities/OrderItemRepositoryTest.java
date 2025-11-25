package com.anapedra.stock_manager.domein.entities;

import com.anapedra.stock_manager.domain.entities.*;
import com.anapedra.stock_manager.domain.enums.OrderStatus;
import com.anapedra.stock_manager.domain.pks.OrderItemPK;
import com.anapedra.stock_manager.repositories.OrderItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

@DataJpaTest
public class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private TestEntityManager entityManager;

    private OrderItemPK existingItemPK;

    @BeforeEach
    void setUp() {
        // 1. Configuração de Entidades Base
        User client = new User(null, "Client Teste", "client@email.com", "99999", LocalDate.now(), 
            "123", Instant.now(), Instant.now(), "12345678900");
        client = entityManager.persist(client);
        
        // Crie uma Beer fictícia para o teste (preço 15.00)
        Beer beer = new Beer(null, "Cerveja Teste Item", "url", 5.0, 15.00, LocalDate.now(), LocalDate.now().plusYears(1));
        beer = entityManager.persist(beer);
        
        Order order = new Order(null, Instant.now(), client);
        order.setOrderStatus(OrderStatus.WAITING_PAYMENT); 
        order = entityManager.persist(order);

        // 2. Criação do OrderItem com a Chave Composta
        OrderItem item = new OrderItem(order, beer, 5, beer.getPrice());
        item = entityManager.persist(item);
        
        // CORREÇÃO APLICADA: Chama o método getId() da entidade OrderItem
        existingItemPK = item.getId();

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void findById_shouldReturnOrderItemByCompositeKey() {
        // Act
        Optional<OrderItem> result = orderItemRepository.findById(existingItemPK);

        // Assert
        Assertions.assertTrue(result.isPresent(), "O OrderItem deve ser encontrado pela chave composta.");
        OrderItem item = result.get();
        
        // Verifica dados e relacionamento
        Assertions.assertEquals(5, item.getQuantity(), "A quantidade deve ser 5.");
        Assertions.assertEquals(15.00, item.getPrice(), 0.001, "O preço unitário deve ser 15.00.");
        Assertions.assertNotNull(item.getOrder(), "O OrderItem deve ter um Order associado.");
        Assertions.assertNotNull(item.getBeer(), "O OrderItem deve ter uma Beer associada.");
        // Cálculo: 5 * 15.00 = 75.00
        Assertions.assertEquals(75.00, item.getSubTotal(), 0.001, "O SubTotal deve ser 75.00.");
    }
    
    @Test
    void delete_shouldRemoveItemByCompositeKey() {
        // Arrange: Garante que existe antes de deletar
        Assertions.assertTrue(orderItemRepository.findById(existingItemPK).isPresent());
        
        // Act
        orderItemRepository.deleteById(existingItemPK);
        entityManager.flush();
        
        // Assert
        Optional<OrderItem> result = orderItemRepository.findById(existingItemPK);
        Assertions.assertFalse(result.isPresent(), "O OrderItem deve ser deletado após a exclusão pela chave composta.");
    }
}