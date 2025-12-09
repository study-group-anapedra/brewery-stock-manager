package com.anapedra.stock_manager.config;

import com.anapedra.stock_manager.config.customgrant.CustomPasswordAuthenticationConverter;
import com.anapedra.stock_manager.config.customgrant.CustomPasswordAuthenticationProvider;
import com.anapedra.stock_manager.config.customgrant.CustomUserAuthorities;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * Classe de configuração principal do Servidor de Autorização OAuth 2.0 (Authorization Server).
 *
 * <p>Responsável por:</p>
 * <ul>
 * <li>Configurar o filtro de segurança para o endpoint de tokens.</li>
 * <li>Definir e registrar o cliente (Client) da aplicação.</li>
 * <li>Configurar a codificação (encoder) e decodificação (decoder) de JWT usando chaves RSA.</li>
 * <li>Customizar o fluxo de concessão de senha (Custom Password Grant).</li>
 * </ul>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Configuration
public class AuthorizationServerConfig {

    /**
     * ID do cliente injetado das propriedades de configuração.
     */
    @Value("${security.client-id}")
    private String clientId;

    /**
     * Segredo do cliente injetado das propriedades de configuração.
     */
    @Value("${security.client-secret}")
    private String clientSecret;

    /**
     * Duração do token JWT em segundos, injetada das propriedades.
     */
    @Value("${security.jwt.duration}")
    private Integer jwtDurationSeconds;

    /**
     * Serviço de detalhes do usuário injetado para autenticação.
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Configura o {@link SecurityFilterChain} específico para os endpoints do Servidor de Autorização.
     *
     * <p>Configura o endpoint de token ({@code /oauth2/token}) para usar o
     * {@link CustomPasswordAuthenticationConverter} e o {@link CustomPasswordAuthenticationProvider}.</p>
     *
     * @param httpSecurity Objeto para configurar a segurança web.
     * @return O {@link SecurityFilterChain} configurado.
     * @throws Exception Em caso de erro na configuração.
     */
    @Bean
    @Order(2) // Executado antes do Resource Server
    public SecurityFilterChain asSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {

       HttpSecurity http = httpSecurity.securityMatcher("/**");

       // Configuração padrão do Authorization Server
       http.with(OAuth2AuthorizationServerConfigurer.authorizationServer(), Customizer.withDefaults());

       // @formatter:off
       // Customiza o Token Endpoint para aceitar o fluxo 'password'
       http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
          .tokenEndpoint(tokenEndpoint -> tokenEndpoint
             .accessTokenRequestConverter(new CustomPasswordAuthenticationConverter())
             .authenticationProvider(new CustomPasswordAuthenticationProvider(
                 authorizationService(),
                 tokenGenerator(),
                 userDetailsService,
                 passwordEncoder()
             )));

       // Configuração para permitir a inspeção do JWT
       http.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(Customizer.withDefaults()));
       // @formatter:on

