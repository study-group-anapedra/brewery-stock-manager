package com.anapedra.stock_manager.repositories;

import com.anapedra.stock_manager.domain.entities.Order;
import com.anapedra.stock_manager.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Repositório JPA para a entidade Pedido (Order).
 *
 * <p>Fornece métodos para operações CRUD e consultas customizadas
 * relacionadas aos pedidos, incluindo filtros complexos por cliente,
 * CPF, nome e intervalo de tempo.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {


    /**
     * Busca pedidos paginados aplicando filtros dinâmicos por cliente, nome/CPF
     * do cliente e intervalo de tempo de criação do pedido (momentAt).
     *
     * <p>Utiliza {@code COALESCE} para garantir que o filtro de data seja ignorado
     * se o parâmetro {@code min} ou {@code max} for nulo, permitindo que a consulta
     * funcione corretamente no HQL sem exigir valores padrão extremos no Service.</p>
     *
     * @param client A entidade {@link User} específica do cliente (opcional).
     * @param nameClient O nome do cliente (opcional, busca parcial).
     * @param cpfClient O CPF do cliente (opcional, busca parcial).
     * @param min O {@link Instant} mínimo para o filtro de data (opcional).
     * @param max O {@link Instant} máximo para o filtro de data (opcional).
     * @param pageable Objeto de paginação e ordenação do Spring Data.
     * @return Uma {@link Page} de entidades {@link Order} que correspondem aos filtros.
     */
    @Query("SELECT DISTINCT obj FROM Order obj INNER JOIN obj.client cli " +
            "WHERE (:client IS NULL OR cli = :client) " +
            "AND (:nameClient IS NULL OR :nameClient = '' OR LOWER(cli.name) LIKE LOWER(CONCAT('%', :nameClient, '%'))) " +
            "AND (:cpfClient IS NULL OR :cpfClient = '' OR LOWER(cli.cpf) LIKE LOWER(CONCAT('%', :cpfClient, '%'))) " +
            "AND (obj.momentAt BETWEEN COALESCE(:min, obj.momentAt) AND COALESCE(:max, obj.momentAt))")
    Page<Order> find(
            @Param("client") User client,
            @Param("nameClient") String nameClient,
            @Param("cpfClient") String cpfClient,
            @Param("min") Instant min,
            @Param("max") Instant max,
            Pageable pageable);

    /**
     * Busca uma lista de pedidos, forçando o carregamento eager (JOIN FETCH)
     * da entidade Cliente (client) para evitar problemas de N+1 queries
     * (LazyInitializationException) ao iterar sobre os resultados.
     *
     * @param orders Uma lista de entidades {@link Order} (geralmente IDs já consultados).
     * @return Uma {@link List} de entidades {@link Order} com o cliente carregado.
     */
    @Query("SELECT obj FROM Order obj JOIN FETCH obj.client  WHERE obj IN :orders ")
    List<Order> findOrder(List<Order> orders);


}