package com.anapedra.stock_manager.services;

import com.anapedra.stock_manager.domain.dtos.OrderDTO;
import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.Order;
import com.anapedra.stock_manager.domain.entities.Stock;
import com.anapedra.stock_manager.domain.entities.User;
import com.anapedra.stock_manager.repositories.BeerRepository;
import com.anapedra.stock_manager.repositories.OrderItemRepository;
import com.anapedra.stock_manager.repositories.OrderRepository;
import com.anapedra.stock_manager.repositories.UserRepository;
import com.anapedra.stock_manager.services.exceptions.ForbiddenException;
import com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException;
import com.anapedra.stock_manager.services.impl.OrderServiceImpl;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry; // Importação CRÍTICA
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    private AuthService authService;
    private UserService userService;
    private OrderRepository orderRepository;
    private BeerRepository beerRepository;
    private UserRepository userRepository;
    private OrderItemRepository orderItemRepository;
    private SimpleMeterRegistry meterRegistry;

    private OrderServiceImpl service;

    private User user;
    private Beer beer;
    private Stock stock;

    @BeforeEach
    void setup() {
        // Inicializa os Mocks das dependências
        authService = mock(AuthService.class);
        userService = mock(UserService.class);
        orderRepository = mock(OrderRepository.class);
        beerRepository = mock(BeerRepository.class);
        userRepository = mock(UserRepository.class);
        orderItemRepository = mock(OrderItemRepository.class);

        // **CORREÇÃO CRÍTICA**: Inicializa o MeterRegistry com uma implementação real e simples.
        // Isso resolve a NullPointerException no construtor.
        meterRegistry = new SimpleMeterRegistry(); 


        // **CORREÇÃO**: O construtor do serviço agora recebe o MeterRegistry no final
        service = new OrderServiceImpl(
                authService, userService, orderRepository,
                beerRepository, userRepository, orderItemRepository,
                meterRegistry

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
        // CORREÇÃO: O serviço agora usa authService.authenticatedUser() para checar permissão/autenticação
        when(authService.authenticatedUser()).thenReturn(null);
        assertThrows(ForbiddenException.class, () -> service.save(new OrderDTO()));
    }

    // Teste findById com sucesso
    @Test
    void findById_ShouldReturnOrder_WhenOrderExistsAndUserAuthorized() {
        Order order = new Order();
        order.setId(1L);
        order.setClient(user);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        
        // Simula autorização
        doNothing().when(authService).validateSelfOrAdmin(1L);

        // Execução
        OrderDTO result = service.findById(1L);

        // Verificação
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(authService).validateSelfOrAdmin(1L);
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
    
    // Adicionado teste para update_ShouldThrowForbidden
    @Test
    void update_ShouldThrowForbidden_WhenUserNotAuthorized() {
        Order existingOrder = new Order();
        existingOrder.setId(1L);
        existingOrder.setClient(user);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        // Força a exceção de permissão
        doThrow(new ForbiddenException("")).when(authService).validateSelfOrAdmin(anyLong()); 

        assertThrows(ForbiddenException.class, () -> service.update(1L, new OrderDTO()));
        verify(authService).validateSelfOrAdmin(user.getId());
    }


    @Test
    void delete_ShouldRemoveOrder_WhenUserAuthorized() {
        Order order = new Order();
        order.setId(1L);
        order.setClient(user);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        // Simula autorização
        doNothing().when(authService).validateSelfOrAdmin(anyLong());

        service.delete(1L);

        verify(authService).validateSelfOrAdmin(user.getId());
        verify(orderRepository).delete(order);
    }

    @Test
    void delete_ShouldThrow_WhenOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }


    @Test
    void find_ShouldThrowForbidden_WhenNotAdmin() {
        doThrow(new ForbiddenException("")).when(authService).validateAdmin();

        assertThrows(ForbiddenException.class,
                () -> service.find(1L, "Ana", "01589924578","","",Pageable.unpaged()));
        
        verify(authService).validateAdmin();
    }
}