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

@Service
public class StockServiceImpl implements StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockServiceImpl.class);

    private final BeerRepository beerRepository;

    public StockServiceImpl(BeerRepository beerRepository) {
        this.beerRepository = beerRepository;
    }


    @Transactional(readOnly = true)
    @Override
    public Page<BeerStockDTO> findAllBeer(
            Long categoryId,
            String categoryDescription,
            String beerDescription,
            Integer minQuantity,
            Integer maxQuantity,
            Pageable pageable) {

        logger.info("SERVICE: Buscando estoque com filtros - CatID: {}, Quantidade Min: {}. Página: {}", 
                    categoryId, minQuantity, pageable.getPageNumber());

        String categorySearch = (categoryDescription != null && !categoryDescription.isBlank())
                ? categoryDescription.trim()
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


    @Transactional(readOnly = true)
    @Override
    public List<BeerStockDTO> getExpiredBeersReport(LocalDate referenceDate) {
        logger.info("SERVICE: Gerando relatório de cervejas vencidas antes de: {}", referenceDate);
        
        List<Beer> expiredBeers = beerRepository.findExpiredBeersBefore(referenceDate);

        logger.info("SERVICE: Relatório de cervejas vencidas concluído. Total de itens: {}", expiredBeers.size());
        
        return expiredBeers.stream()
                .map(BeerStockDTO::new)
                .collect(Collectors.toList());
    }


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