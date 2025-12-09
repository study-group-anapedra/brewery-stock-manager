package com.anapedra.stock_manager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Classe de configuração do Spring Security para atuar como um Servidor de Recursos (Resource Server).
 *
 * <p>Esta configuração:</p>
 * <ul>
 * <li>Habilita a segurança via WebSecurity.</li>
 * <li>Habilita a segurança em nível de método ({@code @PreAuthorize}, etc.).</li>
 * <li>Configura a validação de tokens JWT (OAuth 2.0).</li>
 * <li>Define regras de CORS.</li>
 * <li>Fornece uma configuração de segurança especial para o console H2 em ambiente de teste.</li>
 * </ul>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ResourceServerConfig {

    /**
     * Valor injetado da propriedade 'cors.origins' contendo a lista de origens permitidas para CORS.
     */
    @Value("${cors.origins}")
    private String corsOrigins;

    /**
     * Configuração de segurança específica para o console H2 em ambiente de "test".
     *
     * <p>Permite acesso ao console H2, desabilitando CSRF e ajustes de cabeçalho
     * para permitir que o console seja exibido em um frame.</p>
     *
     * @param httpSecurity Objeto para configurar a segurança web.
     * @return O {@link SecurityFilterChain} configurado.
     * @throws Exception Em caso de erro na configuração.
     */
    @Bean
    @Profile("test")
    @Order(1)
    public SecurityFilterChain h2SecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
       // Configurações específicas para o path do H2 Console
       HttpSecurity http = httpSecurity.securityMatcher("/**");
       http.securityMatcher(PathRequest.toH2Console()).csrf(csrf -> csrf.disable())
             .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
       return http.build();
    }

    /**
     * Configuração principal do Servidor de Recursos.
     *
     * <p>Aplica as seguintes regras:</p>
     * <ul>
     * <li>Desabilita CSRF (stateless API).</li>
     * <li>Permite todas as requisições (a autorização detalhada é feita em nível de método via {@code @EnableMethodSecurity}).</li>
     * <li>Habilita o Resource Server com processamento de JWT.</li>
     * <li>Aplica a configuração de CORS.</li>
     * </ul>
     *
     * @param http Objeto para configurar a segurança web.
     * @return O {@link SecurityFilterChain} configurado.
     * @throws Exception Em caso de erro na configuração.
     */
    @Bean
    @Order(3)
    public SecurityFilterChain rsSecurityFilterChain(HttpSecurity http) throws Exception {

       http.csrf(csrf -> csrf.disable());
       // Permite o acesso a qualquer endpoint. A segurança real será delegada ao @EnableMethodSecurity.
       http.authorizeHttpRequests((authorize) -> authorize.anyRequest().permitAll());
       // Configura o OAuth 2.0 Resource Server para usar JWT
       http.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(Customizer.withDefaults()));
       // Aplica a configuração de CORS
       http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
       return http.build();
    }

    /**
     * Configura o conversor de autenticação JWT para extrair corretamente as
     * autoridades (roles/scopes) do token.
     *
     * <p>Assume que as autoridades estão na claim "authorities" e não utiliza prefixo.</p>
     *
     * @return O {@link JwtAuthenticationConverter} configurado.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
       // Converte as authorities (permissões/roles)
       JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
       grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities"); // Nome da claim no JWT onde as authorities estão
       grantedAuthoritiesConverter.setAuthorityPrefix(""); // Não usa prefixo (ex: "ROLE_")

       // Define o conversor principal que usará o conversor de authorities
       JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
       jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
       return jwtAuthenticationConverter;
    }

    /**
     * Configura a política de CORS (Cross-Origin Resource Sharing).
     *
     * @return O {@link CorsConfigurationSource} configurado.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {

       String[] origins = corsOrigins.split(",");

       CorsConfiguration corsConfig = new CorsConfiguration();
       corsConfig.setAllowedOriginPatterns(Arrays.asList(origins));
       corsConfig.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "PATCH"));
       corsConfig.setAllowCredentials(true);
       corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

       UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
       source.registerCorsConfiguration("/**", corsConfig); // Aplica a configuração a todos os paths
       return source;
    }

    /**
     * Registra o {@link CorsFilter} para garantir que a política de CORS seja aplicada
     * o mais cedo possível na cadeia de filtros do Spring.
     *
     * @return O {@link FilterRegistrationBean} para o CorsFilter.
     */
    @Bean
    FilterRegistrationBean<CorsFilter> filterRegistrationBeanCorsFilter() {
       FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(
             new CorsFilter(corsConfigurationSource()));
       bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
       return bean;
    }
}