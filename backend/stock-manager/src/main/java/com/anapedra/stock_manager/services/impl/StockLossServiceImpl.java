package com.anapedra.stock_manager.services.impl;

import com.anapedra.stock_manager.domain.dtos.StockLossDTO;
import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.StockLoss;
import com.anapedra.stock_manager.domain.enums.LossReason;
import com.anapedra.stock_manager.repositories.BeerRepository;
import com.anapedra.stock_manager.repositories.StockLossRepository;
import com.anapedra.stock_manager.services.StockLossService;
import com.anapedra.stock_manager.services.exceptions.InsufficientStockException;
import com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Implementação da interface {@link StockLossService} responsável por gerenciar
 * as operações de negócio relacionadas ao registro e consulta de perdas de estoque.
 *
 * <p>
 * Esta classe:
 * <ul>
 *   <li>Centraliza regras de negócio</li>
 *   <li>Garante consistência transacional</li>
 *   <li>Atualiza o estoque de forma segura</li>
 *   <li>Aplica observabilidade via Micrometer</li>
 * </ul>
 * </p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class StockLossServiceImpl implements StockLossService {

    private static final Logger logger = LoggerFactory.getLogger(StockLossServiceImpl.class);

    private final BeerRepository beerRepository;
    private final StockLossRepository stockLossRepository;

    /** Métrica de tempo para registro de perdas */
    private final Timer lossRegistrationTimer;

    /** Métrica de volume total de unidades perdidas */
    private final Counter totalUnitsLostCounter;

    public StockLossServiceImpl(
            BeerRepository beerRepository,
            StockLossRepository stockLossRepository,
            MeterRegistry registry) {

        this.beerRepository = beerRepository;
        this.stockLossRepository = stockLossRepository;

        this.lossRegistrationTimer = Timer.builder("stock_manager.stock_loss.registration_time")
                .description("Tempo de execução do registro de perda de estoque")
                .register(registry);

        this.totalUnitsLostCounter = Counter.builder("stock_manager.stock_loss.total_units_lost")
                .description("Total de unidades registradas como perda")
                .register(registry);
    }

    /**
     * Busca paginada de perdas de estoque com filtros opcionais.
     */
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

        logger.info("SERVICE: Buscando perdas de estoque. Beer ID: {}, Reason: {}", beerId, reasonCode);

        String beerSearch = (beerName != null && !beerName.trim().isEmpty())
                ? beerName.trim()
                : null;

        Page<StockLoss> page = stockLossRepository.findLossesByFilters(
                reasonCode,
                beerId,
                beerSearch,
                categoryId,
                startDate,
                endDate,
                pageable
        );

        logger.info("SERVICE: Consulta retornou {} registros.", page.getTotalElements());

        return page.map(StockLossDTO::new);
    }

    /**
     * Registra uma nova perda de estoque e atualiza o estoque da cerveja.
     */
    @Transactional
    @Override
    public StockLossDTO registerLoss(StockLossDTO dto) {

        logger.info("SERVICE: Iniciando registro de perda. Beer ID: {}, Quantity: {}",
                dto.getBeerId(), dto.getQuantityLost());

        return lossRegistrationTimer.record(() -> {
            StockLoss entity = new StockLoss();
            copyDtoToEntity(dto, entity);
            entity = stockLossRepository.save(entity);
            return new StockLossDTO(entity);
        });
    }

    /**
     * Mapeia dados do DTO para a entidade aplicando regras de negócio
     * e validações de estoque.
     */
    private void copyDtoToEntity(StockLossDTO dto, StockLoss entity) {

        if (dto.getBeerId() == null) {
            throw new IllegalArgumentException("Beer ID must not be null.");
        }

        Beer beer = beerRepository.findById(dto.getBeerId())
                .orElseThrow(() -> {
                    logger.warn("SERVICE WARN: Cerveja não encontrada. ID: {}", dto.getBeerId());
                    return new ResourceNotFoundException(
                            "Cerveja não encontrada. ID: " + dto.getBeerId()
                    );
                });

        if (dto.getQuantityLost() == null || dto.getQuantityLost() <= 0) {
            throw new IllegalArgumentException("Quantity lost must be greater than zero.");
        }

        int currentStock = beer.getStock().getQuantity();

        if (dto.getQuantityLost() > currentStock) {
            logger.error(
                    "SERVICE ERROR: Estoque insuficiente. Beer ID: {}, Disponível: {}, Solicitado: {}",
                    beer.getId(), currentStock, dto.getQuantityLost()
            );

            throw new InsufficientStockException(
                    "Quantidade insuficiente em estoque para a cerveja: " + beer.getName() +
                    ". Disponível: " + currentStock +
                    ", Solicitado: " + dto.getQuantityLost()
            );
        }

        entity.setBeer(beer);
        entity.setQuantityLost(dto.getQuantityLost());
        entity.setLossReason(
                dto.getReason() != null ? dto.getReason() : LossReason.OTHER
        );
        entity.setLossDate(
                dto.getLossDate() != null ? dto.getLossDate() : LocalDate.now()
        );
        entity.setDescription(dto.getDescription());
        entity.setRegistrationMoment(Instant.now());

        // Atualização do estoque encapsulada na entidade
        entity.getBeer().getStock().setQuantity(entity.getBeer().getStock().getQuantity() - entity.getQuantityLost());

        totalUnitsLostCounter.increment(dto.getQuantityLost());

        logger.info(
                "SERVICE: Perda registrada com sucesso. Beer ID: {}, Redução: {} unidades.",
                beer.getId(), dto.getQuantityLost()
        );
    }
}
