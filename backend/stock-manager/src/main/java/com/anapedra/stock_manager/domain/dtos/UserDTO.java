package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * DTO (Data Transfer Object) para a entidade Usuário (User).
 *
 * <p>Usado para transferir informações completas do usuário/cliente,
 * incluindo dados de registro, contato, login (username) e validações
 * ({@code @NotBlank}, {@code @Email}, {@code @CPF}).</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
public class UserDTO implements Serializable {
    private static final long serialVersionUID=1L;

    /**
     * O identificador único do usuário.
     */
    private Long id;

    /**
     * Nome completo do usuário. Campo obrigatório.
     */
    @NotBlank(message = "Campo obrigatório")
    private String name;

    /**
     * Momento em que o usuário foi registrado.
     */
    private Instant momentRegistration;

    /**
     * Momento da última atualização do registro.
     */
    private Instant momentUpdate;

    /**
     * O telefone principal do usuário. Mapeado a partir do campo 'phone' da entidade. Campo obrigatório.
     */
    @NotBlank(message = "Campo obrigatório")
    private String mainPhone;

    /**
     * O nome de usuário, que é o e-mail. Deve ser um e-mail válido.
     */
    @Email
    private String username;

    /**
     * ID de dados adicionais (campo não mapeado diretamente da entidade, presumivelmente para relacionamento).
     */
    private Long additionalDataId;

    /**
     * O Cadastro de Pessoa Física do usuário. Deve ser um CPF válido.
     */
    @CPF
    private String cpf;


    /**
     * Construtor padrão sem argumentos.
     */
    public UserDTO() {

    }

    /**
     * Construtor para inicializar todos os campos.
     */
    public UserDTO(Long id, String name, Instant momentRegistration, Instant momentUpdate, String mainPhone,
                   String username, Long additionalDataId, String cpf) {
        this.id = id;
        this.name = name;
        this.momentRegistration = momentRegistration;
        this.momentUpdate = momentUpdate;
        this.mainPhone = mainPhone;
        this.username = username;
        this.additionalDataId=additionalDataId;
        this.cpf=cpf;

    }


    /**
     * Construtor que inicializa o DTO a partir de uma entidade {@link User}.
     *
     * @param entity A entidade User de origem.
     */
    public UserDTO(User entity) {
        id=entity.getId();
        name=entity.getName();
        momentRegistration=entity.getMomentRegistration();
        momentUpdate=entity.getMomentUpdate();
        // Mapeamento de 'phone' da entidade para 'mainPhone' no DTO
        mainPhone=entity.getPhone();
        // Mapeamento de 'email' da entidade para 'username' no DTO
        username=entity.getEmail();
        cpf=entity.getCpf();
        // Nota: additionalDataId não é explicitamente mapeado aqui, é assumido como nulo/ignorado.
    }


    // --- Getters e Setters ---

    /**
     * Retorna o ID do usuário.
     * @return O ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o ID do usuário.
     * @param id O novo ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna o nome do usuário.
     * @return O nome.
     */
    public String getName() {
        return name;
    }

    /**
     * Define o nome do usuário.
     * @param name O novo nome.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retorna o momento de registro.
     * @return O Instant de registro.
     */
    public Instant getMomentRegistration() {
        return momentRegistration;
    }

    /**
     * Define o momento de registro.
     * @param momentRegistration O novo Instant.
     */
    public void setMomentRegistration(Instant momentRegistration) {
        this.momentRegistration = momentRegistration;
    }

    /**
     * Retorna o momento da última atualização.
     * @return O Instant de atualização.
     */
    public Instant getMomentUpdate() {
        return momentUpdate;
    }

    /**
     * Define o momento da última atualização.
     * @param momentUpdate O novo Instant.
     */
    public void setMomentUpdate(Instant momentUpdate) {
        this.momentUpdate = momentUpdate;
    }

    /**
     * Retorna o telefone principal (mainPhone).
     * @return O telefone principal.
     */
    public String getMainPhone() {
        return mainPhone;
    }

    /**
     * Define o telefone principal (mainPhone).
     * @param mainPhone O novo telefone.
     */
    public void setMainPhone(String mainPhone) {
        this.mainPhone = mainPhone;
    }

    /**
     * Retorna o username (e-mail) do usuário.
     * @return O username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Define o username (e-mail) do usuário.
     * @param username O novo username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Retorna o ID de dados adicionais.
     * @return O ID de dados adicionais.
     */
    public Long getAdditionalDataId() {
        return additionalDataId;
    }

    /**
     * Define o ID de dados adicionais.
     * @param additionalDataId O novo ID.
     */
    public void setAdditionalDataId(Long additionalDataId) {
        this.additionalDataId = additionalDataId;
    }

    /**
     * Retorna o CPF do usuário.
     * @return O CPF.
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Define o CPF do usuário.
     * @param cpf O novo CPF.
     */
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }


    /**
     * Compara dois objetos UserDTO com base no ID.
     * @param o O objeto a ser comparado.
     * @return true se os IDs forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDTO)) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(getId(), userDTO.getId());
    }

    /**
     * Calcula o hash code com base no ID.
     * @return O hash code do ID.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}