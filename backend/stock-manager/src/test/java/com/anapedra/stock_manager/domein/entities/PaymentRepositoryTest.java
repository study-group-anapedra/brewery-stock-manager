package com.anapedra.stock_manager.domein.entities;

import com.anapedra.stock_manager.domain.entities.Order;
import com.anapedra.stock_manager.domain.entities.Payment;
import com.anapedra.stock_manager.domain.entities.User;
import com.anapedra.stock_manager.domain.enums.OrderStatus;
import com.anapedra.stock_manager.repositories.PaymentRepository;
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
public class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository; // Você precisará criar esta interface

    @Autowired
    private TestEntityManager entityManager;

    private Long existingPaymentId;
    private Long existingOrderId;

    @BeforeEach
    void setUp() {
        // 1. Configuração de Entidades Base
        User client = new User(null, "Pagador", "payer@email.com", "99999", LocalDate.now(), 
            "123", Instant.now(), Instant.now(), "12345678900");
        client = entityManager.persist(client);
        
        Order order = new Order(null, Instant.now(), client);
        order.setOrderStatus(OrderStatus.PAID); 
        order = entityManager.persist(order);
        existingOrderId = order.getId();

        // 2. Criação do Payment
        Payment payment = new Payment(null, Instant.now(), order);
        payment = entityManager.persist(payment); 
        existingPaymentId = payment.getId();


        order.setPayment(payment);
        entityManager.merge(order); 

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void findById_shouldLoadPaymentAndAssociatedOrder() {
        Optional<Payment> result = paymentRepository.findById(existingPaymentId);

        Assertions.assertTrue(result.isPresent(), "O Payment deve ser encontrado.");
        Payment payment = result.get();
        
        Assertions.assertNotNull(payment.getOrder(), "O Payment deve ter um Order associado.");
        Assertions.assertEquals(existingOrderId, payment.getOrder().getId());
        
        Assertions.assertEquals(OrderStatus.PAID, payment.getOrder().getOrderStatus());
    }

    // Localizado em com.anapedra.stock_manager.domein.entities.PaymentRepositoryTest

    @Test
    void save_shouldPersistNewPayment() {

        User client = entityManager.find(User.class, entityManager.find(Order.class, existingOrderId).getClient().getId());

        Order newOrder = new Order(null, Instant.now().plusSeconds(100), client);
        newOrder.setOrderStatus(OrderStatus.WAITING_PAYMENT);
        newOrder = entityManager.persist(newOrder);

        Instant newMoment = Instant.now().plusSeconds(3600);

        Payment newPayment = new Payment(null, newMoment, newOrder);

        newPayment = paymentRepository.save(newPayment);

        Assertions.assertNotNull(newPayment.getId(), "O novo Payment deve ter um ID gerado.");
        Assertions.assertNotNull(newPayment.getOrder(), "O novo Payment deve estar associado a uma Order.");
        Assertions.assertEquals(newOrder.getId(), newPayment.getOrder().getId(), "O Payment deve estar ligado à nova Order.");
    }
}