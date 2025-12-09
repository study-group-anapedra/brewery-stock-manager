package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.Category;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO (Data Transfer Object) usado para transferir informações completas da entidade {@link Category}.
 *
 * <p>É utilizado em endpoints que exigem a visualização ou manipulação de todos os atributos
 * de uma categoria (ID, nome e descrição).</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
public class CategoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * O identificador único da categoria.
     */
    private Long id;

    /**
     * O nome da categoria.
     */
    private String name;

    /**
     * A descrição detalhada da categoria.
     */
    private String description;


    /**
     * Construtor padrão sem argumentos.
     */
    public CategoryDTO() {
    }

    /**
     * Construtor para inicializar todos os campos.
     *
     * @param id O identificador da categoria.
     * @param name O nome da categoria.
     * @param description A descrição da categoria.
     */
    public CategoryDTO(Long id,String name, String description) {
        this.id = id;
        this.name =name;
        this.description = description;

    }

    /**
     * Construtor que inicializa o DTO a partir de uma entidade {@link Category}.
     *
     * @param entity A entidade Category de origem.
     */
    public CategoryDTO(Category entity) {
        id = entity.getId();
        description = entity.getDescription();
        name = entity.getName();

    }

    /**
     * Retorna o ID da categoria.
     * @return O ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o ID da categoria.
     * @param id O novo ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna o nome da categoria.
     * @return O nome.
     */
    public String getName() {
        return name;
    }

    /**
     * Define o nome da categoria.
     * @param name O novo nome.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retorna a descrição da categoria.
     * @return A descrição.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Define a descrição da categoria.
     * @param description A nova descrição.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Compara dois objetos CategoryDTO com base no ID.
     * @param o O objeto a ser comparado.
     * @return true se os IDs forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CategoryDTO that = (CategoryDTO) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Calcula o hash code com base no ID.
     * @return O hash code do ID.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}