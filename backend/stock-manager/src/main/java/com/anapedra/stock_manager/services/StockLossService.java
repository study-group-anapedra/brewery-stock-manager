package com.anapedra.stock_manager.services;

import com.anapedra.stock_manager.domain.dtos.StockLossDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface StockLossService {

    /**
     * Registra uma perda de estoque e debita a quantidade do estoque da cerveja de forma transacional.
     */
    StockLossDTO registerLoss(StockLossDTO dto);

    /**
     * Busca registros de perda com filtros flexíveis (Enum, Datas, Beer, Categoria).
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