package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.Stock;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;

public class StockInputDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "A quantidade inicial de estoque é obrigatória")
    @PositiveOrZero(message = "A quantidade de estoque deve ser maior ou igual a zero")
    private Integer quantity;

    // --- Construtores ---
    public StockInputDTO() {
    }

    public StockInputDTO(Integer quantity) {
        this.quantity = quantity;
    }
    public StockInputDTO(Stock entity) {
        quantity = entity.getQuantity();
    }


    // --- Getters e Setters ---
    public Integer getQuantity() {
        return quantity;
    }

    public void setInitialQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}