package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.Category;

import java.io.Serializable;

public class MinCategoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String description;

    public MinCategoryDTO(Category entity) {
        description = entity.getDescription();

    }

    public String getDescription() {
        return description;
    }


}





