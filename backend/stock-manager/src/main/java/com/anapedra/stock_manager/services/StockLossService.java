package com.anapedra.stock_manager.services;

import com.anapedra.stock_manager.domain.dtos.StockLossDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

/**
 * Interface de serviço para gerenciar as operações relacionadas à Perda de Estoque (StockLoss).
 *
 * <p>Define o contrato para o registro de perdas (que envolvem a atualização do estoque)
 * e a busca avançada por registros de perda, utilizando o DTO {@link StockLossDTO}
 * para transferência de dados.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @see StockLossDTO
 * @since 0.0.1-SNAPSHOT
 */
public interface StockLossService {

    /**
     * Registra uma perda de estoque e debita a quantidade do estoque da cerveja de forma transacional.
     *
     * <p>Esta é uma operação crítica que envolve a criação do registro de perda e
     * a atualização do estoque da cerveja correspondente.</p>
     *
     * @param dto O {@link StockLossDTO} com os dados da perda a ser registrada.
     * @return O {@link StockLossDTO} registrado e persistido.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se a cerveja não for encontrada.
     * @throws com.anapedra.stock_manager.services.exceptions.BusinessRuleException Se a quantidade em estoque for insuficiente.
     */
    StockLossDTO registerLoss(StockLossDTO dto);

    /**
     * Busca registros de perda paginados, aplicando filtros flexíveis.
     *
     * @param reasonCode O código inteiro do motivo da perda (Enum {@code LossReason}, opcional).
     * @param beerId O ID da cerveja (opcional).
     * @param beerName O nome da cerveja (opcional, busca parcial).
     * @param categoryId O ID da categoria associada à cerveja (opcional).
     * @param startDate A data inicial para o filtro de ocorrência da perda (opcional).
     * @param endDate A data final para o filtro de ocorrência da perda (opcional).
     * @param pageable Objeto de paginação e ordenação do Spring Data.
     * @return Uma {@link Page} de {@link StockLossDTO} que correspondem aos filtros.
     */
    Page<StockLossDTO> findLossesByFilters(
        Integer reasonCode,
        Long beerId,
        String beerName,
        Long categoryId,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );
}