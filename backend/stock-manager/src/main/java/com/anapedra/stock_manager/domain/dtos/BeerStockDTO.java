package com.anapedra.stock_manager.domain.dtos;
import com.anapedra.stock_manager.domain.entities.Beer;

import java.io.Serializable;
import java.time.LocalDate; // Import necessário

public class BeerStockDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Integer stock;
    private LocalDate expirationDate; // <--- NOVO CAMPO DE DATA DE VENCIMENTO

    public BeerStockDTO() {
    }

    public BeerStockDTO(Beer entity) {
        id = entity.getId();
        name = entity.getName();
        // Lógica de estoque
        stock =(entity.getStock() != null) ? entity.getStock().getQuantity() : entity.returnQuantityStock();
        // Mapeamento do novo campo
        expirationDate = entity.getExpirationDate(); 
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Integer getStock() { return stock; }
    
    // NOVO GETTER
    public LocalDate getExpirationDate() { return expirationDate; } 
}