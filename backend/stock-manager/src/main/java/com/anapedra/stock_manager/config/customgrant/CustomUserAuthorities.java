package com.anapedra.stock_manager.config.customgrant;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * DTO (Data Transfer Object) simples usado para encapsular o nome de usuário
 * e a coleção de autoridades (roles/permissões) do usuário autenticado no
 * contexto de um fluxo de concessão de senha customizado (Custom Password Grant).
 *
 * <p>Esta classe é utilizada pelo {@code CustomPasswordAuthenticationProvider}
 * para injetar os detalhes do usuário no {@code SecurityContext} e para customizar
 * as claims do JWT (via {@code AuthorizationServerConfig}).</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
public class CustomUserAuthorities {

    /**
     * O nome de usuário (username) do usuário autenticado.
     */
    private String username;

    /**
     * A coleção de autoridades (permissões ou roles) concedidas ao usuário.
     */
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Construtor para inicializar o objeto de autoridades do usuário.
     *
     * @param username O nome de usuário.
     * @param authorities A coleção de autoridades (roles/permissões).
     */
    public CustomUserAuthorities(String username, Collection<? extends GrantedAuthority> authorities) {
       this.username = username;
       this.authorities = authorities;
    }

    /**
     * Retorna o nome de usuário.
     *
     * @return O username.
     */
    public String getUsername() {
       return username;
    }

    /**
     * Retorna a coleção de autoridades concedidas.
     *
     * @return A {@link Collection} de {@link GrantedAuthority}.
     */
    public Collection<? extends GrantedAuthority> getAuthorities() {
       return authorities;
    }
}