package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.Role;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.Objects;

public class RoleDTO implements Serializable {
    private static final long serialVersionUID=1L;

    private Long id;
    @NotBlank(message = "Campo obrigatório")
    private String authority;

    public RoleDTO(Long id, String authority) {
        this.id = id;
        this.authority = authority;
    }

    public RoleDTO(Role entity) {
        id = entity.getId();
        authority = entity.getAuthority();
    }

    public RoleDTO(){

    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoleDTO)) return false;
        RoleDTO roleDTO = (RoleDTO) o;
        return Objects.equals(getId(), roleDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }





}