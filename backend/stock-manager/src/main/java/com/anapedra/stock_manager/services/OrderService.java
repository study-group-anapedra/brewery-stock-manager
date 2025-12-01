package com.anapedra.stock_manager.services;

import com.anapedra.stock_manager.domain.dtos.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderDTO save(OrderDTO dto);

    OrderDTO findById(Long id);

    Page<OrderDTO> findAll(Pageable pageable);

    OrderDTO update(Long id, OrderDTO dto);

    void delete(Long id);

    Page<OrderDTO> find(
            Long clientId,
            String nameClient,
            String cpfClient,
            String minInstant,
            String maxInstant,
            Pageable pageable
    );
}