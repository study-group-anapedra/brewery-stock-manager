package com.anapedra.stock_manager.services.impl;

import com.anapedra.stock_manager.domain.dtos.BeerFilterDTO;
import com.anapedra.stock_manager.domain.dtos.BeerInsertDTO;
import com.anapedra.stock_manager.domain.dtos.StockInputDTO;
import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.Category;
import com.anapedra.stock_manager.domain.entities.Stock;
import com.anapedra.stock_manager.repositories.BeerRepository;
import com.anapedra.stock_manager.repositories.CategoryRepository;
import com.anapedra.stock_manager.repositories.StockRepository;
import com.anapedra.stock_manager.services.BeerService;
import com.anapedra.stock_manager.services.exceptions.DatabaseException;
import com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;
    private final CategoryRepository categoryRepository;
    private final StockRepository stockRepository;
    private final Timer beerCreationUpdateTimer; // Timer para criação e atualização

    public BeerServiceImpl(
            BeerRepository beerRepository,
            CategoryRepository categoryRepository,
            StockRepository stockRepository,
            MeterRegistry registry
    ) {
        this.beerRepository = beerRepository;
        this.categoryRepository = categoryRepository;
        this.stockRepository = stockRepository;
        this.beerCreationUpdateTimer = Timer.builder("stock_manager.beer.creation_update_time")
                .description("Tempo de execução da criação ou atualização de cervejas")
                .register(registry);
    }


    @Transactional(readOnly = true)
    @Override
    public Page<BeerFilterDTO> findAllBeer(
            Long categoryId,
            String categoryDescription,
            String beerDescription,
            Integer minQuantity,
            Integer maxQuantity,
            Pageable pageable) {
        String categorySearch = (categoryDescription != null && !categoryDescription.trim().isEmpty())
                ? categoryDescription.trim()
                : null;
        String beerSearch = (beerDescription != null && !beerDescription.trim().isEmpty())
                ? beerDescription.trim()
                : null;
        Page<Beer> filteredPage = beerRepository.findAllBeer(
                categoryId,
                categorySearch,
                beerSearch,
                minQuantity,
                maxQuantity,
                pageable
        );
        Page<Beer> finalPage = filteredPage.isEmpty()
                ? beerRepository.findAll(pageable)
                : filteredPage;
        return finalPage.map(BeerFilterDTO::new);
    }

    @Transactional(readOnly = true)
    @Override
    public BeerFilterDTO findById(Long id) {
        Beer entity = beerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cerveja (Entity) não encontrada com ID: " + id));
        return new BeerFilterDTO(entity);
    }


    @Override
    @Transactional
    public BeerInsertDTO insert(BeerInsertDTO dto) {
        return beerCreationUpdateTimer.record(() -> {
            Beer beer = new Beer();
            copyInsertDtoToEntity(dto, beer);
            Beer savedBeer = beerRepository.save(beer);
            return new BeerInsertDTO(savedBeer);
        });
    }


    @Override
    @Transactional
    public BeerInsertDTO update(Long id, BeerInsertDTO dto) {
        return beerCreationUpdateTimer.record(() -> {
            Beer beer = beerRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Cerveja não encontrada"));
            copyInsertDtoToEntity(dto, beer);
            Beer savedBeer = beerRepository.save(beer);
            return new BeerInsertDTO(savedBeer);
        });
    }


    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!beerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try {
            beerRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }


    private void copyInsertDtoToEntity(BeerInsertDTO dto, Beer beer) {
        beer.setName(dto.getName());
        beer.setUrlImg(dto.getUrlImg());
        beer.setAlcoholContent(dto.getAlcoholContent());
        beer.setPrice(dto.getPrice());
        beer.setManufactureDate(dto.getManufactureDate());
        beer.setExpirationDate(dto.getExpirationDate());


        StockInputDTO stockDTO = dto.getStock();
        Stock stock = (beer.getStock() != null) ? beer.getStock() : new Stock();
        stock.setQuantity(stockDTO.getQuantity());
        stock.setBeer(beer);
        
        beer.setStock(stock);
        stockRepository.save(stock);


        beer.getCategories().clear();
        dto.getCategories().forEach(catDto -> {
            Category category = categoryRepository.findById(catDto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
            beer.getCategories().add(category);
        });


    }

}