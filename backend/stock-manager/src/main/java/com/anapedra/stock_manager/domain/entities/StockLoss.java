package com.anapedra.stock_manager.domain.entities;

import com.anapedra.stock_manager.domain.enums.LossReason;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "tb_stock_loss")
public class StockLoss implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "beer_id", nullable = false)
    private Beer beer;

    @Column(nullable = false)
    private Integer quantityLost;

    @Column(nullable = false)
    private Integer reason;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd") // Removemos o timezone, pois LocalDate não tem hora
    private LocalDate lossDate;

    private Instant registrationMoment;

    public StockLoss() {
    }

    public StockLoss(Long id, Beer beer, Integer quantityLost, LossReason reason, LocalDate lossDate, String description) {
        this.id = id;
        this.beer = beer;
        this.quantityLost = quantityLost;
        setLossReason(reason);
        this.lossDate = lossDate;
        this.description = description;
        this.registrationMoment = Instant.now();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Beer getBeer() {
        return beer;
    }

    public void setBeer(Beer beer) {
        this.beer = beer;
    }

    public Integer getQuantityLost() {
        return quantityLost;
    }

    public void setQuantityLost(Integer quantityLost) {
        this.quantityLost = quantityLost;
    }

    public Integer getReason() {
        return reason;
    }

    public void setReason(Integer reason) {
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

    public LossReason getLossReason() {
        return LossReason.valueOf(reason);
    }

    public void setLossReason(LossReason reason) {
        if (reason != null){
            this.reason = reason.getCode();
        }
    }

//    public void getUpdateStock() {
//        beer.getStock().setQuantity(beer.getStock().getQuantity() - quantityLost);
//    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StockLoss stockLoss = (StockLoss) o;
        return Objects.equals(id, stockLoss.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}