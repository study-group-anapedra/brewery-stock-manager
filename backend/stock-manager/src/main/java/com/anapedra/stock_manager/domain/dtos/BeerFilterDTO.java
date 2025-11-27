package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.Beer;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class BeerFilterDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String urlImg;
    private Double alcoholContent;
    private Double price;
    private LocalDate manufactureDate;
    private LocalDate expirationDate;

    private Set<MinCategoryDTO> categories = new HashSet<>();
    private Integer stock;

    public BeerFilterDTO() {
    }

    public BeerFilterDTO(Beer entity) {
        id = entity.getId();
        name = entity.getName();
        urlImg = entity.getUrlImg();
        alcoholContent = entity.getAlcoholContent();
        price = entity.getPrice();
        manufactureDate = entity.getManufactureDate();
        expirationDate = entity.getExpirationDate();
        stock = entity.returnQuantityStock();
        entity.getCategories().forEach(category -> this.categories.add(new MinCategoryDTO(category)));
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getUrlImg() { return urlImg; }
    public Double getAlcoholContent() { return alcoholContent; }
    public Double getPrice() { return price; }
    public LocalDate getManufactureDate() { return manufactureDate; }
    public LocalDate getExpirationDate() { return expirationDate; }

    public Set<MinCategoryDTO> getCategories() {
        return categories;
    }

    public Integer getStock() {
        return stock;
    }

    public void setId(Long id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BeerFilterDTO)) return false;
        BeerFilterDTO that = (BeerFilterDTO) o;
        return Objects.equals(name, that.name)
                && Objects.equals(alcoholContent, that.alcoholContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, alcoholContent);
    }
}
