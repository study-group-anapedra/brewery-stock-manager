package com.anapedra.stock_manager.repositories;

import com.anapedra.stock_manager.domain.entities.StockLoss;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

/**
 * Repositório JPA para a entidade Perda de Estoque (StockLoss).
 *
 * <p>Esta interface estende {@link JpaRepository} e fornece métodos CRUD básicos,
 * além de uma consulta customizada para filtrar registros de perda de estoque
 * por diversos critérios, como motivo, cerveja, nome da cerveja, categoria e período de tempo.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Repository
public interface StockLossRepository extends JpaRepository<StockLoss, Long> {

    /**
     * Busca registros de perda de estoque paginados aplicando filtros dinâmicos.
     *
     * <p>Os filtros incluem: código do motivo da perda (Enum), ID da cerveja,
     * nome da cerveja (busca parcial), ID da categoria e um intervalo de datas
     * de ocorrência da perda (lossDate).</p>
     *
     * @param reasonCode O código inteiro do motivo da perda (LossReason).
     * @param beerId O ID da cerveja.
     * @param beerName O nome da cerveja (busca case-insensitive parcial).
     * @param categoryId O ID da categoria associada à cerveja.
     * @param startDate A data inicial para o filtro de {@code lossDate}.
     * @param endDate A data final para o filtro de {@code lossDate}.
     * @param pageable Objeto de paginação e ordenação do Spring Data.
     * @return Uma {@link Page} de entidades {@link StockLoss} que correspondem aos filtros.
     */
    @Query("""
        SELECT DISTINCT sl
        FROM StockLoss sl
        INNER JOIN sl.beer b
        INNER JOIN b.categories c
        WHERE
            (:reasonCode IS NULL OR sl.reason = :reasonCode)
        AND
            (:beerId IS NULL OR b.id = :beerId)
        AND
            (:beerName IS NULL OR :beerName = '' 
             OR LOWER(TRIM(b.name)) LIKE LOWER(CONCAT('%', :beerName, '%')))
        AND
            (:categoryId IS NULL OR c.id = :categoryId)
        AND
            (:startDate IS NULL OR sl.lossDate >= :startDate)
        AND
            (:endDate IS NULL OR sl.lossDate <= :endDate)
        """)
    Page<StockLoss> findLossesByFilters(
            Integer reasonCode,
            Long beerId,
            String beerName,
            Long categoryId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );
}