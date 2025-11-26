package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.Category;

import java.io.Serializable;
import java.util.Objects;

public class CategoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;



    public CategoryDTO() {
    }

    public CategoryDTO(Long id,String name, String description) {
        this.id = id;
        this.name =name;
        this.description = description;

    }
    public CategoryDTO(Category entity) {
        id = entity.getId();
        description = entity.getDescription();
        name = entity.getName();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        CategoryDTO that = (CategoryDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
