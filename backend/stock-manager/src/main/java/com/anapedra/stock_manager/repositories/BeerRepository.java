package com.anapedra.stock_manager.repositories;

import com.anapedra.stock_manager.domain.dtos.BeerStockDTO;
import com.anapedra.stock_manager.domain.entities.Beer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BeerRepository extends JpaRepository<Beer, Long> {

    // ------------------------------------------------------------
    // 1. Consulta JPQL com filtros dinâmicos
    // CORRIGIDO: Removidos comentários internos para evitar erro de sintaxe HQL.
    // COALESCE(s.quantity, 0) adicionado para tratar estoque nulo como zero.
    // ------------------------------------------------------------
    @Query("""
            SELECT DISTINCT b FROM Beer b
            LEFT JOIN b.categories c
            LEFT JOIN b.stock s
            WHERE (:categoryId IS NULL OR c.id = :categoryId)
              AND (:categoryDescription IS NULL OR :categoryDescription = ''
                    OR LOWER(CAST(c.description AS text)) LIKE LOWER(CONCAT('%', :categoryDescription, '%')))
              AND (:beerDescription IS NULL OR :beerDescription = ''
                    OR LOWER(CAST(b.name AS text)) LIKE LOWER(CONCAT('%', :beerDescription, '%')))
              AND (:minQuantity IS NULL OR COALESCE(s.quantity, 0) >= :minQuantity)
              AND (:maxQuantity IS NULL OR COALESCE(s.quantity, 0) <= :maxQuantity)
            """)
    Page<Beer> findAllBeer(
            @Param("categoryId") Long categoryId,
            @Param("categoryDescription") String categoryDescription,
            @Param("beerDescription") String beerDescription,
            @Param("minQuantity") Integer minQuantity,
            @Param("maxQuantity") Integer maxQuantity,
            Pageable pageable
    );






    @Query(value = """
    SELECT * FROM find_beers_using_filters(
        :beerId,
        :beerDescription,
        :minQuantity,
        :maxQuantity,
        :daysUntilExpiry,
        :pageSize,
        :pageNumber
    )
""", nativeQuery = true)
    List<BeerStockDTO> findBeersUsingPlpgsqlFunction(
            @Param("beerId") Long beerId,
            @Param("beerDescription") String beerDescription,
            @Param("minQuantity") Integer minQuantity,
            @Param("maxQuantity") Integer maxQuantity,
            @Param("daysUntilExpiry") Integer daysUntilExpiry,
            @Param("pageSize") Integer pageSize,
            @Param("pageNumber") Integer pageNumber
    );


    // ------------------------------------------------------------
    // 3. Relatório de cervejas vencidas
    // ------------------------------------------------------------
    @Query("""
            SELECT b FROM Beer b
            WHERE b.expirationDate < :referenceDate
            """)
    List<Beer> findExpiredBeersBefore(
            @Param("referenceDate") LocalDate referenceDate
    );
}