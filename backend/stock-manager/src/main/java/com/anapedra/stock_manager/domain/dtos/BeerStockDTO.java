package com.anapedra.stock_manager.domain.dtos;
import com.anapedra.stock_manager.domain.entities.Beer;

import java.io.Serializable;
import java.time.LocalDate; // Import necessário

/**
 * DTO (Data Transfer Object) usado para transferir informações de Estoque de Cerveja.
 *
 * <p>É usado em cenários onde apenas o estado atual do estoque e a data de validade
 * são necessários (ex: relatórios de estoque, painéis de controle).</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
public class BeerStockDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * O identificador único da cerveja.
     */
    private Long id;

    /**
     * O nome da cerveja.
     */
    private String name;

    /**
     * A quantidade atual em estoque.
     */
    private Integer stock;

    /**
     * A data de validade (expiração) da cerveja.
     */
    private LocalDate expirationDate; // <--- NOVO CAMPO DE DATA DE VENCIMENTO

    /**
     * Construtor padrão sem argumentos.
     */
    public BeerStockDTO() {
    }

    /**
     * Construtor que inicializa o DTO a partir de uma entidade {@link Beer}.
     *
     * @param entity A entidade Beer de origem.
     */
    public BeerStockDTO(Beer entity) {
        id = entity.getId();
        name = entity.getName();
        // Lógica de estoque: Tenta pegar do Stock, se nulo, usa o método de fallback da Beer.
        stock =(entity.getStock() != null) ? entity.getStock().getQuantity() : entity.returnQuantityStock();
        // Mapeamento do novo campo
        expirationDate = entity.getExpirationDate();
    }

    /**
     * Retorna o ID da cerveja.
     * @return O ID.
     */
    public Long getId() { return id; }

    /**
     * Retorna o nome da cerveja.
     * @return O nome.
     */
    public String getName() { return name; }

    /**
     * Retorna a quantidade em estoque.
     * @return A quantidade em estoque.
     */
    public Integer getStock() { return stock; }

    /**
     * Retorna a data de validade da cerveja.
     * @return A data de validade.
     */
    // NOVO GETTER
    public LocalDate getExpirationDate() { return expirationDate; }
}