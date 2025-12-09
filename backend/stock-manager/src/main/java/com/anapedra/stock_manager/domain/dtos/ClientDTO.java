package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.User;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO (Data Transfer Object) usado para transferir informações mínimas de um Cliente.
 *
 * <p>Representa uma versão simplificada da entidade {@link User}, focada nos dados
 * de identificação civil (nome e CPF).</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
public class ClientDTO implements Serializable {
    private static final long serialVersionUID=1L;

    /**
     * O identificador único do cliente.
     */
    private Long id;

    /**
     * O nome completo do cliente. Campo obrigatório.
     */
    @NotBlank(message = "Campo obrigatório")
    private String name;

    /**
     * O Cadastro de Pessoa Física do cliente. Deve ser um CPF válido.
     */
    @CPF
    private String cpf;

    /**
     * Construtor padrão sem argumentos.
     */
    public ClientDTO(){
    }

    /**
     * Construtor para inicializar todos os campos.
     *
     * @param id O ID do cliente.
     * @param name O nome do cliente.
     * @param cpf O CPF do cliente.
     */
    public ClientDTO(Long id, String name, String cpf) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
    }

    /**
     * Construtor que inicializa o DTO a partir de uma entidade {@link User}.
     *
     * @param entyty A entidade User de origem.
     */
    public ClientDTO(User entyty) {
        id = entyty.getId();
        name = entyty.getName();
        cpf = entyty.getCpf();
    }

    /**
     * Retorna o ID do cliente.
     * @return O ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o ID do cliente.
     * @param id O novo ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna o nome do cliente.
     * @return O nome.
     */
    public String getName() {
        return name;
    }

    /**
     * Define o nome do cliente.
     * @param name O novo nome.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retorna o CPF do cliente.
     * @return O CPF.
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Define o CPF do cliente.
     * @param cpf O novo CPF.
     */
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    /**
     * Compara dois objetos ClientDTO com base no ID.
     * @param o O objeto a ser comparado.
     * @return true se os IDs forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientDTO)) return false;
        ClientDTO clientDTO = (ClientDTO) o;
        return Objects.equals(id, clientDTO.id);
    }

    /**
     * Calcula o hash code com base no ID.
     * @return O hash code do ID.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}