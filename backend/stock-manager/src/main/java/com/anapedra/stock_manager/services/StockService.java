package com.anapedra.stock_manager.services;

import com.anapedra.stock_manager.domain.dtos.BeerStockDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface StockService {

    Page<BeerStockDTO> findAllBeer(
        Long categoryId,
        String categoryDescription,
        String beerDescription,
        Integer minQuantity,
        Integer maxQuantity,
        Pageable pageable
    );

    BeerStockDTO findById(Long id);

    List<BeerStockDTO> getExpiredBeersReport(LocalDate referenceDate);

    List<BeerStockDTO> findUsingPlpgsqlFunction(
            Long beerId,
            String beerDescription,
            Integer minQuantity,
            Integer maxQuantity,
            Integer daysUntilExpiry,
            Integer pageSize,
            Integer pageNumber
    );

}
