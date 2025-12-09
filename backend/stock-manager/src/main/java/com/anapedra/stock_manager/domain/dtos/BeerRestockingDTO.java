package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.BeerRestocking;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO (Data Transfer Object) usado para receber e enviar informações de uma
 * operação de Reposição de Estoque (Restocking) de uma cerveja.
 *
 * <p>Contém apenas os campos essenciais para identificar qual cerveja foi reposta
 * e em qual quantidade.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
public class BeerRestockingDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * O ID da cerveja que será reposta.
     */
    private Long beerId;

    /**
     * A quantidade de unidades que estão sendo adicionadas ao estoque.
     */
    private Integer quantity;

    /**
     * Construtor padrão sem argumentos.
     */
    public BeerRestockingDTO() {
    }

    /**
     * Construtor para inicializar os campos manualmente.
     *
     * @param beerId O ID da cerveja.
     * @param quantity A quantidade a ser reposta.
     */
    public BeerRestockingDTO(Long beerId, Integer quantity){
        this.beerId = beerId;
        this.quantity = quantity;

    }

    /**
     * Construtor que inicializa o DTO a partir de uma entidade {@link BeerRestocking}.
     *
     * @param entity A entidade BeerRestocking de origem.
     */
    public BeerRestockingDTO(BeerRestocking entity) {
        // Assume que a entidade BeerRestocking tem a referência Beer carregada
        beerId = entity.getBeer().getId();
        quantity = entity.getQuantity();
    }


    /**
     * Retorna o ID da cerveja.
     * @return O ID da cerveja.
     */
    public Long getBeerId() {
        return beerId;
    }

    /**
     * Define o ID da cerveja.
     * @param beerId O novo ID da cerveja.
     */
    public void setBeerId(Long beerId) {
        this.beerId = beerId;
    }

    /**
     * Retorna a quantidade a ser reposta.
     * @return A quantidade.
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Define a quantidade a ser reposta.
     * @param quantity A nova quantidade.
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}