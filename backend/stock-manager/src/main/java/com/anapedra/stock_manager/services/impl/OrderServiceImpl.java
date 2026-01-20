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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

/**
 * Implementação da interface {@link OrderService} que gerencia as operações de negócio
 * relacionadas à entidade Pedido (Order).
 *
 * <p>Esta classe lida com a criação de pedidos, validação de estoque,
 * manipulação de itens de pedido ({@link OrderItem}), e autorização de acesso
 * (via {@link AuthService}), garantindo a integridade transacional. Inclui
 * monitoramento de desempenho e erros usando Micrometer.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @see OrderService
 * @see Order
 * @since 0.0.1-SNAPSHOT
 */
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

    /**
     * Construtor responsável pela injeção de dependências do serviço de pedidos
     * e pela configuração das métricas de observabilidade via Micrometer.
     *
     * @param authService serviço responsável pela autenticação e validação de permissões
     * @param userService serviço de operações relacionadas ao usuário
     * @param orderRepository repositório de persistência de pedidos
     * @param beerRepository repositório de persistência de cervejas
     * @param userRepository repositório de persistência de usuários
     * @param orderItemRepository repositório de persistência dos itens do pedido
     * @param registry registro central de métricas do Micrometer
     */
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
    
    /**
     * Busca um pedido pelo seu ID, validando se o usuário autenticado é o cliente
     * associado ao pedido ou um administrador.
     *
     * @param id O ID do pedido.
     * @return O {@link OrderDTO} correspondente.
     * @throws ResourceNotFoundException Se o ID não for encontrado.
     * @throws ForbiddenException Se o usuário não tiver permissão de acesso.
     */
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

    /**
     * Retorna uma lista paginada de todos os pedidos.
     *
     * @param pageable Objeto de paginação e ordenação do Spring Data.
     * @return Uma {@link Page} de {@link OrderDTO}.
     */
    @Transactional(readOnly = true)
    @Override
    public Page<OrderDTO> findAll(Pageable pageable) {
        logger.info("SERVICE: Buscando todos os pedidos. Página: {}", pageable.getPageNumber());
        Page<OrderDTO> page = orderRepository.findAll(pageable)
                .map(order -> new OrderDTO(order, order.getItems()));
        logger.info("SERVICE: Retornando {} pedidos na página {}.", page.getNumberOfElements(), pageable.getPageNumber());
        return page;
    }

    /**
     * Exclui um pedido pelo seu ID, validando as permissões de acesso.
     *
     * @param id O ID do pedido a ser excluído.
     * @throws ResourceNotFoundException Se o ID não for encontrado.
     * @throws ForbiddenException Se o usuário não tiver permissão de acesso.
     */
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

    /**
     * Busca pedidos paginados aplicando filtros dinâmicos.
     *
     * <p>Esta operação é restrita a usuários com o papel ROLE_ADMIN.</p>
     *
     * @param clientId O ID do cliente (opcional).
     * @param nameClient O nome do cliente (opcional, busca parcial).
     * @param cpfClient O CPF do cliente (opcional, busca parcial).
     * @param minDate A data mínima para o filtro de data (opcional, String no formato LocalDate).
     * @param maxDate A data máxima para o filtro de data (opcional, String no formato LocalDate).
     * @param pageable Objeto de paginação e ordenação do Spring Data.
     * @return Uma {@link Page} de {@link OrderDTO} que correspondem aos filtros.
     * @throws ForbiddenException Se o usuário autenticado não for Admin.
     */
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

        // Conversão de LocalDate (String) para Instant, considerando UTC
        if (minDate != null && !minDate.isBlank()) {
            LocalDate minLD = LocalDate.parse(minDate);
            minInstant = minLD.atStartOfDay(ZoneOffset.UTC).toInstant();
        }

        if (maxDate != null && !maxDate.isBlank()) {
            // Adiciona 1 dia para incluir o dia inteiro no filtro max (ex: 2025-10-10 se torna 2025-10-11T00:00:00Z)
            LocalDate maxLD = LocalDate.parse(maxDate);
            maxInstant = maxLD.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        }


        Page<Order> page = orderRepository.find(client, nameClient, cpfClient, minInstant, maxInstant, pageable);
        // Otimiza N+1: Carrega os clientes de todos os pedidos na página
        orderRepository.findOrder(page.getContent());
        logger.info("SERVICE: Consulta de pedidos filtrados retornou {} elementos.", page.getNumberOfElements());
        return page.map(OrderDTO::new);
    }

    /**
     * Salva um novo pedido, verificando o estoque e debitando a quantidade
     * de cada item de forma transacional.
     *
     * @param dto O {@link OrderDTO} com os dados para criação.
     * @return O {@link OrderDTO} criado.
     * @throws ResourceNotFoundException Se o cliente ou alguma cerveja não for encontrada.
     * @throws InsufficientStockException Se a quantidade solicitada for maior que a disponível.
     */
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

    /**
     * Atualiza um pedido existente, validando as permissões de acesso.
     *
     * <p>A atualização envolve a exclusão dos itens antigos e o mapeamento dos novos,
     * com validação de estoque e débito.</p>
     *
     * @param id O ID do pedido a ser atualizado.
     * @param dto O {@link OrderDTO} com os dados atualizados.
     * @return O {@link OrderDTO} atualizado.
     * @throws ResourceNotFoundException Se o ID ou recursos associados não forem encontrados.
     * @throws ForbiddenException Se o usuário não tiver permissão de acesso.
     * @throws InsufficientStockException Se a quantidade solicitada for maior que a disponível.
     */
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

    /**
     * Copia os dados de um {@link OrderDTO} para a entidade {@link Order}, realizando validações de
     * integridade, segurança e regras de negócio.
     * * <p>O método executa as seguintes etapas críticas:</p>
     * <ul>
     * <li>Define o momento do pedido e o status inicial.</li>
     * <li>Realiza o <b>Self-enrollment</b>, associando o pedido ao usuário autenticado no sistema.</li>
     * <li>Em caso de atualização (ID presente), limpa os itens antigos para substituição.</li>
     * <li>Valida a existência de cada cerveja (Beer) no banco de dados.</li>
     * <li><b>Validação de Estoque:</b> Verifica se há saldo suficiente para cada item. Caso o pedido
     * exceda o estoque disponível, interrompe o processo.</li>
     * </ul>
     *
     * @param dto O Objeto de Transferência de Dados (DTO) contendo as informações do pedido.
     * @param entity A entidade {@link Order} de destino que será persistida ou atualizada.
     * @throws ForbiddenException Se não houver um usuário autenticado na sessão.
     * @throws ResourceNotFoundException Se o ID de uma cerveja fornecido no DTO não existir no banco.
     * @throws InsufficientStockException Se a quantidade solicitada de uma cerveja for maior que o saldo em estoque.
     * @throws IllegalArgumentException Se o ID da cerveja for nulo em algum item do pedido.
     */

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

        if (entity.getId() != null) {
            orderItemRepository.deleteAll(entity.getItems());
            entity.getItems().clear();
        }

        entity.setItems(
                dto.getItems().stream()
                        .filter(java.util.Objects::nonNull)
                        .map(itemDTO -> {
                            if (itemDTO.getBeerId() == null) {
                                logger.error("SERVICE ERROR: Beer ID nulo em item do pedido.");
                                throw new IllegalArgumentException("Beer ID must not be null.");
                            }

                            Beer beer = beerRepository.findById(itemDTO.getBeerId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Beer not found: " + itemDTO.getBeerId()));

                            // Validação de estoque (Regra de Negócio no Service)
                            int estoqueAtual = beer.getStock().getQuantity();

                            if (itemDTO.getQuantity() > estoqueAtual) {
                                logger.warn("SERVICE WARN: Estoque insuficiente para Cerveja ID: {}. Pedido: {}, Disponível: {}",
                                        beer.getId(), itemDTO.getQuantity(), estoqueAtual);

                                throw new InsufficientStockException(
                                        "Quantidade insuficiente em estoque para a cerveja: " + beer.getName() +
                                                ". Solicitado: " + itemDTO.getQuantity() +
                                                ", Disponível em estoque: " + estoqueAtual
                                );
                            }

                            logger.debug("SERVICE: Mapeando item para cerveja ID {} com quantidade {}.", beer.getId(), itemDTO.getQuantity());

                            // Criação da entidade OrderItem
                            OrderItem item = new OrderItem(entity, beer, itemDTO.getQuantity());

                            // EXECUÇÃO DA ATUALIZAÇÃO: A entidade altera seu próprio estado (Inversão de Controle)
                            item.setAtualStock();

                            return item;
                        })
                        .collect(Collectors.toSet())
        );
    }
}