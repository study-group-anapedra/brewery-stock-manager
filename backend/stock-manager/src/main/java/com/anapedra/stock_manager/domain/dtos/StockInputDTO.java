package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.Stock;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;

/**
 * DTO (Data Transfer Object) usado para receber a quantidade de estoque
 * em operações de entrada (inserção ou atualização de estoque).
 *
 * <p>Contém anotações de validação Javax/Jakarta Bean Validation ({@code @NotNull},
 * {@code @PositiveOrZero}) para garantir que a quantidade seja um número
 * positivo ou zero e não seja nula.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
public class StockInputDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * A quantidade de unidades em estoque.
     * Não pode ser nula e deve ser positiva ou zero.
     */
    @NotNull(message = "A quantidade inicial de estoque é obrigatória")
    @PositiveOrZero(message = "A quantidade de estoque deve ser maior ou igual a zero")
    private Integer quantity;

    // --- Construtores ---

    /**
     * Construtor padrão sem argumentos.
     */
    public StockInputDTO() {
    }

    /**
     * Construtor para inicializar a quantidade.
     *
     * @param quantity A quantidade de estoque.
     */
    public StockInputDTO(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Construtor que inicializa o DTO a partir de uma entidade {@link Stock}.
     *
     * @param entity A entidade Stock de origem.
     */
    public StockInputDTO(Stock entity) {
        quantity = entity.getQuantity();
    }


    // --- Getters e Setters ---

    /**
     * Retorna a quantidade em estoque.
     * @return A quantidade.
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Define a quantidade em estoque.
     *
     * <p>O nome do método foi mantido como {@code setInitialQuantity}
     * para seguir a nomenclatura original, mas funciona como setter normal.</p>
     *
     * @param quantity A nova quantidade.
     */
    public void setInitialQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}