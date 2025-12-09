package com.anapedra.stock_manager.services;

import com.anapedra.stock_manager.domain.dtos.BeerStockDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface de serviço para gerenciar as operações de consulta e relatórios
 * relacionados ao Estoque de Cervejas (Stock).
 *
 * <p>Define o contrato para a listagem paginada de cervejas com seus dados de estoque,
 * relatórios de itens vencidos e consultas avançadas que podem utilizar
 * funções de banco de dados (PL/pgSQL).</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @see BeerStockDTO
 * @since 0.0.1-SNAPSHOT
 */
public interface StockService {

    /**
     * Busca cervejas com informações de estoque paginadas aplicando filtros dinâmicos.
     *
     * <p>Os filtros incluem: ID da categoria, descrição da categoria, nome/descrição da cerveja
     * e faixa de quantidade em estoque.</p>
     *
     * @param categoryId ID da categoria (opcional).
     * @param categoryDescription Descrição da categoria (opcional, busca parcial).
     * @param beerDescription Nome/descrição da cerveja (opcional, busca parcial).
     * @param minQuantity Quantidade mínima em estoque (opcional).
     * @param maxQuantity Quantidade máxima em estoque (opcional).
     * @param pageable Objeto de paginação e ordenação do Spring Data.
     * @return Uma {@link Page} de {@link BeerStockDTO} que correspondem aos filtros.
     */
    Page<BeerStockDTO> findAllBeer(
        Long categoryId,
        String categoryDescription,
        String beerDescription,
        Integer minQuantity,
        Integer maxQuantity,
        Pageable pageable
    );

    /**
     * Busca uma cerveja com informações de estoque pelo seu identificador único.
     *
     * @param id O ID da cerveja.
     * @return O {@link BeerStockDTO} correspondente.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     */
    BeerStockDTO findById(Long id);

    /**
     * Gera um relatório de todas as cervejas cujo prazo de validade (expirationDate)
     * é anterior ou igual à data de referência fornecida.
     *
     * @param referenceDate A data limite para comparação (geralmente {@code LocalDate.now()}).
     * @return Uma {@link List} de {@link BeerStockDTO} que estão vencidas.
     */
    List<BeerStockDTO> getExpiredBeersReport(LocalDate referenceDate);

    /**
     * Executa uma consulta de estoque avançada usando uma função de banco de dados PL/pgSQL
     * e retorna o resultado paginado em formato de lista.
     *
     * <p>Esta consulta é projetada para manipular filtros complexos de forma eficiente
     * no lado do banco de dados, incluindo o número de dias até a expiração.</p>
     *
     * @param beerId ID da cerveja (opcional).
     * @param beerDescription Descrição da cerveja (opcional).
     * @param minQuantity Quantidade mínima em estoque (opcional).
     * @param maxQuantity Quantidade máxima em estoque (opcional).
     * @param daysUntilExpiry Número de dias para expiração (opcional).
     * @param pageSize Tamanho da página.
     * @param pageNumber Número da página.
     * @return Uma {@link List} de {@link BeerStockDTO} para a página solicitada.
     */
    List<BeerStockDTO> findUsingPlpgsqlFunction(
            Long beerId,
            String beerDescription,
            Integer minQuantity,
            Integer maxQuantity,
            Integer daysUntilExpiry,
            Integer pageSize,
            Integer pageNumber
    );

}