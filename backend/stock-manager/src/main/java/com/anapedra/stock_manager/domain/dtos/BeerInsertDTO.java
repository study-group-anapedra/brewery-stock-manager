package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.Category;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BeerInsertDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    // --- Campos da Beer ---
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    private String name;

    private String urlImg;

    @NotNull(message = "O teor alcoólico é obrigatório")
    private Double alcoholContent;

    @NotNull(message = "O preço é obrigatório")
    @PositiveOrZero(message = "O preço deve ser positivo")
    private Double price;

    @NotNull(message = "A data de fabricação é obrigatória")
    private LocalDate manufactureDate;

    private LocalDate expirationDate;

    private List<CategoryDTO> categories = new ArrayList<>();

    @NotNull(message = "As informações de estoque são obrigatórias")
    private StockInputDTO stock; 

    public BeerInsertDTO() {}

    public BeerInsertDTO(String name, String urlImg, Double alcoholContent,
                         Double price, LocalDate manufactureDate,
                         LocalDate expirationDate, StockInputDTO stock) {
        this.name = name;
        this.urlImg = urlImg;
        this.alcoholContent = alcoholContent;
        this.price = price;
        this.manufactureDate = manufactureDate;
        this.expirationDate = expirationDate;
        this.stock = stock;
    }

    public BeerInsertDTO(Beer entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.urlImg = entity.getUrlImg();
        this.alcoholContent = entity.getAlcoholContent();
        this.price = entity.getPrice();
        this.manufactureDate = entity.getManufactureDate();
        this.expirationDate = entity.getExpirationDate();
        this.stock = new StockInputDTO(entity.getStock());

        entity.getCategories().forEach(category -> this.categories.add(new CategoryDTO(category)));
    }

    public BeerInsertDTO(Beer entity, Set<Category> categories) {
        this(entity);
        categories.forEach(category -> this.categories.add(new CategoryDTO(category)));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUrlImg() { return urlImg; }
    public void setUrlImg(String urlImg) { this.urlImg = urlImg; }

    public Double getAlcoholContent() { return alcoholContent; }
    public void setAlcoholContent(Double alcoholContent) { this.alcoholContent = alcoholContent; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public LocalDate getManufactureDate() { return manufactureDate; }
    public void setManufactureDate(LocalDate manufactureDate) { this.manufactureDate = manufactureDate; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public List<CategoryDTO> getCategories() { return categories; }
    public void setCategories(List<CategoryDTO> categories) { this.categories = categories; }

    public StockInputDTO getStock() { return stock; }
    public void setStock(StockInputDTO stock) { this.stock = stock; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BeerInsertDTO)) return false;
        BeerInsertDTO that = (BeerInsertDTO) o;
        return Objects.equals(name, that.name)
                && Objects.equals(alcoholContent, that.alcoholContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, alcoholContent);
    }
}
