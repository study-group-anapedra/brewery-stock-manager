package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.User;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

import java.io.Serializable;
import java.util.Objects;

public class ClientDTO implements Serializable {
    private static final long serialVersionUID=1L;
    private Long id;
    @NotBlank(message = "Campo obrigatório")
    private String name;
    @CPF
    private String cpf;

    public ClientDTO(){
    }

    public ClientDTO(Long id, String name, String cpf) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
    }

    public ClientDTO(User entyty) {
        id = entyty.getId();
        name = entyty.getName();
        cpf = entyty.getCpf();
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientDTO)) return false;
        ClientDTO clientDTO = (ClientDTO) o;
        return Objects.equals(id, clientDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}