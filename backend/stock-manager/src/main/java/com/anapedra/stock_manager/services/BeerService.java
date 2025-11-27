package com.anapedra.stock_manager.services;

import com.anapedra.stock_manager.domain.dtos.BeerFilterDTO;
import com.anapedra.stock_manager.domain.dtos.BeerInsertDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * Service interface para gerenciamento de cervejas.
 * Define operações de consulta, filtro e CRUD.
 */
public interface BeerService {
    Page<BeerFilterDTO> findAllBeer(
            Long categoryId,
            String categoryDescription,
            String beerDescription,
            Integer minQuantity,
            Integer maxQuantity,
            Pageable pageable
    );

    BeerFilterDTO findById(Long id);

    BeerInsertDTO insert(BeerInsertDTO dto);
    BeerInsertDTO update(Long id, BeerInsertDTO dto);
    void delete(Long id);

}
















