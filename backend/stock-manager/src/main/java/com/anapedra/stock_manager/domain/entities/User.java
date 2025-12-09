package com.anapedra.stock_manager.domain.entities;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

/**
 * Representa a entidade Usuário (User).
 * Esta classe mapeia a tabela "tb_user" no banco de dados e gerencia informações
 * de clientes, bem como detalhes de autenticação e autorização.
 *
 * <p>Implementa {@link UserDetails} do Spring Security, permitindo que a aplicação
 * use esta classe para as operações de segurança.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "tb_user")
public class User implements UserDetails {

    /**
     * O identificador único do usuário.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome completo do usuário.
     */
    private String name;

    /**
     * Endereço de e-mail do usuário. Usado como nome de usuário (username)
     * no Spring Security. Deve ser único.
     */
    @Column(unique = true)
    private String email;

    /**
     * Número de telefone.
     */
    private String phone;

    /**
     * Data de nascimento.
     */
    private LocalDate birthDate;

    /**
     * Senha criptografada do usuário.
     */
    private String password;

    /**
     * Momento em que o usuário foi registrado.
     */
    private Instant momentRegistration;

    /**
     * Momento da última atualização do registro do usuário.
     */
    private Instant momentUpdate;

    /**
     * Cadastro de Pessoa Física.
     */
    private String cpf;

    /**
     * Lista de pedidos (Orders) feitos por este cliente. Relacionamento One-to-Many.
     */
    @OneToMany(mappedBy = "client")
    private List<Order> orders=new ArrayList<>();

    /**
     * Conjunto de cargos/permissões (Roles) atribuídos ao usuário. Relacionamento Many-to-Many.
     */
    @ManyToMany
    @JoinTable(name = "tb_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    /**
     * Construtor padrão sem argumentos.
     */
    public User() {
    }

    /**
     * Construtor para inicializar todos os campos.
     */
    public User(Long id, String name, String email, String phone, LocalDate birthDate,
                String password, Instant momentRegistration, Instant momentUpdate,
                String cpf) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.password = password;
        this.momentRegistration = momentRegistration;
        this.momentUpdate = momentUpdate;
        this.cpf = cpf;
    }

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
     * Retorna o nome completo do usuário.
     * @return O nome.
     */
    public String getName() {
        return name;
    }

    /**
     * Define o nome completo do usuário.
     * @param name O novo nome.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retorna o e-mail do usuário.
     * @return O e-mail.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Define o e-mail do usuário.
     * @param email O novo e-mail.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retorna o telefone do usuário.
     * @return O telefone.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Define o telefone do usuário.
     * @param phone O novo telefone.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Retorna a data de nascimento do usuário.
     * @return A data de nascimento.
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /**
     * Define a data de nascimento do usuário.
     * @param birthDate A nova data de nascimento.
     */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Retorna a senha criptografada do usuário.
     *
     * <p>Método obrigatório por implementar {@link UserDetails}.</p>
     * @return A senha.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Define a senha do usuário.
     * @param password A nova senha.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Retorna o momento de registro do usuário.
     * @return O Instant do registro.
     */
    public Instant getMomentRegistration() {
        return momentRegistration;
    }

    /**
     * Define o momento de registro do usuário.
     * @param momentRegistration O novo Instant.
     */
    public void setMomentRegistration(Instant momentRegistration) {
        this.momentRegistration = momentRegistration;
    }

    /**
     * Retorna o momento da última atualização do usuário.
     * @return O Instant da atualização.
     */
    public Instant getMomentUpdate() {
        return momentUpdate;
    }

    /**
     * Define o momento da última atualização do usuário.
     * @param momentUpdate O novo Instant.
     */
    public void setMomentUpdate(Instant momentUpdate) {
        this.momentUpdate = momentUpdate;
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
     * Retorna a lista de pedidos feitos pelo usuário.
     * @return A lista de Orders.
     */
    public List<Order> getOrders() {
        return orders;
    }

    /**
     * Retorna o conjunto de cargos/permissões (Roles) do usuário.
     * @return O Set de Role.
     */
    public Set<Role> getRoles() {
        return roles;
    }


    /**
     * Adiciona um cargo/permissão ao usuário.
     * @param role O cargo a ser adicionado.
     */
    public void addRole(Role role) {
        roles.add(role);
    }

    /**
     * Verifica se o usuário possui uma determinada permissão.
     * @param roleName O nome da permissão (ex: "ROLE_ADMIN").
     * @return true se o usuário tiver a permissão, false caso contrário.
     */
    public boolean hasRole(String roleName) {
        for (Role role : roles) {
            if (role.getAuthority().equals(roleName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Compara dois objetos User com base no ID.
     * @param o O objeto a ser comparado.
     * @return true se os IDs forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return Objects.equals(id, user.id);
    }

    /**
     * Calcula o hash code com base no ID.
     * @return O hash code do ID.
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    // --- Métodos de UserDetails (Spring Security) ---

    /**
     * Retorna as permissões concedidas ao usuário.
     *
     * <p>Este método é obrigatório por implementar {@link UserDetails}.</p>
     * @return O conjunto de {@link GrantedAuthority}.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    /**
     * Retorna o nome de usuário (username) usado para autenticação.
     * Neste caso, é o e-mail.
     *
     * <p>Método obrigatório por implementar {@link UserDetails}.</p>
     * @return O e-mail do usuário.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Indica se a conta do usuário não expirou.
     * Retorna sempre true, indicando que a expiração da conta não é controlada aqui.
     *
     * <p>Método obrigatório por implementar {@link UserDetails}.</p>
     * @return true.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica se o usuário não está bloqueado.
     * Retorna sempre true, indicando que o bloqueio da conta não é controlado aqui.
     *
     * <p>Método obrigatório por implementar {@link UserDetails}.</p>
     * @return true.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica se as credenciais do usuário (senha) não expiraram.
     * Retorna sempre true, indicando que a expiração de credenciais não é controlada aqui.
     *
     * <p>Método obrigatório por implementar {@link UserDetails}.</p>
     * @return true.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica se o usuário está habilitado ou desabilitado.
     * Retorna sempre true, indicando que o status de habilitação não é controlado aqui.
     *
     * <p>Método obrigatório por implementar {@link UserDetails}.</p>
     * @return true.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}