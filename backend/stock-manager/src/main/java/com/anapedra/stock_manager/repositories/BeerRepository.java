package com.anapedra.stock_manager.repositories;

import com.anapedra.stock_manager.domain.dtos.BeerStockDTO;
import com.anapedra.stock_manager.domain.entities.Beer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositório JPA para a entidade {@link Beer}.
 *
 * <p>Fornece métodos para operações CRUD e consultas customizadas relacionadas
 * à cerveja, incluindo filtros complexos por categorias e estoque,
 * e chamadas a funções PL/pgSQL.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Repository
public interface BeerRepository extends JpaRepository<Beer, Long> {

    // ------------------------------------------------------------
    // 1. Consulta JPQL com filtros dinâmicos
    // ------------------------------------------------------------
    /**
     * Busca cervejas paginadas aplicando filtros dinâmicos com base em
     * categoria, nome/descrição da cerveja e faixa de quantidade em estoque.
     *
     * <p>Utiliza {@code COALESCE(s.quantity, 0)} para garantir que cervejas
     * sem registro de estoque sejam consideradas com quantidade zero nos filtros.</p>
     *
     * @param categoryId ID da categoria (opcional).
     * @param categoryName Descrição da categoria (opcional, busca parcial).
     * @param beerDescription Nome/descrição da cerveja (opcional, busca parcial).
     * @param minQuantity Quantidade mínima em estoque (opcional).
     * @param maxQuantity Quantidade máxima em estoque (opcional).
     * @param pageable Objeto de paginação e ordenação do Spring Data.
     * @return Uma {@link Page} de entidades {@link Beer} que correspondem aos filtros.
     */
    @Query("""
            SELECT DISTINCT b FROM Beer b
            LEFT JOIN b.categories c
            LEFT JOIN b.stock s
            WHERE (:categoryId IS NULL OR c.id = :categoryId)
              AND (:categoryName IS NULL OR :categoryName = ''
                    OR LOWER(CAST(c.name AS text)) LIKE LOWER(CONCAT('%', :categoryName, '%')))
              AND (:beerDescription IS NULL OR :beerDescription = ''
                    OR LOWER(CAST(b.name AS text)) LIKE LOWER(CONCAT('%', :beerDescription, '%')))
              AND (:minQuantity IS NULL OR COALESCE(s.quantity, 0) >= :minQuantity)
              AND (:maxQuantity IS NULL OR COALESCE(s.quantity, 0) <= :maxQuantity)
            """)
    Page<Beer> findAllBeer(
            @Param("categoryId") Long categoryId,
            @Param("categoryName") String categoryName,
            @Param("beerDescription") String beerDescription,
            @Param("minQuantity") Integer minQuantity,
            @Param("maxQuantity") Integer maxQuantity,
            Pageable pageable
    );


    // ------------------------------------------------------------
    // 2. Chamada de função PL/pgSQL
    // ------------------------------------------------------------
    /**
     * Executa uma função de banco de dados nativa (PL/pgSQL) para buscar cervejas
     * com filtros complexos (incluindo dias para expiração).
     *
     * <p>Utiliza CASTs explícitos para resolver inconsistências de tipos entre o driver
     * JDBC e o PostgreSQL, garantindo que a função seja localizada corretamente.</p>
     *
     * @param beerId ID da cerveja (opcional).
     * @param beerDescription Descrição da cerveja (opcional).
     * @param minQuantity Quantidade mínima em estoque (opcional).
     * @param maxQuantity Quantidade máxima em estoque (opcional).
     * @param daysUntilExpiry Número de dias para expiração (opcional).
     * @param pageSize Tamanho da página para paginação.
     * @param pageNumber Número da página a ser retornada.
     * @return Uma {@link List} de {@link BeerStockDTO} resultante da função.
     */
    @Query(value = """
        SELECT * FROM find_beers_using_filters(
            CAST(:beerId AS BIGINT), 
            CAST(:beerDescription AS TEXT), 
            CAST(:minQuantity AS INTEGER), 
            CAST(:maxQuantity AS INTEGER), 
            CAST(:daysUntilExpiry AS INTEGER), 
            CAST(:pageSize AS INTEGER), 
            CAST(:pageNumber AS INTEGER)
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
    /**
     * Busca todas as cervejas cuja data de validade (expirationDate)
     * é anterior à data de referência fornecida.
     *
     * @param referenceDate A data limite para comparação (geralmente {@code LocalDate.now()}).
     * @return Uma {@link List} de entidades {@link Beer} vencidas.
     */
    @Query("""
            SELECT b FROM Beer b
            WHERE b.expirationDate < :referenceDate
            """)
    List<Beer> findExpiredBeersBefore(
            @Param("referenceDate") LocalDate referenceDate
    );
}