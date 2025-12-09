package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.Category;

import java.io.Serializable;

/**
 * DTO (Data Transfer Object) mínimo usado para transferir apenas a descrição
 * de uma {@link Category}.
 *
 * <p>É usado em cenários onde queremos incluir informações de categorias dentro
 * de outro DTO (como {@link com.anapedra.stock_manager.domain.dtos.BeerFilterDTO}),
 * mas precisamos de um objeto extremamente leve, ignorando ID e nome.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
public class MinCategoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * A descrição detalhada da categoria.
     */
    private String description;

    /**
     * Construtor que inicializa o DTO a partir de uma entidade {@link Category}.
     *
     * @param entity A entidade Category de origem.
     */
    public MinCategoryDTO(Category entity) {
        description = entity.getDescription();

    }

    /**
     * Retorna a descrição da categoria.
     * @return A descrição.
     */
    public String getDescription() {
        return description;
    }
}