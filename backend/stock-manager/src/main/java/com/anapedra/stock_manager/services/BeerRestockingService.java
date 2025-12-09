package com.anapedra.stock_manager.services;


import com.anapedra.stock_manager.domain.dtos.BeerRestockingDTO;

import java.util.List;

/**
 * Interface de serviço para gerenciar as operações relacionadas à Reposição de Cerveja (BeerRestocking).
 *
 * <p>Define o contrato para as operações CRUD (Create, Read, Update, Delete) e listagem,
 * trabalhando com o DTO {@link BeerRestockingDTO} para transferência de dados.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @see BeerRestockingDTO
 * @since 0.0.1-SNAPSHOT
 */
public interface BeerRestockingService {

    /**
     * Retorna uma lista de todos os registros de reposição de cerveja.
     *
     * @return Uma {@link List} de {@link BeerRestockingDTO}.
     */
    List<BeerRestockingDTO> findAll();

    /**
     * Busca um registro de reposição de cerveja pelo seu identificador único.
     *
     * @param id O ID do registro de reposição.
     * @return O {@link BeerRestockingDTO} correspondente.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     */
    BeerRestockingDTO findById(Long id);

    /**
     * Cria e persiste um novo registro de reposição de cerveja.
     *
     * @param dto O {@link BeerRestockingDTO} com os dados para criação.
     * @return O {@link BeerRestockingDTO} criado, incluindo o ID gerado.
     */
    BeerRestockingDTO create(BeerRestockingDTO dto);

    /**
     * Atualiza um registro de reposição de cerveja existente.
     *
     * @param id O ID do registro a ser atualizado.
     * @param dto O {@link BeerRestockingDTO} com os dados atualizados.
     * @return O {@link BeerRestockingDTO} atualizado.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     */
    BeerRestockingDTO update(Long id, BeerRestockingDTO dto);

    /**
     * Exclui um registro de reposição de cerveja pelo seu identificador único.
     *
     * @param id O ID do registro a ser excluído.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     * @throws com.anapedra.stock_manager.services.exceptions.DatabaseException Se houver violação de integridade.
     */
    void delete(Long id);
}