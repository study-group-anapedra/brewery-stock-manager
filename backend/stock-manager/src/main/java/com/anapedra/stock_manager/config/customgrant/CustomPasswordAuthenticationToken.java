package com.anapedra.stock_manager.config.customgrant;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Representa um token de autenticação específico para o fluxo de concessão de senha
 * customizado (Custom Password Grant).
 *
 * <p>Esta classe estende {@link OAuth2AuthorizationGrantAuthenticationToken} e
 * encapsula os dados necessários para autenticar o usuário e gerar um token de acesso,
 * incluindo o nome de usuário, a senha e os escopos solicitados.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 * @see com.anapedra.stock_manager.config.customgrant.CustomPasswordAuthenticationConverter
 * @see com.anapedra.stock_manager.config.customgrant.CustomPasswordAuthenticationProvider
 */
public class CustomPasswordAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    private static final long serialVersionUID = 1L;

    /**
     * Nome de usuário (username) extraído dos parâmetros da requisição.
     */
    private final String username;

    /**
     * Senha (password) extraída dos parâmetros da requisição.
     */
    private final String password;

    /**
     * Conjunto de escopos (scopes) solicitados pelo cliente.
     */
    private final Set<String> scopes;

    /**
     * Construtor para criar o token de autenticação de concessão de senha.
     *
     * <p>Os parâmetros "username" e "password" são extraídos do {@code additionalParameters}
     * que é preenchido pelo {@code CustomPasswordAuthenticationConverter}.</p>
     *
     * @param clientPrincipal O principal autenticado do cliente (OAuth2ClientAuthenticationToken).
     * @param scopes Conjunto de escopos solicitados (pode ser nulo).
     * @param additionalParameters Mapa de parâmetros adicionais da requisição (deve conter username e password).
     */
    public CustomPasswordAuthenticationToken(Authentication clientPrincipal,
          @Nullable Set<String> scopes, @Nullable Map<String, Object> additionalParameters) {

       // Chama o construtor pai com o Grant Type customizado "password"
       super(new AuthorizationGrantType("password"), clientPrincipal, additionalParameters);

       // Extrai username e password dos parâmetros adicionais
       this.username = (String) additionalParameters.get("username");
       this.password = (String) additionalParameters.get("password");

       // Inicializa os escopos como um conjunto imutável
       this.scopes = Collections.unmodifiableSet(
             scopes != null ? new HashSet<>(scopes) : Collections.emptySet());
    }

    /**
     * Retorna o nome de usuário.
     *
     * @return O username.
     */
    public String getUsername() {
       return this.username;
    }

    /**
     * Retorna a senha.
     *
     * @return A password.
     */
    public String getPassword() {
       return this.password;
    }

    /**
     * Retorna o conjunto imutável de escopos solicitados.
     *
     * @return O {@link Set} de escopos.
     */
    public Set<String> getScopes() {
       return this.scopes;
    }
}