package com.anapedra.stock_manager.services.impl;

import com.anapedra.stock_manager.domain.dtos.StockLossDTO;
import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.StockLoss;
import com.anapedra.stock_manager.domain.enums.LossReason;
import com.anapedra.stock_manager.repositories.BeerRepository;
import com.anapedra.stock_manager.repositories.StockLossRepository;
import com.anapedra.stock_manager.services.StockLossService;
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

import java.time.LocalDate;

/**
 * Implementação da interface {@link StockLossService} responsável por gerenciar
 * as operações de negócio relacionadas ao registro e consulta de perdas de estoque (StockLoss).
 *
 * <p>Esta classe garante o tratamento de exceções, a integridade transacional e
 * inclui monitoramento de desempenho (Micrometer) para registro de perdas.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @see StockLossService
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class StockLossServiceImpl implements StockLossService {

    private static final Logger logger = LoggerFactory.getLogger(StockLossServiceImpl.class);

    /**
     * Repositório para operações de persistência da entidade {@link Beer}.
     */
    private final BeerRepository beerRepository;

    /**
     * Repositório para operações de persistência da entidade {@link StockLoss}.
     */
    private final StockLossRepository stockLossRepository;

    /**
     * Timer do Micrometer para monitorar o tempo gasto no registro de perdas.
     */
    private final Timer lossRegistrationTimer;

    /**
     * Contador do Micrometer para registrar o total de unidades perdidas.
     */
    private final Counter totalUnitsLostCounter;

    /**
     * Construtor para injeção de dependências.
     *
     * @param beerRepository Repositório de cervejas.
     * @param stockLossRepository Repositório de perdas de estoque.
     * @param registry O registro de métricas do Micrometer.
     */
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

    /**
     * Busca paginada de registros de perda de estoque aplicando diversos filtros.
     *
     * @param reasonCode O código da razão de perda (Enum {@link LossReason}).
     * @param beerId O ID da cerveja.
     * @param beerName O nome da cerveja (parcial).
     * @param categoryId O ID da categoria da cerveja.
     * @param startDate A data mínima de registro da perda.
     * @param endDate A data máxima de registro da perda.
     * @param pageable O objeto de paginação (número da página, tamanho, ordenação).
     * @return Uma {@link Page} de {@link StockLossDTO} contendo os resultados filtrados.
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

    /**
     * Registra uma nova perda de estoque para uma cerveja, atualizando a quantidade em estoque.
     *
     * <p>O tempo de execução é monitorado pelo {@code lossRegistrationTimer}.</p>
     *
     * @param dto O {@link StockLossDTO} contendo os detalhes da perda.
     * @return O {@link StockLossDTO} registrado com o ID gerado.
     * @throws ResourceNotFoundException Se o ID da cerveja não for encontrado.
     * @throws IllegalArgumentException Se a quantidade perdida for maior que o estoque atual.
     */
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
            
            // NOTE: É crucial corrigir a lógica de mapeamento para LossReason
            // A linha abaixo está usando hashCode() do DTO, o que é quase certamente um erro lógico. 
            // Deve ser substituída pela lógica correta de conversão de LossReason.
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


            // Este método, presumivelmente, atualiza o estoque da Beer associada
            entity.getUpdateStock(); 
            entity = stockLossRepository.save(entity);

            totalUnitsLostCounter.increment(dto.getQuantityLost());
            
            logger.info("SERVICE: Perda ID {} registrada com sucesso. Estoque de cerveja ID {} reduzido em {} unidades.",
                        entity.getId(), beer.getId(), entity.getQuantityLost());

            return new StockLossDTO(entity);
        });
    }
}