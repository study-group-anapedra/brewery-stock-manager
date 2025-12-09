package com.anapedra.stock_manager.repositories;

import com.anapedra.stock_manager.domain.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade Pagamento (Payment).
 *
 * <p>Esta interface estende {@link JpaRepository}, fornecendo métodos CRUD básicos
 * para a entidade {@link Payment}, que registra os detalhes de uma transação
 * associada a um pedido.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}