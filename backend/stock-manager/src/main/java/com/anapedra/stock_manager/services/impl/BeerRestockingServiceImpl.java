package com.anapedra.stock_manager.services.impl;

import com.anapedra.stock_manager.domain.dtos.BeerRestockingDTO;
import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.BeerRestocking;
import com.anapedra.stock_manager.repositories.BeerRepository;
import com.anapedra.stock_manager.repositories.BeerRestockingRepository;
import com.anapedra.stock_manager.services.BeerRestockingService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BeerRestockingServiceImpl implements BeerRestockingService {

    private final BeerRestockingRepository bookRestockingRepository;
    private final BeerRepository bookRepository;
    private final Timer restockingTimer;

    public BeerRestockingServiceImpl(BeerRestockingRepository bookRestockingRepository, BeerRepository bookRepository, MeterRegistry registry) {
        this.bookRestockingRepository = bookRestockingRepository;
        this.bookRepository = bookRepository;
        this.restockingTimer = Timer.builder("stock_manager.restocking.creation_time")
                .description("Tempo de execução da criação de reabastecimento")
                .register(registry);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BeerRestockingDTO> findAll() {
        List<BeerRestocking> list = bookRestockingRepository.findAll();
        return list.stream().map(BeerRestockingDTO::new).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BeerRestockingDTO findById(Long id) {
        BeerRestocking entity = bookRestockingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book restocking not found"));
        return new BeerRestockingDTO(entity);
    }
    @Override
    @Transactional
    public BeerRestockingDTO create(BeerRestockingDTO dto) {
        return restockingTimer.record(() -> {
            BeerRestocking entity = new BeerRestocking();
            copyDtoToEntity(dto, entity);
            entity.getBeer().getStock().setQuantity(entity.getBeer().getStock().getQuantity() + entity.getQuantity());
            entity.setMoment(Instant.now());
            BeerRestocking savedEntity = bookRestockingRepository.save(entity);
            return new BeerRestockingDTO(savedEntity);
        });
    }

    @Override
    @Transactional
    public BeerRestockingDTO update(Long id, BeerRestockingDTO dto) {
        BeerRestocking entity = bookRestockingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book restocking not found"));
        copyDtoToEntity(dto, entity);
        entity = bookRestockingRepository.save(entity);
        return new BeerRestockingDTO(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!bookRestockingRepository.existsById(id)) {
            throw new RuntimeException("Book restocking not found");
        }
        bookRestockingRepository.deleteById(id);
    }

    private void copyDtoToEntity(BeerRestockingDTO dto, BeerRestocking entity) {
        entity.setQuantity(dto.getQuantity());

        Beer beer = bookRepository.findById(dto.getBeerId())
                .orElseThrow(() -> new RuntimeException("Book not found"));
        entity.setBeer(beer);

        beer.setStock(beer.getStock());
    }


}

