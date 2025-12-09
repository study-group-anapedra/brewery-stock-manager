package com.anapedra.stock_manager.services;


import com.anapedra.stock_manager.domain.dtos.CategoryDTO;

import java.util.List;

/**
 * Interface de serviço para gerenciar as operações relacionadas à Categoria (Category).
 *
 * <p>Define o contrato para as operações CRUD (Create, Read, Update, Delete) e listagem,
 * trabalhando com o DTO {@link CategoryDTO} para transferência de dados.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @see CategoryDTO
 * @since 0.0.1-SNAPSHOT
 */
public interface CategoryService {

    /**
     * Retorna uma lista de todas as categorias.
     *
     * @return Uma {@link List} de {@link CategoryDTO}.
     */
    List<CategoryDTO> findAll();

    /**
     * Busca uma categoria pelo seu identificador único.
     *
     * @param id O ID da categoria.
     * @return O {@link CategoryDTO} correspondente.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     */
    CategoryDTO findById(Long id);

    /**
     * Insere e persiste um novo registro de categoria.
     *
     * @param dto O {@link CategoryDTO} com os dados para criação.
     * @return O {@link CategoryDTO} criado, incluindo o ID gerado.
     * @throws com.anapedra.stock_manager.services.exceptions.DatabaseException Se a categoria já existir.
     */
    CategoryDTO insert(CategoryDTO dto);

    /**
     * Atualiza um registro de categoria existente.
     *
     * @param id O ID do registro a ser atualizado.
     * @param dto O {@link CategoryDTO} com os dados atualizados.
     * @return O {@link CategoryDTO} atualizado.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     * @throws com.anapedra.stock_manager.services.exceptions.DatabaseException Se houver violação de dados.
     */
    CategoryDTO update(Long id, CategoryDTO dto);

    /**
     * Exclui um registro de categoria pelo seu identificador único.
     *
     * @param id O ID do registro a ser excluído.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     * @throws com.anapedra.stock_manager.services.exceptions.DatabaseException Se houver violação de integridade.
     */
    void delete(Long id);
}