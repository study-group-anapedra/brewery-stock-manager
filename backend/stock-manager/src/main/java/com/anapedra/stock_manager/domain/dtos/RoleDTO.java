package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.Role;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO (Data Transfer Object) para a entidade Cargo/Permissão (Role).
 *
 * <p>Usado para transferir dados de autoridade, geralmente no formato "ROLE_NOME".
 * Esta classe é fundamental para o sistema de segurança e controle de acesso.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
public class RoleDTO implements Serializable {
    private static final long serialVersionUID=1L;

    /**
     * O identificador único do cargo.
     */
    private Long id;

    /**
     * O nome da autoridade/permissão (ex: "ROLE_ADMIN"). Campo obrigatório.
     */
    @NotBlank(message = "Campo obrigatório")
    private String authority;

    /**
     * Construtor para inicializar todos os campos.
     *
     * @param id O ID do cargo.
     * @param authority O nome da autoridade.
     */
    public RoleDTO(Long id, String authority) {
        this.id = id;
        this.authority = authority;
    }

    /**
     * Construtor que inicializa o DTO a partir de uma entidade {@link Role}.
     *
     * @param entity A entidade Role de origem.
     */
    public RoleDTO(Role entity) {
        id = entity.getId();
        authority = entity.getAuthority();
    }

    /**
     * Construtor padrão sem argumentos.
     */
    public RoleDTO(){

    }

    /**
     * Compara dois objetos RoleDTO com base no ID.
     * @param o O objeto a ser comparado.
     * @return true se os IDs forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoleDTO)) return false;
        RoleDTO roleDTO = (RoleDTO) o;
        return Objects.equals(getId(), roleDTO.getId());
    }

    /**
     * Calcula o hash code com base no ID.
     * @return O hash code do ID.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    /**
     * Retorna o ID do cargo.
     * @return O ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o ID do cargo.
     * @param id O novo ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna o nome da autoridade.
     * @return A autoridade.
     */
    public String getAuthority() {
        return authority;
    }

    /**
     * Define o nome da autoridade.
     * @param authority A nova autoridade.
     */
    public void setAuthority(String authority) {
        this.authority = authority;
    }
}