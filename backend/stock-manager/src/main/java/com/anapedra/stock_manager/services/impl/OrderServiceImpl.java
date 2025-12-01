package com.anapedra.stock_manager.services.impl;

import com.anapedra.stock_manager.domain.dtos.OrderDTO;
import com.anapedra.stock_manager.domain.entities.Order;
import com.anapedra.stock_manager.domain.entities.OrderItem;
import com.anapedra.stock_manager.domain.entities.Payment;
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
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final AuthService authService;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final BeerRepository beerRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(
            AuthService authService,
            UserService userService,
            OrderRepository orderRepository,
            BeerRepository beerRepository,
            UserRepository userRepository
    ) {
        this.authService = authService;
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.beerRepository = beerRepository;
        this.userRepository = userRepository;
    }

    // -------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------

    @Transactional
    @Override
    public OrderDTO save(OrderDTO dto) {

        // Agora sempre pega o cliente do usuário logado 
        User authenticated = userService.authenticated();
        if (authenticated == null) {
            throw new ForbiddenException("User not authenticated");
        }

        Order order = new Order();
        order.setClient(authenticated);
        order.setMomentAt(Instant.now());



        copyDtoToEntity(dto, order);

        orderRepository.save(order);
        return new OrderDTO(order, order.getItems());
    }

    // -------------------------------------------------------------
    // FIND BY ID
    // -------------------------------------------------------------

    @Transactional(readOnly = true)
    @Override
    public OrderDTO findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));

        authService.validateSelfOrAdmin(order.getClient().getId());

        return new OrderDTO(order, order.getItems());
    }

    // -------------------------------------------------------------
    // LIST ALL
    // -------------------------------------------------------------

    @Transactional(readOnly = true)
    @Override
    public Page<OrderDTO> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(order -> new OrderDTO(order, order.getItems()));
    }






    // -------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------

    @Transactional
    @Override
    public OrderDTO update(Long id, OrderDTO dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));

        authService.validateSelfOrAdmin(order.getClient().getId());



        copyDtoToEntity(dto, order);

        orderRepository.save(order);
        return new OrderDTO(order, order.getItems());
    }




    // -------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------




    @Transactional
    @Override
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));

        authService.validateSelfOrAdmin(order.getClient().getId());
        orderRepository.delete(order);
    }

    // -------------------------------------------------------------
    // FILTER SEARCH
    // -------------------------------------------------------------







    // -------------------------------------------------------------
    // DTO → ENTITY
    // -------------------------------------------------------------

    private void copyDtoToEntity(OrderDTO dto, Order entity) {

        // MOMENTO
        if (dto.getMomentAt() != null) {
            entity.setMomentAt(dto.getMomentAt());
        }

        // PAYMENT
        if (dto.getPayment() != null) {
            Payment payment = new Payment();
            payment.setMoment(dto.getPayment().getMoment());
            payment.setOrder(entity);
            entity.setPayment(payment);
        }

        // ORDER ITEMS
        if (dto.getItems() != null) {
            Set<OrderItem> items = dto.getItems().stream().map(itemDTO -> {

                var beer = beerRepository.findById(itemDTO.getBeerId())
                        .orElseThrow(() -> new IllegalArgumentException("Beer not found: " + itemDTO.getBeerId()));

                if (itemDTO.getQuantity() > beer.getStock().getQuantity()) {
                    throw new InsufficientStockException(
                            "Insufficient stock for beer ID " + beer.getId()
                                    + ". Available: " + beer.getStock().getQuantity()
                                    + ", Requested: " + itemDTO.getQuantity()
                    );
                }

                OrderItem orderItem = new OrderItem(entity, beer, itemDTO.getQuantity());
                orderItem.decreaseStock(itemDTO.getQuantity());
                return orderItem;

            }).collect(Collectors.toSet());

            entity.setItems(items);
        }
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

        User client = (clientId != null && clientId > 0) ? userRepository.findById(clientId).orElse(null) : null;

        Instant minInstant = null;
        Instant maxInstant = null;

        // Apenas se minDate for informado, defina o Instant
        if (minDate != null && !minDate.isBlank()) {
            LocalDate minLD = LocalDate.parse(minDate);
            minInstant = minLD.atStartOfDay(ZoneOffset.UTC).toInstant();
        }

        // Apenas se maxDate for informado, defina o Instant
        if (maxDate != null && !maxDate.isBlank()) {
            LocalDate maxLD = LocalDate.parse(maxDate);
            // Garante que o intervalo vá até o final do dia
            maxInstant = maxLD.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        }

        // Se nenhum parâmetro de filtro for passado (tudo nulo),
        // a busca retornará todas as orders, conforme desejado.
        Page<Order> page = orderRepository.find(client, nameClient, cpfClient, minInstant, maxInstant, pageable);

        // Faz fetch da relação cliente
        orderRepository.findOrder(page.getContent());

        return page.map(OrderDTO::new);
    }
}
