package com.anapedra.stock_manager.services;

import com.anapedra.stock_manager.domain.dtos.OrderDTO;
import com.anapedra.stock_manager.domain.dtos.OrderItemDTO;
import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.Order;
import com.anapedra.stock_manager.domain.entities.Stock;
import com.anapedra.stock_manager.domain.entities.User;
import com.anapedra.stock_manager.domain.enums.OrderStatus;
import com.anapedra.stock_manager.repositories.BeerRepository;
import com.anapedra.stock_manager.repositories.OrderRepository;
import com.anapedra.stock_manager.repositories.UserRepository;
import com.anapedra.stock_manager.services.exceptions.ForbiddenException;
import com.anapedra.stock_manager.services.exceptions.InsufficientStockException;
import com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException;
import com.anapedra.stock_manager.services.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    private AuthService authService;
    private UserService userService;
    private OrderRepository orderRepository;
    private BeerRepository beerRepository;
    private UserRepository userRepository;

    private OrderServiceImpl service;

    private User user;
    private Beer beer;
    private Stock stock;

    @BeforeEach
    void setup() {

        authService = mock(AuthService.class);
        userService = mock(UserService.class);
        orderRepository = mock(OrderRepository.class);
        beerRepository = mock(BeerRepository.class);
        userRepository = mock(UserRepository.class);

        service = new OrderServiceImpl(
                authService, userService, orderRepository,
                beerRepository, userRepository
        );

        user = new User();
        user.setId(1L);
        user.setName("Ana");

        stock = new Stock();
        stock.setQuantity(10);

        beer = new Beer();
        beer.setId(100L);
        beer.setName("Lager");
        beer.setStock(stock);
    }



    @Test
    void save_ShouldThrowForbidden_WhenUserNotAuthenticated() {
        when(userService.authenticated()).thenReturn(null);
        assertThrows(ForbiddenException.class, () -> service.save(new OrderDTO()));
    }



    @Test
    void findById_ShouldThrow_WhenOrderNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }


    @Test
    void update_ShouldThrow_WhenOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, new OrderDTO()));
    }

    // =============================================================
    // DELETE
    // =============================================================

    @Test
    void delete_ShouldRemoveOrder_WhenUserAuthorized() {
        Order order = new Order();
        order.setId(1L);
        order.setClient(user);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        service.delete(1L);

        verify(authService).validateSelfOrAdmin(1L);
        verify(orderRepository).delete(order);
    }

    @Test
    void delete_ShouldThrow_WhenOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }

    // =============================================================
    // FIND (ADMIN)
    // =============================================================

//    @Test
//    void find_ShouldThrowForbidden_WhenNotAdmin() {
//        doThrow(new ForbiddenException("")).when(authService).validateAdmin();
//
//        assertThrows(ForbiddenException.class,
//                () -> service.find(1L, "Ana", "111", Pageable.unpaged()));
//    }
}
