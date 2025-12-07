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
import org.slf4j.Logger; // Import do Logger
import org.slf4j.LoggerFactory; // Import do LoggerFactory


import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final AuthService authService;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final BeerRepository beerRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    private final Timer orderCreationTimer;
    private final Counter insufficientStockCounter;

    public OrderServiceImpl(
            AuthService authService,
            UserService userService,
            OrderRepository orderRepository,
            BeerRepository beerRepository,
            UserRepository userRepository,
            OrderItemRepository orderItemRepository,
            MeterRegistry registry
    ) {
        this.authService = authService;
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.beerRepository = beerRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;

        this.orderCreationTimer = Timer.builder("stock_manager.order.creation_time")
                .description("Tempo de execução da criação/atualização de pedidos")
                .register(registry);

        this.insufficientStockCounter = Counter.builder("stock_manager.order.insufficient_stock_errors")
                .description("Contagem de pedidos que falharam por falta de estoque")
                .register(registry);
    }
    
    @Transactional(readOnly = true)
    @Override
    public OrderDTO findById(Long id) {
        logger.info("SERVICE: Buscando pedido pelo ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("SERVICE WARN: Pedido ID {} não encontrado.", id);
                    return new ResourceNotFoundException("Order not found with id " + id);
                });
        authService.validateSelfOrAdmin(order.getClient().getId());
        logger.info("SERVICE: Pedido ID {} encontrado e acesso validado.", id);
        return new OrderDTO(order, order.getItems());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderDTO> findAll(Pageable pageable) {
        logger.info("SERVICE: Buscando todos os pedidos. Página: {}", pageable.getPageNumber());
        Page<OrderDTO> page = orderRepository.findAll(pageable)
                .map(order -> new OrderDTO(order, order.getItems()));
        logger.info("SERVICE: Retornando {} pedidos na página {}.", page.getNumberOfElements(), pageable.getPageNumber());
        return page;
    }

    @Transactional
    @Override
    public void delete(Long id) {
        logger.warn("SERVICE: Tentativa de exclusão do pedido ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("SERVICE ERROR: Pedido ID {} não encontrado para exclusão.", id);
                    return new ResourceNotFoundException("Order not found with id " + id);
                });
        authService.validateSelfOrAdmin(order.getClient().getId());
        orderRepository.delete(order);
        logger.info("SERVICE: Pedido ID {} excluído com sucesso.", id);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> find(
            Long clientId,
            String nameClient,
            String cpfClient,
            String minDate,
            String maxDate,
            Pageable pageable) {

        logger.info("SERVICE: Buscando pedidos com filtros (Admin). Client ID: {}, Data Min: {}", clientId, minDate);
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
        logger.info("SERVICE: Consulta de pedidos filtrados retornou {} elementos.", page.getNumberOfElements());
        return page.map(OrderDTO::new);
    }

    @Transactional
    @Override
    public OrderDTO save(OrderDTO dto) {
        logger.info("SERVICE: Iniciando criação de novo pedido. Itens: {}", dto.getItems().size());
        return orderCreationTimer.record(() -> {
            Order order = new Order();
            copyDtoToEntity(dto, order);
            Order savedOrder = orderRepository.save(order);
            logger.info("SERVICE: Pedido ID {} criado com sucesso para o cliente ID {}.", savedOrder.getId(), savedOrder.getClient().getId());
            return new OrderDTO(savedOrder, savedOrder.getItems());
        });
    }

    @Transactional
    @Override
    public OrderDTO update(Long id, OrderDTO dto) {
        logger.info("SERVICE: Iniciando atualização do pedido ID: {}", id);
        return orderCreationTimer.record(() -> {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("SERVICE WARN: Pedido ID {} não encontrado para atualização.", id);
                        return new ResourceNotFoundException("Order not found with id " + id);
                    });
            authService.validateSelfOrAdmin(order.getClient().getId());
            copyDtoToEntity(dto, order);
            Order savedOrder = orderRepository.save(order);
            logger.info("SERVICE: Pedido ID {} atualizado com sucesso.", savedOrder.getId());
            return new OrderDTO(savedOrder, savedOrder.getItems());
        });
    }


    private void copyDtoToEntity(OrderDTO dto, Order entity) {

        logger.debug("SERVICE: Iniciando mapeamento e validação de estoque para o pedido.");

        entity.setMomentAt(Instant.now());
        entity.setOrderStatus(dto.getOrderStatus());

        User authenticatedUser = authService.authenticatedUser();
        if (authenticatedUser == null) {
            logger.error("SERVICE ERROR: Usuário autenticado não encontrado.");
            throw new ForbiddenException("Authenticated user not found or not logged in.");
        }
        entity.setClient(authenticatedUser);
        logger.debug("SERVICE: Pedido associado ao cliente ID: {}", authenticatedUser.getId());


        if (entity.getId() != null) {
            logger.debug("SERVICE: Excluindo itens antigos do pedido ID {}.", entity.getId());
            orderItemRepository.deleteAll(entity.getItems());
            entity.getItems().clear();
        }

        entity.setItems(
                dto.getItems().stream()
                        .filter(java.util.Objects::nonNull)
                        .map(itemDTO -> {
                            if (itemDTO.getBeerId() == null) {
                                logger.error("SERVICE ERROR: Beer ID nulo em item do pedido.");
                                throw new IllegalArgumentException("Beer ID must not be null for an order item.");
                            }

                            Beer beer = beerRepository.findById(itemDTO.getBeerId())
                                    .orElseThrow(() -> {
                                        logger.warn("SERVICE WARN: Cerveja não encontrada ID: {} para item do pedido.", itemDTO.getBeerId());
                                        return new ResourceNotFoundException("Beer not found: " + itemDTO.getBeerId());
                                    });
                            logger.debug("SERVICE: Mapeando item para cerveja ID {} com quantidade {}.", beer.getId(), itemDTO.getQuantity());

                            return new OrderItem(entity, beer, itemDTO.getQuantity());
                        })
                        .collect(Collectors.toSet())
        );

        for (OrderItem orderItem : entity.getItems()) {
            Beer beer = orderItem.getBeer();

            if (orderItem.getQuantity() > beer.getStock().getQuantity()) {
                insufficientStockCounter.increment();
                logger.error("SERVICE ERROR: FALHA DE ESTOQUE para cerveja ID: {}. Disponível: {}, Solicitado: {}", 
                             beer.getId(), beer.getStock().getQuantity(), orderItem.getQuantity());

                throw new InsufficientStockException(
                        "Insufficient stock for beer ID: "
                                + beer.getId()
                                + ". Available: " + beer.getStock().getQuantity()
                                + ", Requested: " + orderItem.getQuantity()
                );
            }

            orderItem.decreaseStock(orderItem.getQuantity());
            logger.debug("SERVICE: Estoque da cerveja ID {} reduzido em {} unidades.", beer.getId(), orderItem.getQuantity());
        }
        logger.debug("SERVICE: Mapeamento concluído e estoque de todos os itens reduzido.");
    }
}