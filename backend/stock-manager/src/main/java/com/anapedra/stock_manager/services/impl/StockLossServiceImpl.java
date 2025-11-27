package com.anapedra.stock_manager.services.impl;

import com.anapedra.stock_manager.domain.dtos.StockLossDTO;
import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.StockLoss;
import com.anapedra.stock_manager.domain.enums.LossReason;
import com.anapedra.stock_manager.repositories.BeerRepository;
import com.anapedra.stock_manager.repositories.StockLossRepository;
import com.anapedra.stock_manager.services.StockLossService;
import com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class StockLossServiceImpl implements StockLossService {

    private final BeerRepository beerRepository;
    private final StockLossRepository stockLossRepository;

    public StockLossServiceImpl(BeerRepository beerRepository, StockLossRepository stockLossRepository) {
        this.beerRepository = beerRepository;
        this.stockLossRepository = stockLossRepository;
    }

    @Transactional
    @Override
    public StockLossDTO registerLoss(StockLossDTO dto) {

        Beer beer = beerRepository.findById(dto.getBeerId())
            .orElseThrow(() -> new ResourceNotFoundException("Cerveja não encontrada. ID: " + dto.getBeerId()));


        Integer currentStock = beer.getStock().getQuantity();
        if (dto.getQuantityLost() > currentStock) {
            throw new IllegalArgumentException("Quantidade perdida (" + dto.getQuantityLost() + ") é maior que o estoque atual (" + currentStock + ").");
        }
        

        LossReason reason = LossReason.valueOf(dto.hashCode());
        
        StockLoss entity = new StockLoss(
            null,                       
            beer,                       
            dto.getQuantityLost(),      
            reason,                     
            dto.getLossDate(),          
            dto.getDescription()        
        );


        entity.getUpdateStock();
        entity = stockLossRepository.save(entity);
        return new StockLossDTO(entity);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<StockLossDTO> findLossesByFilters(
            Integer reasonCode,
            Long beerId,
            String beerName,
            Long categoryId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        String beerSearch = (beerName != null && !beerName.trim().isEmpty())
                ? beerName.trim()
                : null;


        Page<StockLoss> entityPage = stockLossRepository.findLossesByFilters(
                reasonCode,
                beerId,
                beerSearch,
                categoryId,
                startDate,
                endDate,
                pageable
        );

        // Mapeia a página de Entidades para uma página de DTOs de Saída
        return entityPage.map(StockLossDTO::new);
    }
}