package com.anapedra.stock_manager.services;

import com.anapedra.stock_manager.domain.dtos.BeerFilterDTO;
import com.anapedra.stock_manager.domain.dtos.BeerInsertDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * Interface de serviço para gerenciar as operações relacionadas à Cerveja (Beer).
 *
 * <p>Define o contrato para as operações de listagem com filtros complexos,
 * consulta por ID e as operações CRUD (Create, Read, Update, Delete),
 * utilizando os DTOs {@link BeerFilterDTO} e {@link BeerInsertDTO}.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @see BeerFilterDTO
 * @see BeerInsertDTO
 * @since 0.0.1-SNAPSHOT
 */
public interface BeerService {

    /**
     * Busca cervejas paginadas aplicando filtros dinâmicos.
     *
     * @param categoryId ID da categoria (opcional).
     * @param categoryName Descrição da categoria (opcional, busca parcial).
     * @param beerDescription Nome/descrição da cerveja (opcional, busca parcial).
     * @param minQuantity Quantidade mínima em estoque (opcional).
     * @param maxQuantity Quantidade máxima em estoque (opcional).
     * @param pageable Objeto de paginação e ordenação do Spring Data.
     * @return Uma {@link Page} de {@link BeerFilterDTO} que correspondem aos filtros.
     */
    Page<BeerFilterDTO> findAllBeer(
            Long categoryId,
            String categoryName,
            String beerDescription,
            Integer minQuantity,
            Integer maxQuantity,
            Pageable pageable
    );

    /**
     * Busca uma cerveja pelo seu identificador único.
     *
     * @param id O ID da cerveja.
     * @return O {@link BeerFilterDTO} correspondente.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     */
    BeerFilterDTO findById(Long id);

    /**
     * Insere e persiste um novo registro de cerveja.
     *
     * @param dto O {@link BeerInsertDTO} com os dados para criação.
     * @return O {@link BeerInsertDTO} criado, incluindo o ID gerado.
     * @throws com.anapedra.stock_manager.services.exceptions.DatabaseException Se a cerveja já existir.
     */
    BeerInsertDTO insert(BeerInsertDTO dto);

    /**
     * Atualiza um registro de cerveja existente.
     *
     * @param id O ID do registro a ser atualizado.
     * @param dto O {@link BeerInsertDTO} com os dados atualizados.
     * @return O {@link BeerInsertDTO} atualizado.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     * @throws com.anapedra.stock_manager.services.exceptions.DatabaseException Se houver violação de dados.
     */
    BeerInsertDTO update(Long id, BeerInsertDTO dto);

    /**
     * Exclui um registro de cerveja pelo seu identificador único.
     *
     * @param id O ID do registro a ser excluído.
     * @throws com.anapedra.stock_manager.services.exceptions.ResourceNotFoundException Se o ID não for encontrado.
     * @throws com.anapedra.stock_manager.services.exceptions.DatabaseException Se houver violação de integridade.
     */
    void delete(Long id);

}