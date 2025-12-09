package com.anapedra.stock_manager.services.impl;

import com.anapedra.stock_manager.domain.dtos.CategoryDTO;
import com.anapedra.stock_manager.domain.entities.Category;
import com.anapedra.stock_manager.repositories.CategoryRepository;
import com.anapedra.stock_manager.services.CategoryService;
import com.anapedra.stock_manager.services.exceptions.DatabaseException;
import com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementação da interface {@link CategoryService} que gerencia as operações de negócio
 * relacionadas à entidade Categoria (Category).
 *
 * <p>Esta classe lida com as operações CRUD (Create, Read, Update, Delete) e listagem de
 * categorias, garantindo a integridade transacional e o tratamento de exceções de negócio.
 * Inclui monitoramento de desempenho usando Micrometer ({@link Timer}).</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @see CategoryService
 * @see Category
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;
    private final Timer categoryCreationUpdateTimer;

    /**
     * Construtor para injeção de dependências.
     *
     * @param categoryRepository Repositório de categorias.
     * @param registry O registro de métricas do Micrometer.
     */
    public CategoryServiceImpl(CategoryRepository categoryRepository, MeterRegistry registry) {
        this.categoryRepository = categoryRepository;
        this.categoryCreationUpdateTimer = Timer.builder("stock_manager.category.creation_update_time")
                .description("Tempo de execução da criação ou atualização de categorias")
                .register(registry);
    }


    /**
     * Retorna uma lista de todas as categorias.
     *
     * @return Uma {@link List} de {@link CategoryDTO}.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        logger.info("SERVICE: Buscando todas as categorias.");
        List<Category> categories = categoryRepository.findAll();
        logger.info("SERVICE: Retornando {} categorias.", categories.size());
        return categories.stream()
                .map(CategoryDTO::new)
                .collect(Collectors.toList());
    }


    /**
     * Busca uma categoria pelo seu identificador único.
     *
     * @param id O ID da categoria.
     * @return O {@link CategoryDTO} correspondente.
     * @throws ResourceNotFoundException Se o ID não for encontrado.
     */
    @Override
    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        logger.info("SERVICE: Buscando categoria pelo ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("SERVICE WARN: Categoria ID {} não encontrada.", id);
                    return new ResourceNotFoundException("Categoria não encontrada (ID: " + id + ")");
                });
        logger.info("SERVICE: Categoria ID {} encontrada.", id);
        return new CategoryDTO(category);
    }


    /**
     * Insere e persiste um novo registro de categoria.
     *
     * <p>O tempo de execução desta operação é monitorado pelo {@code categoryCreationUpdateTimer}.</p>
     *
     * @param dto O {@link CategoryDTO} com os dados para criação.
     * @return O {@link CategoryDTO} criado, incluindo o ID gerado.
     */
    @Override
    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {
        logger.info("SERVICE: Iniciando inserção da nova categoria: {}", dto.getDescription());
        return categoryCreationUpdateTimer.record(() -> {
            Category category = new Category();
            category.setDescription(dto.getDescription());
            Category savedCategory = categoryRepository.save(category);
            logger.info("SERVICE: Categoria ID {} salva com sucesso.", savedCategory.getId());
            return new CategoryDTO(savedCategory);
        });
    }


    /**
     * Atualiza um registro de categoria existente.
     *
     * <p>O tempo de execução desta operação é monitorado pelo {@code categoryCreationUpdateTimer}.</p>
     *
     * @param id O ID do registro a ser atualizado.
     * @param dto O {@link CategoryDTO} com os dados atualizados.
     * @return O {@link CategoryDTO} atualizado.
     * @throws ResourceNotFoundException Se o ID não for encontrado.
     */
    @Override
    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        logger.info("SERVICE: Iniciando atualização da categoria ID: {}", id);
        return categoryCreationUpdateTimer.record(() -> {
            try {
                // Usando getReferenceById para potencial otimização se o método findById for pesado
                Category category = categoryRepository.getReferenceById(id);
                category.setDescription(dto.getDescription());
                Category updatedCategory = categoryRepository.save(category);
                logger.info("SERVICE: Categoria ID {} atualizada com sucesso.", id);
                return new CategoryDTO(updatedCategory);
            } catch (jakarta.persistence.EntityNotFoundException e) {
                logger.warn("SERVICE WARN: Categoria ID {} não encontrada para atualização.", id);
                throw new ResourceNotFoundException("Categoria não encontrada (ID: " + id + ")");
            }
        });
    }


    /**
     * Exclui um registro de categoria pelo seu identificador único.
     *
     * <p>Configurado com {@code Propagation.SUPPORTS} para executar em transação.
     * Lança {@link DatabaseException} em caso de violação de integridade referencial.</p>
     *
     * @param id O ID do registro a ser excluído.
     * @throws ResourceNotFoundException Se o ID não for encontrado.
     * @throws DatabaseException Se houver violação de integridade (ex: cervejas ainda usam a categoria).
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        logger.warn("SERVICE: Tentativa de exclusão da categoria ID: {}", id);
        if (!categoryRepository.existsById(id)) {
            logger.error("SERVICE ERROR: Tentativa de exclusão falhou. Recurso ID {} não encontrado.", id);
            throw new ResourceNotFoundException("Recurso não encontrado (ID: " + id + ")");
        }
        try {
            categoryRepository.deleteById(id);
            logger.info("SERVICE: Categoria ID {} excluída com sucesso.", id);
        } catch (DataIntegrityViolationException e) {
            logger.error("SERVICE ERROR: Falha de integridade referencial ao excluir categoria ID {}. Detalhes: {}", id, e.getMessage());
            throw new DatabaseException("Falha de integridade referencial: Categoria ID " + id + " está sendo referenciada por outras entidades.");
        }
    }
}