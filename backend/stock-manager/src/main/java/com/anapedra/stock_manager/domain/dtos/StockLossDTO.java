package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.StockLoss;
import com.anapedra.stock_manager.domain.enums.LossReason;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public class StockLossDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long beerId;
    private String beerName;
    private Integer quantityLost;
    private String reasonDescription;
    private LossReason reason;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate lossDate;

    private Instant registrationMoment;
    
    private String description;

    public StockLossDTO() {
    }

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

    public StockLossDTO(StockLoss entity) {
        id = entity.getId();
        beerId = entity.getBeer().getId();
        beerName = entity.getBeer().getName();
        quantityLost = entity.getQuantityLost();
        reasonDescription = entity.getDescription(); // Isso não tem nada a ver com ENUM e sim de uma descrição do ocorrido
        lossDate = entity.getLossDate();
        registrationMoment = entity.getRegistrationMoment();
        description = entity.getDescription();
        reason = (entity.getLossReason() != null) ? entity.getLossReason() : LossReason.OTHER;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBeerId() {
        return beerId;
    }

    public void setBeerId(Long beerId) {
        this.beerId = beerId;
    }

    public String getBeerName() {
        return beerName;
    }

    public Integer getQuantityLost() {
        return quantityLost;
    }

    public void setQuantityLost(Integer quantityLost) {
        this.quantityLost = quantityLost;
    }

    public String getReasonDescription() {
        return reasonDescription;
    }

    public void setReasonDescription(String reasonDescription) {
        this.reasonDescription = reasonDescription;
    }

    public LossReason getReason() {
        return reason;
    }

    public void setReason(LossReason reason) {
        this.reason = reason;
    }

    public LocalDate getLossDate() {
        return lossDate;
    }

    public void setLossDate(LocalDate lossDate) {
        this.lossDate = lossDate;
    }

    public Instant getRegistrationMoment() {
        return registrationMoment;
    }

    public void setRegistrationMoment(Instant registrationMoment) {
        this.registrationMoment = registrationMoment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StockLossDTO that = (StockLossDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(beerId, that.beerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, beerId);
    }
}