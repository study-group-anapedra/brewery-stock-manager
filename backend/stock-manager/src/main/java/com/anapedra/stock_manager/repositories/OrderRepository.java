package com.anapedra.stock_manager.repositories;

import com.anapedra.stock_manager.domain.entities.Order;
import com.anapedra.stock_manager.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {


    @Query("SELECT DISTINCT obj FROM Order obj INNER JOIN obj.client cli  WHERE " +
            "(COALESCE(:client) IS NULL OR cli IN :client) AND " +
            " (LOWER(obj.client.name) LIKE LOWER(CONCAT('%',:nameClint,'%'))) AND" +
            " (LOWER(obj.client.cpf) LIKE LOWER(CONCAT('%',:cpfClient,'%'))) ")
    Page<Order> find(User client, String nameClint, String cpfClient, Pageable pageable);
    @Query("SELECT obj FROM Order obj JOIN FETCH obj.client  WHERE obj IN :orders ")
    List<Order> findOrder(List<Order> orders);



}