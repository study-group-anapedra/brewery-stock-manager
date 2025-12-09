package com.anapedra.stock_manager.domain.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * Representa um registro de reabastecimento (reposição) de uma cerveja específica no estoque.
 * Esta classe mapeia a tabela "tb_restocking_beer" no banco de dados.
 *
 * <p>É usada para rastrear quando e em qual quantidade uma cerveja foi reposta.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "tb_restocking_beer")
public class BeerRestocking implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * O identificador único do registro de reabastecimento.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * A quantidade de cervejas reabastecida.
     */
    private Integer quantity;

    /**
     * O momento exato (timestamp) em que o reabastecimento ocorreu.
     */
    private Instant moment;

    /**
     * A cerveja (Beer) que foi reabastecida.
     * Mapeia o relacionamento Many-to-One.
     */
    @ManyToOne
    @JoinColumn(name = "beer_id", nullable = false)
    private Beer beer;

    /**
     * Construtor padrão sem argumentos.
     */
    public BeerRestocking() {
    }

    /**
     * Construtor para inicializar todos os campos.
     *
     * @param id O identificador do registro.
     * @param quantity A quantidade reabastecida.
     * @param moment O momento em que ocorreu o reabastecimento.
     * @param beer A cerveja associada.
     */
    public BeerRestocking(Long id, Integer quantity, Instant moment, Beer beer) {
        this.id = id;
        this.quantity = quantity;
        this.moment = moment;
        this.beer = beer;
    }


    // Getters and Setters
    /**
     * Retorna o ID do registro de reabastecimento.
     * @return O ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o ID do registro de reabastecimento.
     * @param id O novo ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna a quantidade de cervejas reabastecida.
     * @return A quantidade.
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Define a quantidade de cervejas reabastecida.
     * @param quantity A nova quantidade.
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Retorna o momento do reabastecimento.
     * @return O Instant do momento.
     */
    public Instant getMoment() {
        return moment;
    }

    /**
     * Define o momento do reabastecimento.
     * @param moment O novo Instant do momento.
     */
    public void setMoment(Instant moment) {
        this.moment = moment;
    }

    /**
     * Retorna a cerveja associada ao registro de reabastecimento.
     * @return A entidade Beer.
     */
    public Beer getBeer() {
        return beer;
    }

    /**
     * Define a cerveja associada ao registro de reabastecimento.
     * @param beer A nova entidade Beer.
     */
    public void setBeer(Beer beer) {
        this.beer = beer;
    }

    /**
     * Compara dois objetos BeerRestocking com base no ID.
     * @param o O objeto a ser comparado.
     * @return true se os IDs forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeerRestocking that = (BeerRestocking) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Calcula o hash code com base no ID.
     * @return O hash code do ID.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}