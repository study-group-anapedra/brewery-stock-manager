package com.anapedra.stock_manager.repositories;

import com.anapedra.stock_manager.domain.entities.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório JPA para a entidade Estoque (Stock).
 *
 * <p>Esta interface estende {@link JpaRepository}, fornecendo métodos CRUD básicos
 * para a entidade {@link Stock}, além de métodos de consulta derivados do nome
 * para buscar o estado atual do estoque de produtos.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    /**
     * Busca todos os registros de {@link Stock} onde o campo {@code quantity}
     * é menor que o valor especificado.
     *
     * <p>Este método é útil para identificar produtos com estoque baixo.</p>
     *
     * @param quantity O valor limite.
     * @return Uma {@link List} de entidades {@link Stock} que têm quantidade abaixo do limite.
     */
     List<Stock> findByQuantityLessThan(Integer quantity);

}