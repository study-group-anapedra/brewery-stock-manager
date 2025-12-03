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

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {


    // Adiciona valores padrão extremos para que o filtro seja ignorado se o parâmetro for nulo,
    // mas o tipo de dado (Instant) seja sempre usado no BETWEEN.
    // Usamos :min e :max como estão, mas dependemos do Service para passar NULL ou o Instant
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

    @Query("SELECT obj FROM Order obj JOIN FETCH obj.client  WHERE obj IN :orders ")
    List<Order> findOrder(List<Order> orders);









    }