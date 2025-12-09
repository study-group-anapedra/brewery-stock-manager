package com.anapedra.stock_manager.repositories;

import com.anapedra.stock_manager.domain.entities.OrderItem;
import com.anapedra.stock_manager.domain.pks.OrderItemPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade Item de Pedido (OrderItem).
 *
 * <p>Esta interface estende {@link JpaRepository} e usa a chave composta
 * {@link OrderItemPK} como seu tipo de ID. Fornece métodos CRUD básicos
 * para manipulação dos itens associados a pedidos.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPK> {


    //Optional<OrderItemDTO> findById(Long livroId); // Comentário mantido (ignorado)
}