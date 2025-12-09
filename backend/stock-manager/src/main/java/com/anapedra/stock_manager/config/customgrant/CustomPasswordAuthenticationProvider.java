package com.anapedra.stock_manager.config.customgrant;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.Assert;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provedor de autenticação customizado para o fluxo de concessão de senha (Custom Password Grant).
 *
 * <p>Responsável por:</p>
 * <ul>
 * <li>Autenticar o cliente (Client) da aplicação.</li>
 * <li>Carregar e autenticar o usuário final usando {@link UserDetailsService} e {@link PasswordEncoder}.</li>
 * <li>Verificar escopos autorizados.</li>
 * <li>Gerar o token de acesso (JWT) e salvá-lo via {@link OAuth2AuthorizationService}.</li>
 * </ul>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 * @see AuthenticationProvider
 * @see CustomPasswordAuthenticationToken
 */
public class CustomPasswordAuthenticationProvider implements AuthenticationProvider {

    private static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";
    
    private final OAuth2AuthorizationService authorizationService;
    private final UserDetailsService userDetailsService;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
    private final PasswordEncoder passwordEncoder;
    
    private String username = "";
    private String password = "";
    private Set<String> authorizedScopes = new HashSet<>();

    /**
     * Construtor para injeção de dependências essenciais.
     *
     * @param authorizationService Serviço para persistir autorizações.
     * @param tokenGenerator Gerador para criar tokens de acesso e refresh.
     * @param userDetailsService Serviço para carregar detalhes do usuário.
     * @param passwordEncoder Codificador/Verificador de senhas.
     */
    public CustomPasswordAuthenticationProvider(OAuth2AuthorizationService authorizationService,
          OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
          UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
       
       Assert.notNull(authorizationService, "authorizationService cannot be null");
       Assert.notNull(tokenGenerator, "TokenGenerator cannot be null");
       Assert.notNull(userDetailsService, "UserDetailsService cannot be null");
       Assert.notNull(passwordEncoder, "PasswordEncoder cannot be null");
       this.authorizationService = authorizationService;
       this.tokenGenerator = tokenGenerator;
       this.userDetailsService = userDetailsService;
       this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Lógica principal de autenticação.
     *
     * @param authentication O token {@link CustomPasswordAuthenticationToken} recebido do conversor.
     * @return O token {@link OAuth2AccessTokenAuthenticationToken} contendo o token de acesso gerado.
     * @throws AuthenticationException Se o cliente for inválido, o usuário/senha estiver incorreto, ou a geração do token falhar.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
       
       CustomPasswordAuthenticationToken customPasswordAuthenticationToken = (CustomPasswordAuthenticationToken) authentication;
       
       // 1. Autenticação do Cliente
       OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(customPasswordAuthenticationToken);
       RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();
       
       username = customPasswordAuthenticationToken.getUsername();
       password = customPasswordAuthenticationToken.getPassword();
       
       // 2. Autenticação do Usuário
       UserDetails user = null;
       try {
          user = userDetailsService.loadUserByUsername(username);
       } catch (UsernameNotFoundException e) {
          throw new OAuth2AuthenticationException("Invalid credentials");
       }
             
       // 3. Validação da Senha
       if (!passwordEncoder.matches(password, user.getPassword()) || !user.getUsername().equals(username)) {
          throw new OAuth2AuthenticationException("Invalid credentials");
       }
       
       // 4. Determinação dos Scopes Autorizados (Scopes do usuário que o cliente solicitou)
       authorizedScopes = user.getAuthorities().stream()
             .map(scope -> scope.getAuthority())
             .filter(scope -> registeredClient.getScopes().contains(scope))
             .collect(Collectors.toSet());
       
       // 5. Configuração do Contexto de Segurança (Injeta detalhes do usuário para customização do JWT)
       OAuth2ClientAuthenticationToken oAuth2ClientAuthenticationToken = (OAuth2ClientAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
       CustomUserAuthorities customPasswordUser = new CustomUserAuthorities(username, user.getAuthorities());
       oAuth2ClientAuthenticationToken.setDetails(customPasswordUser);
       
       var newcontext = SecurityContextHolder.createEmptyContext();
       newcontext.setAuthentication(oAuth2ClientAuthenticationToken);
       SecurityContextHolder.setContext(newcontext);
       
       // 6. Construção dos Builders de Contexto e Autorização
       DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
             .registeredClient(registeredClient)
             .principal(clientPrincipal)
             .authorizationServerContext(AuthorizationServerContextHolder.getContext())
             .authorizedScopes(authorizedScopes)
             .authorizationGrantType(new AuthorizationGrantType("password"))
             .authorizationGrant(customPasswordAuthenticationToken);
       
       OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
             .attribute(Principal.class.getName(), clientPrincipal)
             .principalName(clientPrincipal.getName())
             .authorizationGrantType(new AuthorizationGrantType("password"))
             .authorizedScopes(authorizedScopes);
       
       // 7. Geração do Access Token
       OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
       OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
       
       if (generatedAccessToken == null) {
          OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                "The token generator failed to generate the access token.", ERROR_URI);
          throw new OAuth2AuthenticationException(error);
       }

       // 8. Criação do Objeto OAuth2AccessToken
       OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
             generatedAccessToken.getTokenValue(), generatedAccessToken.getIssuedAt(),
             generatedAccessToken.getExpiresAt(), tokenContext.getAuthorizedScopes());
       
       // 9. Armazenamento do Token e Metadados
       if (generatedAccessToken instanceof ClaimAccessor) {
          authorizationBuilder.token(accessToken, (metadata) ->
                metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, ((ClaimAccessor) generatedAccessToken).getClaims()));
       } else {
          authorizationBuilder.accessToken(accessToken);
       }
             
       OAuth2Authorization authorization = authorizationBuilder.build();
       this.authorizationService.save(authorization); // Persiste a autorização
       
       // 10. Retorna o Token de Autenticação Final
       return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken);
    }

    /**
     * Indica se este provedor suporta a classe de token {@link CustomPasswordAuthenticationToken}.
     *
     * @param authentication A classe de autenticação a ser verificada.
     * @return {@code true} se for suportada, {@code false} caso contrário.
     */
    @Override
    public boolean supports(Class<?> authentication) {
       return CustomPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * Extrai e valida o Principal do Cliente (o Cliente deve estar autenticado).
     *
     * @param authentication O objeto de autenticação que contém o Principal do Cliente.
     * @return O token de autenticação do cliente.
     * @throws OAuth2AuthenticationException Se o cliente não estiver autenticado.
     */
    private static OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {
       
       OAuth2ClientAuthenticationToken clientPrincipal = null;
       // Tenta extrair o OAuth2ClientAuthenticationToken
       if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
          clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
       }
       
       // Verifica se o cliente foi extraído e está autenticado
       if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
          return clientPrincipal;
       }
       
       // Caso contrário, lança erro de cliente inválido
       throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
    }
}