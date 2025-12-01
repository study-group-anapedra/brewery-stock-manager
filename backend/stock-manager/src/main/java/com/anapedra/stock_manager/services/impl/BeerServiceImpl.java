package com.anapedra.stock_manager.services.impl;

import com.anapedra.stock_manager.domain.dtos.BeerFilterDTO;
import com.anapedra.stock_manager.domain.dtos.BeerInsertDTO;
import com.anapedra.stock_manager.domain.dtos.CategoryDTO;
import com.anapedra.stock_manager.domain.dtos.StockInputDTO;
import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.Category;
import com.anapedra.stock_manager.domain.entities.Stock;
import com.anapedra.stock_manager.repositories.BeerRepository;
import com.anapedra.stock_manager.repositories.CategoryRepository;
import com.anapedra.stock_manager.services.BeerService;
import com.anapedra.stock_manager.services.exceptions.DatabaseException;
import com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;
    private final CategoryRepository categoryRepository;
    public BeerServiceImpl(BeerRepository beerRepository, CategoryRepository categoryRepository) {
        this.beerRepository = beerRepository;
        this.categoryRepository = categoryRepository;
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
        Beer beer = new Beer();
        copyInsertDtoToEntity(dto, beer);
        beer = beerRepository.save(beer);
        return new BeerInsertDTO(beer);
    }



    @Override
    @Transactional
    public BeerInsertDTO update(Long id, BeerInsertDTO dto) {
        Beer beer = beerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cerveja não encontrada"));
        copyInsertDtoToEntity(dto, beer);
        beer = beerRepository.save(beer);
        return new BeerInsertDTO(beer);
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
                Stock stock = new Stock(stockDTO.getQuantity(), beer);
                beer.setStock(stock);


        beer.getCategories().clear();
        if (dto.getCategories() != null && !dto.getCategories().isEmpty()) {
            Set<Long> categoryIds = dto.getCategories().stream()
                    .map(CategoryDTO::getId)
                    .collect(Collectors.toSet());
            List<Category> categories = categoryRepository.findAllById(categoryIds);
            beer.getCategories().addAll(categories);
        }
    }

}
