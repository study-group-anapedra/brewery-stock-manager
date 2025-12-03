package com.anapedra.stock_manager.domein.entities;

import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.Order;
import com.anapedra.stock_manager.domain.entities.OrderItem;
import com.anapedra.stock_manager.domain.entities.User;
import com.anapedra.stock_manager.domain.enums.OrderStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;


public class OrderTest {

    private Order order;
    private Beer beer1;
    private Beer beer2;
    private OrderItem item1;
    private OrderItem item2;

    @BeforeEach
    void setUp() {
        beer1 = Mockito.mock(Beer.class);
        Mockito.when(beer1.getPrice()).thenReturn(10.00);

        beer2 = Mockito.mock(Beer.class);
        Mockito.when(beer2.getPrice()).thenReturn(5.50);
        item1 = new OrderItem(null, beer1, 3, 10.00);
        item2 = new OrderItem(null, beer2, 2, 5.50);

        order = new Order(Instant.now(), new User(), OrderStatus.WAITING_PAYMENT);
        order.getItems().add(item1);
        order.getItems().add(item2);
    }

    @Test
    void getOrderStatus_shouldReturnWaitingPaymentOnCreation() {
        Assertions.assertEquals(OrderStatus.WAITING_PAYMENT, order.getOrderStatus());
    }

    @Test
    void getQuantityProduct_shouldReturnTotalQuantity() {
        int totalQuantity = order.getQuantityProduct();
        Assertions.assertEquals(5, totalQuantity, "A quantidade total deve ser 5.");
    }

    @Test
    void getTotal_shouldCalculateCorrectTotalValue() {
        double total = order.getTotal();
        Assertions.assertEquals(41.00, total, 0.001, "O valor total deve ser 41.00.");
    }

    @Test
    void getTotalToPay_shouldReturnSameTotalValue() {
        double totalToPay = order.getTotalToPay();
        Assertions.assertEquals(41.00, totalToPay, 0.001, "O valor a pagar deve ser igual ao total.");
    }

    @Test
    void setOrderStatus_shouldChangeStatusCorrectly() {
        order.setOrderStatus(OrderStatus.WAITING_PAYMENT);
        Assertions.assertEquals(OrderStatus.WAITING_PAYMENT, order.getOrderStatus(),
                "O objeto Enum retornado deve ser WAITING_PAYMENT.");

        Assertions.assertEquals(1, order.getOrderStatus().getCode(),
                "O código interno do status deve ser 1.");
    }
}