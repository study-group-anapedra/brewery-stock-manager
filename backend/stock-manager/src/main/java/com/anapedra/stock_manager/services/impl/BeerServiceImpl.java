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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BeerServiceImpl implements BeerService {

    private static final Logger logger = LoggerFactory.getLogger(BeerServiceImpl.class);

    private final BeerRepository beerRepository;
    private final CategoryRepository categoryRepository;
    private final StockRepository stockRepository;
    private final Timer beerCreationUpdateTimer;

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

        logger.info("SERVICE: Buscando cervejas com filtros - CatID: {}, Desc: '{}', MinQ: {}, MaxQ: {}. Página: {}",
                categoryId, categoryDescription, minQuantity, maxQuantity, pageable.getPageNumber());

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

        logger.info("SERVICE: Consulta de cervejas retornou {} elementos na página {}.",
                filteredPage.getNumberOfElements(), pageable.getPageNumber());

        return filteredPage.map(BeerFilterDTO::new);
    }

//    @Transactional(readOnly = true)
//    @Override
//    public Page<BeerFilterDTO> findAllBeer(
//            Long categoryId,
//            String categoryDescription,
//            String beerDescription,
//            Integer minQuantity,
//            Integer maxQuantity,
//            Pageable pageable) {
//        String categorySearch = (categoryDescription != null && !categoryDescription.trim().isEmpty())
//                ? categoryDescription.trim()
//                : null;
//        String beerSearch = (beerDescription != null && !beerDescription.trim().isEmpty())
//                ? beerDescription.trim()
//                : null;
//        Page<Beer> filteredPage = beerRepository.findAllBeer(
//                categoryId,
//                categorySearch,
//                beerSearch,
//                minQuantity,
//                maxQuantity,
//                pageable
//        );
//
//        Page<Beer> finalPage = filteredPage.isEmpty()
//                ? beerRepository.findAll(pageable)
//                : filteredPage;
//        return finalPage.map(BeerFilterDTO::new);
//
//    }

    @Transactional(readOnly = true)
    @Override
    public BeerFilterDTO findById(Long id) {
        logger.info("SERVICE: Buscando cerveja pelo ID: {}", id);
        Beer entity = beerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("SERVICE WARN: Cerveja não encontrada com ID: {}", id);
                    return new ResourceNotFoundException("Cerveja (Entity) não encontrada com ID: " + id);
                });
        logger.info("SERVICE: Cerveja ID {} encontrada.", id);
        return new BeerFilterDTO(entity);
    }

//    @Transactional(readOnly = true)
//    @Override
//    public BeerFilterDTO findById(Long id) {
//        Beer entity = beerRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Cerveja (Entity) não encontrada com ID: " + id));
//        return new BeerFilterDTO(entity);
//    }


//    @Override
//    @Transactional
//    public BeerInsertDTO insert(BeerInsertDTO dto) {
//        logger.info("SERVICE: Iniciando inserção da nova cerveja: {}", dto.getName());
//        return beerCreationUpdateTimer.record(() -> {
//            Beer beer = new Beer();
//            copyInsertDtoToEntity(dto, beer);
//            Beer savedBeer = beerRepository.save(beer);
//            logger.info("SERVICE: Cerveja ID {} salva com sucesso.", savedBeer.getId());
//            return new BeerInsertDTO(savedBeer);
//        });
//    }

    @Override
    @Transactional
    public BeerInsertDTO insert(BeerInsertDTO dto) {
            logger.info("SERVICE: Iniciando inserção da nova cerveja: {}", dto.getName());

         return beerCreationUpdateTimer.record(() -> {
            Beer beer = new Beer();
            copyInsertDtoToEntity(dto, beer);
            Beer savedBeer = beerRepository.save(beer);
                logger.info("SERVICE: Cerveja ID {} salva com sucesso.", savedBeer.getId());

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
            logger.info("SERVICE: Cerveja ID {} excluída com sucesso.", id);
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try {
            beerRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            logger.error("SERVICE ERROR: Falha de integridade ao excluir cerveja ID {}.", id, e);
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
        logger.debug("SERVICE: Estoque para cerveja '{}' mapeado e salvo com quantidade: {}", beer.getName(), stock.getQuantity());


        beer.getCategories().clear();
        dto.getCategories().forEach(catDto -> {
            Category category = categoryRepository.findById(catDto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
            logger.warn("SERVICE WARN: Categoria ID {} não encontrada durante mapeamento.", catDto.getId());
            beer.getCategories().add(category);
            logger.debug("SERVICE: Categoria ID {} adicionada à cerveja.", category.getId());
        });


    }


}