package com.anapedra.stock_manager.repositories;


import com.anapedra.stock_manager.domain.entities.Beer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BeerRepository extends JpaRepository<Beer, Long> {

    @Query("""
        SELECT DISTINCT b
        FROM Beer b
        INNER JOIN b.categories c
        LEFT JOIN Stock s ON s.beer.id = b.id
        WHERE
            (:categoryId IS NULL OR c.id = :categoryId)
        AND
            (:categoryDescription IS NULL OR :categoryDescription = '' 
             OR LOWER(TRIM(CAST(c.name AS text))) LIKE LOWER(CONCAT('%', :categoryDescription, '%')))
        AND
            (:beerDescription IS NULL OR :beerDescription = '' 
             OR LOWER(TRIM(CAST(b.name AS text))) LIKE LOWER(CONCAT('%', :beerDescription, '%')))
        AND
            (:minQuantity IS NULL OR COALESCE(s.quantity, 0) >= :minQuantity)
        AND 
            (:maxQuantity IS NULL OR COALESCE(s.quantity, 0) <= :maxQuantity)
        """)
    Page<Beer> findAllBeer(
            Long categoryId,
            String categoryDescription,
            String beerDescription,
            Integer minQuantity,
            Integer maxQuantity,
            Pageable pageable
    );


    @Query("SELECT b FROM Beer b WHERE b.expirationDate < :referenceDate")
    List<Beer> findExpiredBeersBefore(@Param("referenceDate") LocalDate referenceDate);



}
