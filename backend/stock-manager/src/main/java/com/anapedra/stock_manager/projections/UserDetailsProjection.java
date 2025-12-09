package com.anapedra.stock_manager.projections;

/**
 * Interface de Projeção (Projection) utilizada para carregar dados essenciais
 * de segurança (Usuário e Permissões/Roles) de forma otimizada,
 * tipicamente em consultas customizadas (ex: SQL Nativo) no Spring Data JPA.
 *
 * <p>Esta projeção é fundamental para a funcionalidade de autenticação
 * do Spring Security, pois permite buscar as credenciais e as autoridades
 * de um usuário de forma eficiente.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
public interface UserDetailsProjection {

    /**
     * Retorna o nome de usuário (geralmente o e-mail ou login).
     * @return O username.
     */
    String getUsername();

    /**
     * Retorna a senha criptografada do usuário.
     * @return A senha.
     */
    String getPassword();

    /**
     * Retorna o ID do cargo/permissão.
     * @return O ID do Role.
     */
    Long getRoleId();

    /**
     * Retorna o nome da autoridade/permissão (ex: "ROLE_ADMIN").
     * @return O nome da autoridade.
     */
    String getAuthority();
}