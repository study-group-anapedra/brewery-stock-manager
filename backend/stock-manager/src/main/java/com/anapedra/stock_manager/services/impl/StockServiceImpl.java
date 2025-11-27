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

    @Transactional(readOnly = true)
    @Override
    public Page<BeerStockDTO> findAllBeer(
        Long categoryId,
        String categoryDescription, 
        String beerDescription, 
        Integer minQuantity,
        Integer maxQuantity,
        Pageable pageable) {
        String categorySearch = (categoryDescription != null && !categoryDescription.trim().isEmpty())
                ? categoryDescription.trim()
                : null;
        String beerSearch = (beerDescription != null && !beerDescription.trim().isEmpty())
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
        Page<Beer> finalPage = filteredPage.isEmpty() 
            ? beerRepository.findAll(pageable) 
            : filteredPage;
        return finalPage.map(BeerStockDTO::new);
    }

    @Transactional(readOnly = true)
    @Override
    public BeerStockDTO findById(Long id) {
        Beer entity = beerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cerveja (Entity) não encontrada com ID: " + id));
        return new BeerStockDTO(entity);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BeerStockDTO> getExpiredBeersReport(LocalDate referenceDate) {
        List<Beer> expiredBeers = beerRepository.findExpiredBeersBefore(referenceDate);
        return expiredBeers.stream()
                .map(BeerStockDTO::new)
                .collect(Collectors.toList());
    }
}