package com.anapedra.stock_manager.services.impl;

import com.anapedra.stock_manager.domain.dtos.BeerStockDTO;
import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.repositories.BeerRepository;
import com.anapedra.stock_manager.services.StockService;
import com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementação da interface {@link StockService} que gerencia as operações de consulta
 * e relatórios relacionadas ao Estoque de Cervejas (Stock).
 *
 * <p>Esta classe foca em operações de leitura, como listagem paginada com filtros,
 * busca por ID, relatórios de itens vencidos e consultas complexas utilizando
 * funções de banco de dados (PL/pgSQL).</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @see StockService
 * @see Beer
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class StockServiceImpl implements StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockServiceImpl.class);

    private final BeerRepository beerRepository;

    /**
     * Construtor para injeção de dependências.
     *
     * @param beerRepository Repositório de cervejas.
     */
    public StockServiceImpl(BeerRepository beerRepository) {
        this.beerRepository = beerRepository;
    }


    /**
     * Busca cervejas com informações de estoque paginadas aplicando filtros dinâmicos.
     *
     * @param categoryId ID da categoria (opcional).
     * @param categoryName Descrição da categoria (opcional, busca parcial).
     * @param beerDescription Nome/descrição da cerveja (opcional, busca parcial).
     * @param minQuantity Quantidade mínima em estoque (opcional).
     * @param maxQuantity Quantidade máxima em estoque (opcional).
     * @param pageable Objeto de paginação e ordenação do Spring Data.
     * @return Uma {@link Page} de {@link BeerStockDTO} que correspondem aos filtros.
     */
    @Transactional(readOnly = true)
    @Override
    public Page<BeerStockDTO> findAllBeer(
            Long categoryId,
            String categoryName,
            String beerDescription,
            Integer minQuantity,
            Integer maxQuantity,
            Pageable pageable) {

        logger.info("SERVICE: Buscando estoque com filtros - CatID: {}, Quantidade Min: {}. Página: {}", 
                    categoryId, minQuantity, pageable.getPageNumber());

        String categorySearch = (categoryName != null && !categoryName.isBlank())
                ? categoryName.trim()
                : null;

        String beerSearch = (beerDescription != null && !beerDescription.isBlank())
                ? beerDescription.trim()
                : null;

        Page<Beer> filteredPage = beerRepository.findAllBeer(
                categoryId,
                categorySearch,
                beerSearch,
                minQuantity,
                maxQuantity,
                pageable
        );
        
        logger.info("SERVICE: Consulta de estoque retornou {} elementos na página {}.", 
                    filteredPage.getNumberOfElements(), pageable.getPageNumber());

        return filteredPage.map(BeerStockDTO::new);
    }
    

    /**
     * Busca uma cerveja com informações de estoque pelo seu identificador único.
     *
     * @param id O ID da cerveja.
     * @return O {@link BeerStockDTO} correspondente.
     * @throws ResourceNotFoundException Se o ID não for encontrado.
     */
    @Transactional(readOnly = true)
    @Override
    public BeerStockDTO findById(Long id) {
        logger.info("SERVICE: Buscando estoque da cerveja pelo ID: {}", id);
        Beer entity = beerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("SERVICE WARN: Cerveja ID {} não encontrada.", id);
                    return new ResourceNotFoundException("Cerveja não encontrada com ID: " + id);
                });
        
        logger.info("SERVICE: Estoque da cerveja ID {} encontrado. Quantidade: {}", id, entity.getStock().getQuantity());
        return new BeerStockDTO(entity);
    }


    /**
     * Gera um relatório de todas as cervejas cujo prazo de validade (expirationDate)
     * é anterior ou igual à data de referência fornecida.
     *
     * @param referenceDate A data limite para comparação (geralmente {@code LocalDate.now()}).
     * @return Uma {@link List} de {@link BeerStockDTO} que estão vencidas.
     */
    @Transactional(readOnly = true)
    @Override
    public List<BeerStockDTO> getExpiredBeersReport(LocalDate referenceDate) {
        logger.info("SERVICE: Gerando relatório de cervejas vencidas antes de: {}", referenceDate);
        
        // O método findExpiredBeersBefore no repositório faz a busca
        List<Beer> expiredBeers = beerRepository.findExpiredBeersBefore(referenceDate);

        logger.info("SERVICE: Relatório de cervejas vencidas concluído. Total de itens: {}", expiredBeers.size());
        
        return expiredBeers.stream()
                .map(BeerStockDTO::new)
                .collect(Collectors.toList());
    }


    /**
     * Executa uma consulta de estoque avançada usando uma função de banco de dados PL/pgSQL
     * e retorna o resultado paginado em formato de lista.
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
    @Override
    @Transactional(readOnly = true)
    public List<BeerStockDTO> findUsingPlpgsqlFunction(
            Long beerId,
            String beerDescription,
            Integer minQuantity,
            Integer maxQuantity,
            Integer daysUntilExpiry,
            Integer pageSize,
            Integer pageNumber) {
        
        logger.info("SERVICE: Executando função PL/pgSQL com filtros - DaysUntilExpiry: {}, Page: {}", 
                    daysUntilExpiry, pageNumber);

        String beerSearch = (beerDescription != null && !beerDescription.isBlank())
                ? beerDescription.trim()
                : null;

        // O método no repositório é responsável por chamar a função do banco
        List<BeerStockDTO> result = beerRepository.findBeersUsingPlpgsqlFunction(
                beerId,
                beerSearch,
                minQuantity,
                maxQuantity,
                daysUntilExpiry,
                pageSize,
                pageNumber
        );
        
        logger.info("SERVICE: Função PL/pgSQL retornou {} itens.", result.size());
        
        return result;
    }
}