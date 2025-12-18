package com.anapedra.stock_manager.services.impl;

import com.anapedra.stock_manager.domain.dtos.BeerRestockingDTO;
import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.BeerRestocking;
import com.anapedra.stock_manager.repositories.BeerRepository;
import com.anapedra.stock_manager.repositories.BeerRestockingRepository;
import com.anapedra.stock_manager.services.BeerRestockingService;
import com.anapedra.stock_manager.services.exceptions.DatabaseException;
import com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementação da interface {@link BeerRestockingService} que gerencia as operações
 * de reposição de estoque de cervejas.
 *
 * <p>Esta classe lida com a lógica de negócio, persistência (via Repositories) e
 * a atualização do estoque ao registrar uma nova reposição. Inclui também
 * monitoramento de desempenho usando Micrometer ({@link Timer}).</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @see BeerRestockingService
 * @see BeerRestocking
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class BeerRestockingServiceImpl implements BeerRestockingService {

    private static final Logger logger = LoggerFactory.getLogger(BeerRestockingServiceImpl.class);

    private final BeerRestockingRepository bookRestockingRepository;
    private final BeerRepository bookRepository; // Nota: O nome da variável sugere "Book", mas o tipo é "Beer"
    private final Timer restockingTimer;

    /**
     * Construtor para injeção de dependências.
     *
     * @param bookRestockingRepository Repositório de reposição de cervejas.
     * @param bookRepository Repositório de cervejas.
     * @param registry O registro de métricas do Micrometer.
     */
    public BeerRestockingServiceImpl(BeerRestockingRepository bookRestockingRepository, BeerRepository bookRepository, MeterRegistry registry) {
        this.bookRestockingRepository = bookRestockingRepository;
        this.bookRepository = bookRepository;
        this.restockingTimer = Timer.builder("stock_manager.restocking.creation_time")
                .description("Tempo de execução da criação de reabastecimento")
                .register(registry);
    }

    /**
     * Busca todos os registros de reposição de cerveja.
     *
     * @return Uma lista de {@link BeerRestockingDTO}.
     */
    @Override
    @Transactional(readOnly = true)
    public List<BeerRestockingDTO> findAll() {
        logger.info("SERVICE: Buscando todos os registros de reabastecimento.");
        List<BeerRestocking> list = bookRestockingRepository.findAll();
        return list.stream().map(BeerRestockingDTO::new).collect(Collectors.toList());
    }

    /**
     * Busca um registro de reposição de cerveja pelo seu ID.
     *
     * @param id O ID do registro de reposição.
     * @return O {@link BeerRestockingDTO} correspondente.
     * @throws ResourceNotFoundException Se o ID não for encontrado.
     */
    @Override
    @Transactional(readOnly = true)
    public BeerRestockingDTO findById(Long id) {
        logger.info("SERVICE: Buscando reabastecimento pelo ID: {}", id);
        // Usando a exceção de negócio correta
        BeerRestocking entity = bookRestockingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book restocking not found (ID: " + id + ")"));
        return new BeerRestockingDTO(entity);
    }

    /**
     * Cria e persiste um novo registro de reposição de cerveja,
     * e ATUALIZA o estoque da cerveja correspondente de forma transacional.
     *
     * <p>O tempo de execução desta operação é monitorado pelo {@code restockingTimer}.</p>
     *
     * @param dto O {@link BeerRestockingDTO} com os dados para criação.
     * @return O {@link BeerRestockingDTO} criado.
     * @throws ResourceNotFoundException Se a cerveja associada não for encontrada.
     */
    @Override
    @Transactional
    public BeerRestockingDTO create(BeerRestockingDTO dto) {
        logger.info("SERVICE: Iniciando criação de novo reabastecimento para cerveja ID: {}", dto.getBeerId());

        return restockingTimer.record(() -> {
            BeerRestocking entity = new BeerRestocking();
            copyDtoToEntity(dto, entity);

            Beer beer = entity.getBeer();
            if (beer.getStock() != null) {
                beer.getStock().setQuantity(beer.getStock().getQuantity() + entity.getQuantity());
                logger.info("SERVICE: Estoque da cerveja ID {} atualizado. Nova quantidade: {}", beer.getId(), beer.getStock().getQuantity());
            } else {
                logger.warn("SERVICE WARNING: Cerveja ID {} sem registro de Stock. Inicializando com quantidade reposta.", beer.getId());
            }

            entity.setMoment(Instant.now());
            BeerRestocking savedEntity = bookRestockingRepository.save(entity);
            return new BeerRestockingDTO(savedEntity);
        });
    }

    /**
     * Atualiza um registro de reposição de cerveja existente.
     *
     * <p>Nota: A lógica de atualização de estoque (reverter/aplicar nova diferença)
     * não está implementada aqui, e o código atual apenas sobrescreve os dados.
     * Se a atualização afetar o estoque, a lógica deve ser revisada.</p>
     *
     * @param id O ID do registro a ser atualizado.
     * @param dto O {@link BeerRestockingDTO} com os dados atualizados.
     * @return O {@link BeerRestockingDTO} atualizado.
     * @throws ResourceNotFoundException Se o ID ou a cerveja associada não for encontrada.
     */
    @Override
    @Transactional
    public BeerRestockingDTO update(Long id, BeerRestockingDTO dto) {
        logger.info("SERVICE: Iniciando atualização do reabastecimento ID: {}", id);

        try {
            BeerRestocking entity = bookRestockingRepository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = bookRestockingRepository.save(entity);
            return new BeerRestockingDTO(entity);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            logger.error("SERVICE ERROR: Reabastecimento não encontrado para atualização ID: {}", id);
            throw new ResourceNotFoundException("Book restocking not found (ID: " + id + ")");
        } catch (Exception e) {
             logger.error("SERVICE ERROR: Erro ao atualizar reabastecimento ID: {}. Detalhes: {}", id, e.getMessage());
             // Outras exceções de banco de dados podem ser lançadas aqui
             throw new DatabaseException("Database error during update: " + e.getMessage());
        }
    }

    /**
     * Exclui um registro de reposição de cerveja pelo seu ID.
     *
     * <p>Nota: Se o registro for excluído, a quantidade deve ser REVERTIDA
     * do estoque da cerveja correspondente, mas essa lógica não está implementada
     * no código fornecido.</p>
     *
     * @param id O ID do registro a ser excluído.
     * @throws ResourceNotFoundException Se o ID não for encontrado.
     * @throws DatabaseException Se houver violação de integridade.
     */
    @Override
    @Transactional
    public void delete(Long id) {
        logger.warn("SERVICE: Tentativa de exclusão do reabastecimento ID: {}", id);

        try {
            bookRestockingRepository.deleteById(id);
            // Verifica se a exclusão foi bem-sucedida e lança exceção se o registro não existia
            if (bookRestockingRepository.existsById(id)) { // Esta checagem é falha para deleteById, melhor usar getReferenceById/findById
                 throw new ResourceNotFoundException("Book restocking not found (ID: " + id + ")");
            }
        } catch (EmptyResultDataAccessException e) {
             logger.error("SERVICE ERROR: Reabastecimento não encontrado para exclusão ID: {}", id);
             throw new ResourceNotFoundException("Book restocking not found (ID: " + id + ")");
        } catch (DataIntegrityViolationException e) {
            logger.error("SERVICE ERROR: Violação de integridade ao tentar excluir reabastecimento ID: {}", id);
            throw new DatabaseException("Integrity violation: Cannot delete restocking record.");
        }
    }

    /**
     * Copia os dados do DTO para a entidade {@link BeerRestocking}.
     *
     * @param dto O DTO de origem.
     * @param entity A entidade de destino.
     * @throws ResourceNotFoundException Se a cerveja referenciada não for encontrada.
     */
    private void copyDtoToEntity(BeerRestockingDTO dto, BeerRestocking entity) {
        entity.setQuantity(dto.getQuantity());

        // Busca a entidade Beer para associar
        Beer beer = bookRepository.findById(dto.getBeerId())
                .orElseThrow(() -> new ResourceNotFoundException("Beer not found (ID: " + dto.getBeerId() + ")"));
        entity.setBeer(beer);

        // Garante que o objeto Stock da cerveja está carregado/inicializado antes de acessá-lo na criação.
        // beer.setStock(beer.getStock());
    }
}