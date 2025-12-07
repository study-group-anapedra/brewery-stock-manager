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
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

@Service
public class StockLossServiceImpl implements StockLossService {

    private static final Logger logger = LoggerFactory.getLogger(StockLossServiceImpl.class);

    private final BeerRepository beerRepository;
    private final StockLossRepository stockLossRepository;
    private final Timer lossRegistrationTimer;
    private final Counter totalUnitsLostCounter;

    public StockLossServiceImpl(BeerRepository beerRepository, StockLossRepository stockLossRepository, MeterRegistry registry) {
        this.beerRepository = beerRepository;
        this.stockLossRepository = stockLossRepository;
        this.lossRegistrationTimer = Timer.builder("stock_manager.stock_loss.registration_time")
                .description("Tempo de execução do registro de perda de estoque")
                .register(registry);
        this.totalUnitsLostCounter = Counter.builder("stock_manager.stock_loss.total_units_lost")
                .description("Total de unidades de cerveja registradas como perda")
                .register(registry);
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

        logger.info("SERVICE: Buscando perdas de estoque com filtros. Cerveja ID: {}, Razão: {}", beerId, reasonCode);

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
        
        logger.info("SERVICE: Consulta de perdas retornou {} elementos na página {}.", 
                    entityPage.getNumberOfElements(), pageable.getPageNumber());

        return entityPage.map(StockLossDTO::new);
    }


    @Transactional
    @Override
    public StockLossDTO registerLoss(StockLossDTO dto) {
        logger.info("SERVICE: Iniciando registro de perda de estoque. Cerveja ID: {}, Quantidade: {}", 
                    dto.getBeerId(), dto.getQuantityLost());
                    
        return lossRegistrationTimer.record(() -> {
            Beer beer = beerRepository.findById(dto.getBeerId())
                    .orElseThrow(() -> {
                        logger.warn("SERVICE WARN: Cerveja não encontrada ID: {} para registro de perda.", dto.getBeerId());
                        return new ResourceNotFoundException("Cerveja não encontrada. ID: " + dto.getBeerId());
                    });


            Integer currentStock = beer.getStock().getQuantity();
            if (dto.getQuantityLost() > currentStock) {
                logger.error("SERVICE ERROR: Estoque insuficiente. Cerveja ID: {}. Disponível: {}, Solicitado: {}",
                             beer.getId(), currentStock, dto.getQuantityLost());
                throw new IllegalArgumentException("Quantidade perdida (" + dto.getQuantityLost() + ") é maior que o estoque atual (" + currentStock + ").");
            }
            
            LossReason reason = LossReason.valueOf(dto.hashCode());
            logger.warn("SERVICE WARN: Usando hashCode() do DTO para LossReason. Verifique a lógica de mapeamento de enum.");


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

            totalUnitsLostCounter.increment(dto.getQuantityLost());
            
            logger.info("SERVICE: Perda ID {} registrada com sucesso. Estoque de cerveja ID {} reduzido em {} unidades.",
                        entity.getId(), beer.getId(), entity.getQuantityLost());

            return new StockLossDTO(entity);
        });
    }


}