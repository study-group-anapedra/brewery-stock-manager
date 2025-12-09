package com.anapedra.stock_manager.domain.entities;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

/**
 * Representa a entidade Cargo/Permissão (Role).
 * Esta classe mapeia a tabela "tb_role" no banco de dados.
 *
 * <p>Implementa a interface {@link GrantedAuthority} do Spring Security,
 * sendo essencial para definir as permissões de acesso dos usuários (ex: "ROLE_ADMIN", "ROLE_CLIENT").</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "tb_role")
public class Role implements GrantedAuthority {

    /**
     * O identificador único do cargo/permissão.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * O nome da autoridade/permissão no formato exigido pelo Spring Security (ex: "ROLE_ADMIN").
     */
    private String authority;

    /**
     * Construtor padrão sem argumentos.
     */
    public Role() {
    }

    /**
     * Construtor para inicializar o objeto Role.
     *
     * @param id O identificador do cargo.
     * @param authority O nome da autoridade/permissão.
     */
    public Role(Long id, String authority) {
       this.id = id;
       this.authority = authority;
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
     * Retorna a string da autoridade/permissão.
     *
     * <p>Este método é obrigatório por implementar {@link GrantedAuthority}.</p>
     * @return O nome da permissão (ex: "ROLE_ADMIN").
     */
    @Override
    public String getAuthority() {
       return authority;
    }

    /**
     * Define a string da autoridade/permissão.
     * @param authority O novo nome da permissão.
     */
    public void setAuthority(String authority) {
       this.authority = authority;
    }

    /**
     * Calcula o hash code com base na autoridade/permissão.
     * @return O hash code da autoridade.
     */
    @Override
    public int hashCode() {
       return Objects.hash(authority);
    }

    /**
     * Compara dois objetos Role com base na autoridade/permissão.
     *
     * @param obj O objeto a ser comparado.
     * @return true se as autoridades forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object obj) {
       if (this == obj)
          return true;
       if (obj == null)
          return false;
       if (getClass() != obj.getClass())
          return false;
       Role other = (Role) obj;
       return Objects.equals(authority, other.authority);
    }
}