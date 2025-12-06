package com.anapedra.stock_manager.services.impl;

import com.anapedra.stock_manager.domain.dtos.OrderDTO;
import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.Order;
import com.anapedra.stock_manager.domain.entities.OrderItem;
import com.anapedra.stock_manager.domain.entities.User;
import com.anapedra.stock_manager.repositories.BeerRepository;
import com.anapedra.stock_manager.repositories.OrderItemRepository;
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
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;


import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final AuthService authService;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final BeerRepository beerRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    private final Timer orderCreationTimer; // Timer para a criação de pedidos
    private final Counter insufficientStockCounter; // Contador para erros de estoque

    public OrderServiceImpl(
            AuthService authService,
            UserService userService,
            OrderRepository orderRepository,
            BeerRepository beerRepository,
            UserRepository userRepository,
            OrderItemRepository orderItemRepository,
            MeterRegistry registry // Injeção do MeterRegistry
    ) {
        this.authService = authService;
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.beerRepository = beerRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;

        // Inicializa o Timer
        this.orderCreationTimer = Timer.builder("stock_manager.order.creation_time")
                .description("Tempo de execução da criação/atualização de pedidos")
                .register(registry);

        // Inicializa o Counter
        this.insufficientStockCounter = Counter.builder("stock_manager.order.insufficient_stock_errors")
                .description("Contagem de pedidos que falharam por falta de estoque")
                .register(registry);
    }
    @Transactional(readOnly = true)
    @Override
    public OrderDTO findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));
        authService.validateSelfOrAdmin(order.getClient().getId());
        return new OrderDTO(order, order.getItems());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderDTO> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(order -> new OrderDTO(order, order.getItems()));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));
        authService.validateSelfOrAdmin(order.getClient().getId());
        orderRepository.delete(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> find(
            Long clientId,
            String nameClient,
            String cpfClient,
            String minDate,
            String maxDate,
            Pageable pageable) {

        authService.validateAdmin();

        User client = (clientId != null && clientId > 0)
                ? userRepository.findById(clientId).orElse(null)
                : null;

        Instant minInstant = null;
        Instant maxInstant = null;

        if (minDate != null && !minDate.isBlank()) {
            LocalDate minLD = LocalDate.parse(minDate);
            minInstant = minLD.atStartOfDay(ZoneOffset.UTC).toInstant();
        }

        if (maxDate != null && !maxDate.isBlank()) {
            LocalDate maxLD = LocalDate.parse(maxDate);
            maxInstant = maxLD.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        }



        Page<Order> page = orderRepository.find(client, nameClient, cpfClient, minInstant, maxInstant, pageable);
        orderRepository.findOrder(page.getContent());
        return page.map(OrderDTO::new);
    }

    @Transactional
    @Override
    public OrderDTO save(OrderDTO dto) {
        return orderCreationTimer.record(() -> {
            Order order = new Order();
            copyDtoToEntity(dto, order);
            Order savedOrder = orderRepository.save(order);
            return new OrderDTO(savedOrder, savedOrder.getItems());
        });
    }

    @Transactional
    @Override
    public OrderDTO update(Long id, OrderDTO dto) {
        return orderCreationTimer.record(() -> {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));
            authService.validateSelfOrAdmin(order.getClient().getId());
            copyDtoToEntity(dto, order);
            Order savedOrder = orderRepository.save(order);
            return new OrderDTO(savedOrder, savedOrder.getItems());
        });
    }


    private void copyDtoToEntity(OrderDTO dto, Order entity) {

        entity.setMomentAt(Instant.now());
        entity.setOrderStatus(dto.getOrderStatus());

        User authenticatedUser = authService.authenticatedUser();
        if (authenticatedUser == null) {
            throw new ForbiddenException("Authenticated user not found or not logged in.");
        }
        entity.setClient(authenticatedUser);

        if (entity.getId() != null) {
            orderItemRepository.deleteAll(entity.getItems());
            entity.getItems().clear();
        }

        entity.setItems(
                dto.getItems().stream()
                        .filter(java.util.Objects::nonNull)
                        .map(itemDTO -> {
                            if (itemDTO.getBeerId() == null) {
                                throw new IllegalArgumentException("Beer ID must not be null for an order item.");
                            }

                            Beer beer = beerRepository.findById(itemDTO.getBeerId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Beer not found: " + itemDTO.getBeerId()));

                            return new OrderItem(entity, beer, itemDTO.getQuantity());
                        })
                        .collect(Collectors.toSet())
        );

        for (OrderItem orderItem : entity.getItems()) {
            Beer beer = orderItem.getBeer();

            if (orderItem.getQuantity() > beer.getStock().getQuantity()) {
                insufficientStockCounter.increment();
                throw new InsufficientStockException(
                        "Insufficient stock for beer ID: "
                                + beer.getId()
                                + ". Available: " + beer.getStock().getQuantity()
                                + ", Requested: " + orderItem.getQuantity()
                );
            }

            orderItem.decreaseStock(orderItem.getQuantity());
        }
    }
}
