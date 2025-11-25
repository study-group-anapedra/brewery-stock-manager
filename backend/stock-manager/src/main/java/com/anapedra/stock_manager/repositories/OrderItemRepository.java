package com.anapedra.stock_manager.repositories;

import com.anapedra.stock_manager.domain.entities.OrderItem;
import com.anapedra.stock_manager.domain.pks.OrderItemPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPK> {


    //Optional<OrderItemDTO> findById(Long livroId);
}