       return http.build();
    }

    /**
     * Define o serviço para persistir autorizações. Usado {@link InMemoryOAuth2AuthorizationService} para fins de demonstração/simplicidade.
     *
     * @return O serviço de autorização.
     */
    @Bean
    public OAuth2AuthorizationService authorizationService() {
       return new InMemoryOAuth2AuthorizationService();
    }

    /**
     * Define o serviço para persistir consentimentos de autorização. Usado {@link InMemoryOAuth2AuthorizationConsentService} para fins de demonstração/simplicidade.
     *
     * @return O serviço de consentimento de autorização.
     */
    @Bean
    public OAuth2AuthorizationConsentService oAuth2AuthorizationConsentService() {
       return new InMemoryOAuth2AuthorizationConsentService();
    }

    /**
     * Define o codificador de senhas (PasswordEncoder) a ser usado para hashing de senhas.
     *
     * @return Uma instância de {@link BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();
    }

    /**
     * Define o repositório de clientes registrados, que armazena informações sobre a aplicação cliente.
     *
     * <p>Registra um cliente com tipo de concessão 'password' customizado, scopes 'read' e 'write'.</p>
     *
     * @return O repositório de clientes registrado.
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
       // @formatter:off
       RegisteredClient registeredClient = RegisteredClient
          .withId(UUID.randomUUID().toString())
          .clientId(clientId)
          .clientSecret(passwordEncoder().encode(clientSecret)) // O segredo deve ser hasheado
          .scope("read")
          .scope("write")
          .authorizationGrantType(new AuthorizationGrantType("password")) // Define o tipo de concessão customizado
          .tokenSettings(tokenSettings())
          .clientSettings(clientSettings())
          .build();
       // @formatter:on

       return new InMemoryRegisteredClientRepository(registeredClient);
    }

    /**
     * Configurações para o token, como formato (SELF_CONTAINED = JWT) e tempo de vida.
     *
     * @return O {@link TokenSettings} configurado.
     */
    @Bean
    public TokenSettings tokenSettings() {
       // @formatter:off
       return TokenSettings.builder()
          .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED) // Define JWT como formato do token
          .accessTokenTimeToLive(Duration.ofSeconds(jwtDurationSeconds)) // Define a duração
          .build();
       // @formatter:on
    }

    /**
     * Configurações gerais do cliente.
     *
     * @return O {@link ClientSettings} configurado.
     */
    @Bean
    public ClientSettings clientSettings() {
       return ClientSettings.builder().build();
    }

    /**
     * Configurações do próprio Servidor de Autorização.
     *
     * @return O {@link AuthorizationServerSettings} configurado.
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
       return AuthorizationServerSettings.builder().build();
    }

    /**
     * Define o gerador de tokens, que inclui o {@link JwtGenerator} e o {@link OAuth2AccessTokenGenerator}.
     *
     * @return O gerador de tokens delegado.
     */
    @Bean
    public OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator() {
       NimbusJwtEncoder jwtEncoder = new NimbusJwtEncoder(jwkSource());
       JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
       jwtGenerator.setJwtCustomizer(tokenCustomizer()); // Adiciona o customizador de claims
       
       OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
       
       return new DelegatingOAuth2TokenGenerator(jwtGenerator, accessTokenGenerator);
    }

    /**
     * Customiza as claims do JWT (Payload) para incluir as autoridades (roles) e o nome de usuário.
     *
     * @return O customizador de token.
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
       return context -> {
          // Obtém o principal (o usuário autenticado) do contexto de autenticação
          OAuth2ClientAuthenticationToken principal = context.getPrincipal();
          CustomUserAuthorities user = (CustomUserAuthorities) principal.getDetails();
          // Mapeia authorities para lista de Strings
          List<String> authorities = user.getAuthorities().stream().map(x -> x.getAuthority()).toList();
          
          // Adiciona as claims apenas para o token de acesso (access_token)
          if (context.getTokenType().getValue().equals("access_token")) {
             // @formatter:off
             context.getClaims()
                .claim("authorities", authorities) // Adiciona as roles
                .claim("username", user.getUsername()); // Adiciona o username
             // @formatter:on
          }
       };
    }

    /**
     * Define o decodificador de JWT para validar e ler tokens de acesso.
     *
     * @param jwkSource Fonte das chaves públicas RSA.
     * @return O {@link JwtDecoder} configurado.
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
       return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * Define a fonte das chaves JWK (JSON Web Key), contendo a chave pública RSA para validação.
     *
     * @return A fonte das chaves JWK.
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
       RSAKey rsaKey = generateRsa(); // Gera o par de chaves
       JWKSet jwkSet = new JWKSet(rsaKey);
       // Implementação simples que retorna a JWKSet gerada
       return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    /**
     * Gera uma chave RSA (usada para assinar/verificar o JWT) a partir de um par de chaves.
     *
     * @return A {@link RSAKey} que representa o par de chaves.
     */
    private static RSAKey generateRsa() {
       KeyPair keyPair = generateRsaKey();
       RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
       RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
       // Constrói a chave RSA com o ID único para identificação
       return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
    }

    /**
     * Gera o par de chaves RSA de 2048 bits usando {@link KeyPairGenerator}.
     *
     * @return O par de chaves {@link KeyPair}.
     * @throws IllegalStateException Se a geração da chave falhar.
     */
    private static KeyPair generateRsaKey() {
       KeyPair keyPair;
       try {
          KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
          keyPairGenerator.initialize(2048);
          keyPair = keyPairGenerator.generateKeyPair();
       } catch (Exception ex) {
          throw new IllegalStateException(ex);
       }
       return keyPair;
    }
}