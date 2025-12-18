package com.anapedra.stock_manager.domain.entities;

import com.anapedra.stock_manager.domain.enums.LossReason;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa um registro de Perda de Estoque (StockLoss).
 * Esta classe mapeia a tabela "tb_stock_loss" no banco de dados.
 *
 * <p>É usada para rastrear perdas de produtos (cervejas), incluindo a quantidade,
 * o motivo da perda e a data em que ocorreu.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "tb_stock_loss")
public class StockLoss implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * O identificador único do registro de perda.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Uma descrição detalhada ou observação sobre a perda.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * A cerveja (Beer) que foi perdida. Relacionamento Many-to-One.
     */
    @ManyToOne
    @JoinColumn(name = "beer_id", nullable = false)
    private Beer beer;
 /**
     * O código inteiro que representa o motivo da perda (mapeado para LossReason).
     */
    @Column(nullable = false)
    private Integer reason;
    /**
     * A quantidade de unidades perdidas.
     */
    @Column(nullable = false)
    private Integer quantityLost;


    /**
     * A data em que a perda foi registrada ou ocorreu.
     */
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd") // Removemos o timezone, pois LocalDate não tem hora
    private LocalDate lossDate;

    /**
     * O momento exato (timestamp) em que o registro foi criado no sistema.
     */
    private Instant registrationMoment;

    /**
     * Construtor padrão sem argumentos.
     */
    public StockLoss() {
    }

    /**
     * Construtor para inicializar o objeto StockLoss.
     *
     * @param id O identificador do registro.
     * @param beer A cerveja envolvida na perda.
     * @param quantityLost A quantidade perdida.
    * @param reason O motivo da perda (Enum LossReason).
     * @param lossDate A data da perda.
     * @param description A descrição ou observação.
     */
    public StockLoss(Long id, Beer beer, Integer quantityLost,LossReason reason ,LocalDate lossDate, String description) {
        this.id = id;
        this.beer = beer;
        this.quantityLost = quantityLost;
        setLossReason(reason);
        this.lossDate = lossDate;
        this.description = description;
        this.registrationMoment = Instant.now();
    }

    /**
     * Retorna o ID do registro de perda.
     * @return O ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o ID do registro de perda.
     * @param id O novo ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna a descrição ou observação sobre a perda.
     * @return A descrição.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Define a descrição ou observação sobre a perda.
     * @param description A nova descrição.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retorna a cerveja associada à perda.
     * @return A entidade Beer.
     */
    public Beer getBeer() {
        return beer;
    }

    /**
     * Define a cerveja associada à perda.
     * @param beer A nova entidade Beer.
     */
    public void setBeer(Beer beer) {
        this.beer = beer;
    }

    /**
     * Retorna a quantidade de unidades perdidas.
     * @return A quantidade perdida.
     */
    public Integer getQuantityLost() {
        return quantityLost;
    }

    /**
     * Define a quantidade de unidades perdidas.
     * @param quantityLost A nova quantidade perdida.
     */
    public void setQuantityLost(Integer quantityLost) {
        this.quantityLost = quantityLost;
    }



    /**
     * Retorna a data em que a perda ocorreu.
     * @return O LocalDate da perda.
     */
    public LocalDate getLossDate() {
        return lossDate;
    }

    /**
     * Define a data em que a perda ocorreu.
     * @param lossDate O novo LocalDate da perda.
     */
    public void setLossDate(LocalDate lossDate) {
        this.lossDate = lossDate;
    }

    /**
     * Retorna o momento em que o registro de perda foi criado no sistema.
     * @return O Instant do registro.
     */
    public Instant getRegistrationMoment() {
        return registrationMoment;
    }

    /**
     * Define o momento em que o registro de perda foi criado no sistema.
     * @param registrationMoment O novo Instant do registro.
     */
    public void setRegistrationMoment(Instant registrationMoment) {
        this.registrationMoment = registrationMoment;
    }

    /**
     * Retorna o motivo da perda como um Enum {@link LossReason}.
     * @return O LossReason.
     */
    public LossReason getLossReason() {
        return LossReason.valueOf(reason);
    }

    /**
     * Define o motivo da perda usando um Enum {@link LossReason}.
     * @param reason O novo LossReason.
     */
    public void setLossReason(LossReason reason) {
        if (reason != null){
            this.reason = reason.getCode();
        }
    }

    /**
     * Atualiza o estoque da cerveja associada, diminuindo a quantidade pela
     * quantidade perdida (`quantityLost`).
     */
   public void getUpdateStock() {
        beer.getStock().setQuantity(beer.getStock().getQuantity() - quantityLost);
   }


    /**
     * Compara dois objetos StockLoss com base no ID.
     * @param o O objeto a ser comparado.
     * @return true se os IDs forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StockLoss stockLoss = (StockLoss) o;
        return Objects.equals(id, stockLoss.id);
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