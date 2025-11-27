package com.anapedra.stock_manager.services.impl;

import com.anapedra.stock_manager.domain.dtos.OrderDTO;
import com.anapedra.stock_manager.domain.entities.Order;
import com.anapedra.stock_manager.domain.entities.OrderItem;
import com.anapedra.stock_manager.domain.entities.User;
import com.anapedra.stock_manager.repositories.BeerRepository;
import com.anapedra.stock_manager.repositories.OrderRepository;
import com.anapedra.stock_manager.repositories.UserRepository;
import com.anapedra.stock_manager.services.AuthService;
import com.anapedra.stock_manager.services.OrderService;

import com.anapedra.stock_manager.services.UserService;
import com.anapedra.stock_manager.services.exceptions.ForbiddenException;
import com.anapedra.stock_manager.services.exceptions.InsufficientStockException;
import com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final AuthService authService;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final BeerRepository beerRepository;
    private final UserRepository userRepository;


    public OrderServiceImpl(AuthService authService, UserService userService, OrderRepository orderRepository, BeerRepository beerRepository, UserRepository userRepository) {
        this.authService = authService;
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.beerRepository = beerRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public OrderDTO save(OrderDTO dto) {
        var authenticatedUser = Optional.ofNullable(userService.authenticated())
                .orElseThrow(() -> new ForbiddenException("User not authenticated"));

        var order = new Order();
        order.setClient(authenticatedUser);
        order.setMomentAt(Instant.now());
        copyDtoToEntity(dto, order);

        order = orderRepository.save(order);
        return new OrderDTO(order, order.getItems());
    }


    @Transactional(readOnly = true)
    @Override
    public OrderDTO findById(Long id) {
        var entity = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));
        authService.validateSelfOrAdmin(entity.getClient().getId());
        return new OrderDTO(entity, entity.getItems());
    }


    @Transactional(readOnly = true)
    @Override
    public Page<OrderDTO> findAll(Pageable pageable) {
        // Implementação do findAll
        // Se necessário, adicione lógica de validação de permissão aqui
        return orderRepository.findAll(pageable).map(order -> new OrderDTO(order, order.getItems()));
    }


    @Transactional
    @Override
    public OrderDTO update(Long id, OrderDTO dto) {
        var existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));
        authService.validateSelfOrAdmin(existingOrder.getClient().getId());

        existingOrder.setOrderStatus(dto.getOrderStatus());
        copyDtoToEntity(dto, existingOrder);

        existingOrder = orderRepository.save(existingOrder);
        return new OrderDTO(existingOrder, existingOrder.getItems());
    }

    @Transactional
    @Override
    public void delete(Long id) {
        var existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));
        authService.validateSelfOrAdmin(existingOrder.getClient().getId());
        orderRepository.delete(existingOrder);
    }


    private void copyDtoToEntity(OrderDTO dto, Order entity) {
        entity.setOrderStatus(dto.getOrderStatus());
        entity.setItems(dto.getItems().stream().map(itemDTO -> {
            var beer = beerRepository.findById(itemDTO.getBeerId())
                    .orElseThrow(() -> new IllegalArgumentException("Beer not found: " + itemDTO.getBeerId()));

            if (itemDTO.getQuantity() > beer.getStock().getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for beer ID: "
                        + beer.getId() + ". Available: " + beer.getStock().getQuantity() +
                        ", Requested: " + itemDTO.getQuantity());
            }

            var orderItem = new OrderItem(entity, beer, itemDTO.getQuantity());
            orderItem.decreaseStock(itemDTO.getQuantity());
            return orderItem;
        }).collect(Collectors.toSet()));
    }
    
    @Transactional
    @Override
    public Page<OrderDTO> find(Long clientId, String nameClient,String cpfClient, Pageable pageable){
        authService.validateAdmin();
        User client=(clientId == 0) ? null : userRepository.getOne(clientId);
        Page<Order> page=orderRepository.find(client,nameClient,cpfClient,pageable);
        orderRepository.findOrder(page.stream().collect(Collectors.toList()));
        return page.map(OrderDTO::new);
    }


}