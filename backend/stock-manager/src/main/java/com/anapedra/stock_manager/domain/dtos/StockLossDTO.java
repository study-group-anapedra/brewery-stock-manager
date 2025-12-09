package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.StockLoss;
import com.anapedra.stock_manager.domain.enums.LossReason;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * DTO (Data Transfer Object) para a entidade Perda de Estoque (StockLoss).
 *
 * <p>Usado para transferir dados detalhados sobre uma perda de produto,
 * incluindo o produto afetado, a quantidade, a data e o motivo classificado
 * ({@link LossReason}) e textual.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
public class StockLossDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * O identificador único do registro de perda.
     */
    private Long id;

    /**
     * O ID da cerveja afetada pela perda.
     */
    private Long beerId;

    /**
     * O nome da cerveja afetada.
     */
    private String beerName;

    /**
     * A quantidade de unidades perdidas.
     */
    private Integer quantityLost;

    /**
     * Campo herdado do campo 'description' da entidade StockLoss, usado para retrocompatibilidade ou descrição do motivo.
     */
    private String reasonDescription;

    /**
     * O motivo classificado da perda (Enum {@link LossReason}).
     */
    private LossReason reason;

    /**
     * A data em que a perda ocorreu ou foi registrada (formato yyyy-MM-dd).
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate lossDate;

    /**
     * O momento exato em que o registro foi criado no sistema.
     */
    private Instant registrationMoment;

    /**
     * Uma descrição textual detalhada sobre a perda (idêntico a reasonDescription na entidade).
     */
    private String description;

    /**
     * Construtor padrão sem argumentos.
     */
    public StockLossDTO() {
    }

    /**
     * Construtor para inicializar todos os campos.
     */
    public StockLossDTO(Long id, Long beerId,Integer quantityLost,String reasonDescription, LossReason reason, LocalDate lossDate, Instant registrationMoment, String description) {
        this.id = id;
        this.beerId = beerId;
        this.quantityLost = quantityLost;
        this.reasonDescription = reasonDescription;
        this.reason = reason;
        this.lossDate = lossDate;
        this.registrationMoment = registrationMoment;
        this.description = description;
    }

    /**
     * Construtor que inicializa o DTO a partir de uma entidade {@link StockLoss}.
     *
     * @param entity A entidade StockLoss de origem.
     */
    public StockLossDTO(StockLoss entity) {
        id = entity.getId();
        // Assume que a referência Beer está carregada
        beerId = entity.getBeer().getId();
        beerName = entity.getBeer().getName();
        quantityLost = entity.getQuantityLost();

        // Mapeia o campo 'description' da entidade para ambos os campos do DTO
        reasonDescription = entity.getDescription();
        description = entity.getDescription();

        lossDate = entity.getLossDate();
        registrationMoment = entity.getRegistrationMoment();

        // Mapeia o Enum LossReason, com fallback para OTHER se for nulo
        reason = (entity.getLossReason() != null) ? entity.getLossReason() : LossReason.OTHER;

    }

    // --- Getters e Setters ---

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
     * Retorna o nome da cerveja.
     * @return O nome da cerveja.
     */
    public String getBeerName() {
        return beerName;
    }

    /**
     * Retorna a quantidade perdida.
     * @return A quantidade perdida.
     */
    public Integer getQuantityLost() {
        return quantityLost;
    }

    /**
     * Define a quantidade perdida.
     * @param quantityLost A nova quantidade.
     */
    public void setQuantityLost(Integer quantityLost) {
        this.quantityLost = quantityLost;
    }

    /**
     * Retorna a descrição do motivo (campo antigo/compatibilidade).
     * @return A descrição do motivo.
     */
    public String getReasonDescription() {
        return reasonDescription;
    }

    /**
     * Define a descrição do motivo (campo antigo/compatibilidade).
     * @param reasonDescription A nova descrição.
     */
    public void setReasonDescription(String reasonDescription) {
        this.reasonDescription = reasonDescription;
    }

    /**
     * Retorna o motivo da perda como Enum.
     * @return O {@link LossReason}.
     */
    public LossReason getReason() {
        return reason;
    }

    /**
     * Define o motivo da perda como Enum.
     * @param reason O novo {@link LossReason}.
     */
    public void setReason(LossReason reason) {
        this.reason = reason;
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
     * @param lossDate O novo LocalDate.
     */
    public void setLossDate(LocalDate lossDate) {
        this.lossDate = lossDate;
    }

    /**
     * Retorna o momento do registro no sistema.
     * @return O Instant do registro.
     */
    public Instant getRegistrationMoment() {
        return registrationMoment;
    }

    /**
     * Define o momento do registro no sistema.
     * @param registrationMoment O novo Instant.
     */
    public void setRegistrationMoment(Instant registrationMoment) {
        this.registrationMoment = registrationMoment;
    }

    /**
     * Retorna a descrição detalhada (o mesmo valor de reasonDescription).
     * @return A descrição detalhada.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Define a descrição detalhada.
     * @param description A nova descrição.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Compara dois objetos StockLossDTO com base no ID do registro e no ID da cerveja.
     * @param o O objeto a ser comparado.
     * @return true se os IDs forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StockLossDTO that = (StockLossDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(beerId, that.beerId);
    }

    /**
     * Calcula o hash code com base no ID do registro e no ID da cerveja.
     * @return O hash code combinado.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, beerId);
    }
}