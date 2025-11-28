package com.anapedra.stock_manager.services;


import com.anapedra.stock_manager.domain.dtos.BeerRestockingDTO;

import java.util.List;

public interface BeerRestockingService {
    List<BeerRestockingDTO> findAll();
    BeerRestockingDTO findById(Long id);
    BeerRestockingDTO create(BeerRestockingDTO dto);
    BeerRestockingDTO update(Long id, BeerRestockingDTO dto);
    void delete(Long id);
}

