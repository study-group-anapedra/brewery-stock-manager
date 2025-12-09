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

/**
 * Implementação da interface {@link BeerService} que gerencia as operações de negócio
 * relacionadas à entidade Cerveja (Beer).
 *
 * <p>Esta classe lida com a lógica de busca paginada com filtros complexos,
 * operações CRUD e a manipulação do relacionamento com {@link Category} e {@link Stock}.
 * Inclui monitoramento de desempenho usando Micrometer ({@link Timer}).</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @see BeerService
 * @see Beer
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class BeerServiceImpl implements BeerService {

    private static final Logger logger = LoggerFactory.getLogger(BeerServiceImpl.class);

    private final BeerRepository beerRepository;
    private final CategoryRepository categoryRepository;
    private final StockRepository stockRepository;
    private final Timer beerCreationUpdateTimer;

    /**
     * Construtor para injeção de dependências.
     *
     * @param beerRepository Repositório de cervejas.
     * @param categoryRepository Repositório de categorias.
     * @param stockRepository Repositório de estoque.
     * @param registry O registro de métricas do Micrometer.
     */
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

    /**
     * Busca cervejas paginadas aplicando filtros dinâmicos.
     *
     * @param categoryId ID da categoria (opcional).
     * @param categoryDescription Descrição da categoria (opcional, busca parcial).
     * @param beerDescription Nome/descrição da cerveja (opcional, busca parcial).
     * @param minQuantity Quantidade mínima em estoque (opcional).
     * @param maxQuantity Quantidade máxima em estoque (opcional).
     * @param pageable Objeto de paginação e ordenação do Spring Data.
     * @return Uma {@link Page} de {@link BeerFilterDTO} que correspondem aos filtros.
     */
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

    /**
     * Busca uma cerveja pelo seu identificador único.
     *
     * @param id O ID da cerveja.
     * @return O {@link BeerFilterDTO} correspondente.
     * @throws ResourceNotFoundException Se o ID não for encontrado.
     */
    @Transactional(readOnly = true)
    @Override
    public BeerFilterDTO findById(Long id) {
        logger.info("SERVICE: Buscando cerveja pelo ID: {}", id);
        Beer entity = beerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("SERVICE WARN: Cerveja não encontrada com ID: {}", id);
                    throw new ResourceNotFoundException("Cerveja (Entity) não encontrada com ID: " + id);
                });
        logger.info("SERVICE: Cerveja ID {} encontrada.", id);
        return new BeerFilterDTO(entity);
    }

    /**
     * Insere e persiste um novo registro de cerveja, incluindo o estoque inicial.
     *
     * <p>O tempo de execução desta operação é monitorado pelo {@code beerCreationUpdateTimer}.</p>
     *
     * @param dto O {@link BeerInsertDTO} com os dados para criação.
     * @return O {@link BeerInsertDTO} criado.
     * @throws ResourceNotFoundException Se alguma categoria referenciada não for encontrada.
     */
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


    /**
     * Atualiza um registro de cerveja existente, incluindo suas categorias e estoque.
     *
     * <p>O tempo de execução desta operação é monitorado pelo {@code beerCreationUpdateTimer}.</p>
     *
     * @param id O ID do registro a ser atualizado.
     * @param dto O {@link BeerInsertDTO} com os dados atualizados.
     * @return O {@link BeerInsertDTO} atualizado.
     * @throws ResourceNotFoundException Se o ID da cerveja ou alguma categoria não for encontrada.
     */
    @Override
    @Transactional
    public BeerInsertDTO update(Long id, BeerInsertDTO dto) {
        logger.info("SERVICE: Iniciando atualização da cerveja ID: {}", id);
        return beerCreationUpdateTimer.record(() -> {
            try {
                // Usa getReferenceById para carregar a entidade por referência e evitar query desnecessária antes do update
                Beer beer = beerRepository.getReferenceById(id);
                copyInsertDtoToEntity(dto, beer);
                Beer savedBeer = beerRepository.save(beer);
                logger.info("SERVICE: Cerveja ID {} atualizada com sucesso.", id);
                return new BeerInsertDTO(savedBeer);
            } catch (jakarta.persistence.EntityNotFoundException e) {
                logger.error("SERVICE ERROR: Cerveja não encontrada para atualização ID: {}", id);
                throw new ResourceNotFoundException("Cerveja não encontrada (ID: " + id + ")");
            }
        });
    }


    /**
     * Exclui um registro de cerveja pelo seu identificador único.
     *
     * <p>Configurado com {@code Propagation.SUPPORTS} para executar em transação,
     * mas não requer uma nova se já houver uma. Lança {@link DatabaseException}
     * em caso de violação de integridade referencial.</p>
     *
     * @param id O ID do registro a ser excluído.
     * @throws ResourceNotFoundException Se o ID não for encontrado.
     * @throws DatabaseException Se houver violação de integridade.
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        logger.warn("SERVICE: Tentativa de exclusão da cerveja ID: {}", id);
        if (!beerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado (ID: " + id + ")");
        }
        try {
            beerRepository.deleteById(id);
            logger.info("SERVICE: Cerveja ID {} excluída com sucesso.", id);
        } catch (DataIntegrityViolationException e) {
            logger.error("SERVICE ERROR: Falha de integridade ao excluir cerveja ID {}. Detalhes: {}", id, e.getMessage());
            throw new DatabaseException("Falha de integridade referencial: Cerveja ID " + id + " está sendo referenciada em outras tabelas.");
        }
    }

    /**
     * Copia os dados de um DTO de inserção/atualização (BeerInsertDTO) para a entidade {@link Beer}.
     *
     * <p>Este método é responsável por mapear os campos básicos, atualizar/criar o {@link Stock}
     * e atualizar o conjunto de {@link Category}s da cerveja.</p>
     *
     * @param dto O {@link BeerInsertDTO} de origem.
     * @param beer A entidade {@link Beer} de destino.
     * @throws ResourceNotFoundException Se alguma categoria referenciada não for encontrada.
     */
    private void copyInsertDtoToEntity(BeerInsertDTO dto, Beer beer) {
        // Mapeamento de campos básicos
        beer.setName(dto.getName());
        beer.setUrlImg(dto.getUrlImg());
        beer.setAlcoholContent(dto.getAlcoholContent());
        beer.setPrice(dto.getPrice());
        beer.setManufactureDate(dto.getManufactureDate());
        beer.setExpirationDate(dto.getExpirationDate());

        // Mapeamento e Persistência de Stock (relacionamento OneToOne)
        StockInputDTO stockDTO = dto.getStock();
        // Reutiliza o objeto Stock existente ou cria um novo
        Stock stock = (beer.getStock() != null) ? beer.getStock() : new Stock();
        stock.setQuantity(stockDTO.getQuantity());
        stock.setBeer(beer); // Garante a referência bidirecional
        beer.setStock(stock);
        // Persiste o Stock explicitamente (necessário se CascadeType não for ALL ou PERSIST)
        stockRepository.save(stock);
        logger.debug("SERVICE: Estoque para cerveja '{}' mapeado e salvo com quantidade: {}", beer.getName(), stock.getQuantity());


        // Mapeamento de Categorias (relacionamento ManyToMany)
        beer.getCategories().clear();
        dto.getCategories().forEach(catDto -> {
            Category category = categoryRepository.findById(catDto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada (ID: " + catDto.getId() + ")"));
            // O logger.warn dentro do forEach não deve ser acionado se a linha acima lançar exceção. Corrigido o log.
            //logger.warn("SERVICE WARN: Categoria ID {} não encontrada durante mapeamento.", catDto.getId());
            beer.getCategories().add(category);
            logger.debug("SERVICE: Categoria ID {} adicionada à cerveja.", category.getId());
        });
    }


}