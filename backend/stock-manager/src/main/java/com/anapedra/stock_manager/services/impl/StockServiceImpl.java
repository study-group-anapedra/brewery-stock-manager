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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockServiceImpl implements StockService {

    private final BeerRepository beerRepository;

    public StockServiceImpl(BeerRepository beerRepository) {
        this.beerRepository = beerRepository;
    }

    // ------------------------------------------------------------
    // 1. Filtro com Pageable
    // ------------------------------------------------------------
    @Transactional(readOnly = true)
    @Override
    public Page<BeerStockDTO> findAllBeer(
            Long categoryId,
            String categoryDescription,
            String beerDescription,
            Integer minQuantity,
            Integer maxQuantity,
            Pageable pageable) {

        String categorySearch = (categoryDescription != null && !categoryDescription.isBlank())
                ? categoryDescription.trim()
                : null;

        String beerSearch = (beerDescription != null && !beerDescription.isBlank())
                ? beerDescription.trim()
                : null;

        // Busca filtrada usando a query JPQL
        Page<Beer> filteredPage = beerRepository.findAllBeer(
                categoryId,
                categorySearch,
                beerSearch,
                minQuantity,
                maxQuantity,
                pageable
        );

        return filteredPage.map(BeerStockDTO::new);
    }
    // ------------------------------------------------------------
    // 2. Buscar por ID
    // ------------------------------------------------------------
    @Transactional(readOnly = true)
    @Override
    public BeerStockDTO findById(Long id) {
        Beer entity = beerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cerveja não encontrada com ID: " + id));

        return new BeerStockDTO(entity);
    }

    // ------------------------------------------------------------
    // 3. Relatório de vencidos
    // ------------------------------------------------------------
    @Transactional(readOnly = true)
    @Override
    public List<BeerStockDTO> getExpiredBeersReport(LocalDate referenceDate) {
        List<Beer> expiredBeers = beerRepository.findExpiredBeersBefore(referenceDate);

        return expiredBeers.stream()
                .map(BeerStockDTO::new)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------
    // 4. Consulta PL/pgSQL com 4 parâmetros
    // ------------------------------------------------------------
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

        String beerSearch = (beerDescription != null && !beerDescription.isBlank())
                ? beerDescription.trim()
                : null;

        return beerRepository.findBeersUsingPlpgsqlFunction(
                beerId,
                beerSearch,
                minQuantity,
                maxQuantity,
                daysUntilExpiry,
                pageSize,
                pageNumber
        );
    }


}
