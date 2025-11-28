package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.BeerRestocking;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class BeerRestockingDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer quantity;
    private Instant moment;
    private String beerName;
    private Double alcoholContent;
    private LocalDate manufactureDate;
    private LocalDate expirationDate;
    private List<MinCategoryDTO> categoryNames = new ArrayList<>();
    

    public BeerRestockingDTO() {
    }

    public BeerRestockingDTO(Long id, Integer quantity, Instant moment,
                             String beerName, Double alcoholContent, LocalDate manufactureDate, LocalDate expirationDate) {
        this.id = id;
        this.quantity = quantity;
        this.moment = moment;
        this.beerName = beerName;
        this.alcoholContent = alcoholContent;
        this.manufactureDate = manufactureDate;
        this.expirationDate = expirationDate;
    }

    public BeerRestockingDTO(BeerRestocking entity) {
        id = entity.getId();
        quantity = entity.getQuantity();
        moment = entity.getMoment();
        
        if (entity.getBeer() != null) {
            this.beerName = entity.getBeer().getName();
            this.alcoholContent = entity.getBeer().getAlcoholContent();
            this.manufactureDate = entity.getBeer().getManufactureDate();
            this.expirationDate = entity.getBeer().getExpirationDate();
            entity.getBeer().getCategories().forEach(category -> this.categoryNames.add(new MinCategoryDTO(category)));
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Instant getMoment() {
        return moment;
    }

    public void setMoment(Instant moment) {
        this.moment = moment;
    }

    public String getBeerName() {
        return beerName;
    }

    public void setBeerName(String beerName) {
        this.beerName = beerName;
    }

    public List<MinCategoryDTO> getCategoryNames() {
        return categoryNames;
    }

    public void setCategoryNames(List<MinCategoryDTO> categoryNames) {
        this.categoryNames = categoryNames;
    }

    public Double getAlcoholContent() {
        return alcoholContent;
    }

    public void setAlcoholContent(Double alcoholContent) {
        this.alcoholContent = alcoholContent;
    }

    public LocalDate getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(LocalDate manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
}