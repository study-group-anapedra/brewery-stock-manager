package com.anapedra.stock_manager.repositories;

import com.anapedra.stock_manager.domain.entities.StockLoss;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface StockLossRepository extends JpaRepository<StockLoss, Long> {

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