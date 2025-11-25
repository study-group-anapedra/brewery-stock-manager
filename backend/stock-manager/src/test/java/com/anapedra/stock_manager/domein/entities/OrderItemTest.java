package com.anapedra.stock_manager.domein.entities;

import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.Order;
import com.anapedra.stock_manager.domain.entities.OrderItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class OrderItemTest {

    @Test
    void getSubTotal_shouldCalculateCorrectlyBasedOnBeerPrice() {

        Beer mockBeer = Mockito.mock(Beer.class);
        Mockito.when(mockBeer.getPrice()).thenReturn(10.00); // Preço da cerveja: 10.00
        OrderItem item = new OrderItem(null, mockBeer, 5, 10.00);
        double subTotal = item.getSubTotal();
        Assertions.assertEquals(50.00, subTotal, 0.001);
    }

    @Test
    void getOrderAndBeer_shouldReturnCorrectEntities() {
        Order mockOrder = Mockito.mock(Order.class);
        Beer mockBeer = Mockito.mock(Beer.class);
        
        OrderItem item = new OrderItem(mockOrder, mockBeer, 1, 10.00);
        
        Assertions.assertEquals(mockOrder, item.getOrder());
        Assertions.assertEquals(mockBeer, item.getBeer());
    }

    @Test
    void getSubTotal_shouldReturnZeroIfQuantityIsZero() {
        Beer mockBeer = Mockito.mock(Beer.class);
        Mockito.when(mockBeer.getPrice()).thenReturn(10.00);
        
        OrderItem item = new OrderItem(null, mockBeer, 0, 10.00); 
        
        double subTotal = item.getSubTotal();
        
        Assertions.assertEquals(0.0, subTotal, 0.001);
    }
}