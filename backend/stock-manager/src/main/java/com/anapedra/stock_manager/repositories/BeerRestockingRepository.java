package com.anapedra.stock_manager.repositories;

import com.anapedra.stock_manager.domain.entities.BeerRestocking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade Reposição de Cerveja (BeerRestocking).
 *
 * <p>Esta interface estende {@link JpaRepository}, fornecendo métodos CRUD básicos
 * para operações de persistência relacionadas ao registro de reposição de estoque.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Repository
public interface BeerRestockingRepository extends JpaRepository<BeerRestocking, Long> {

}