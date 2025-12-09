package com.anapedra.stock_manager.services;

import com.anapedra.stock_manager.domain.dtos.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface de serviço para gerenciar as operações relacionadas a Pedidos (Order).
 *
 * <p>Define o contrato para as operações CRUD (Create, Read, Update, Delete),
 * listagem paginada e consultas avançadas com filtros de cliente e data,
 * trabalhando com o DTO {@link OrderDTO} para transferência de dados.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @see OrderDTO
 * @since 0.0.1-SNAPSHOT
 */
public interface OrderService {

    /**
     * Salva e persiste um novo pedido.
     *
     * @param dto O {@link OrderDTO} com os dados para criação.
     * @return O {@link OrderDTO} criado, incluindo o ID gerado.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o cliente não for encontrado.
     */
    OrderDTO save(OrderDTO dto);

    /**
     * Busca um pedido pelo seu identificador único.
     *
     * @param id O ID do pedido.
     * @return O {@link OrderDTO} correspondente.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     */
    OrderDTO findById(Long id);

    /**
     * Retorna uma lista paginada de todos os pedidos.
     *
     * @param pageable Objeto de paginação e ordenação do Spring Data.
     * @return Uma {@link Page} de {@link OrderDTO}.
     */
    Page<OrderDTO> findAll(Pageable pageable);

    /**
     * Atualiza um pedido existente.
     *
     * @param id O ID do pedido a ser atualizado.
     * @param dto O {@link OrderDTO} com os dados atualizados.
     * @return O {@link OrderDTO} atualizado.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID ou cliente não for encontrado.
     * @throws com.anapedra.stock_manager.services.exceptions.DatabaseException Se houver violação de dados.
     */
    OrderDTO update(Long id, OrderDTO dto);

    /**
     * Exclui um pedido pelo seu identificador único.
     *
     * @param id O ID do pedido a ser excluído.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     * @throws com.anapedra.stock_manager.services.exceptions.DatabaseException Se houver violação de integridade.
     */
    void delete(Long id);

    /**
     * Busca pedidos paginados aplicando filtros dinâmicos.
     *
     * <p>Os filtros incluem: ID do cliente, nome/CPF do cliente (busca parcial)
     * e um intervalo de tempo de criação do pedido (momentAt), passado como String no formato Instant.</p>
     *
     * @param clientId O ID do cliente (opcional).
     * @param nameClient O nome do cliente (opcional, busca parcial).
     * @param cpfClient O CPF do cliente (opcional, busca parcial).
     * @param minInstant O Instant mínimo para o filtro de data (opcional, String ISO-8601).
     * @param maxInstant O Instant máximo para o filtro de data (opcional, String ISO-8601).
     * @param pageable Objeto de paginação e ordenação do Spring Data.
     * @return Uma {@link Page} de {@link OrderDTO} que correspondem aos filtros.
     */
    Page<OrderDTO> find(
            Long clientId,
            String nameClient,
            String cpfClient,
            String minInstant,
            String maxInstant,
            Pageable pageable
    );
}