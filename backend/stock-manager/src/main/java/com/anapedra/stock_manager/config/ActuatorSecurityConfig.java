package com.anapedra.stock_manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Classe de configuração do Spring Security dedicada a definir as regras
 * de acesso para os endpoints do Spring Boot Actuator (caminho "/actuator/**").
 *
 * <p>Esta configuração garante que o filtro de segurança seja aplicado
 * exclusivamente ao caminho do Actuator e permite o acesso irrestrito
 * a todos os seus endpoints.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Configuration
public class ActuatorSecurityConfig {

    /**
     * Define o {@link SecurityFilterChain} específico para o caminho "/actuator/**".
     *
     * <p>Utiliza {@code @Order(1)} para garantir que esta regra seja avaliada e
     * aplicada antes de quaisquer outras configurações de segurança, como as do
     * Servidor de Recursos (Resource Server).</p>
     *
     * <p>A configuração faz o seguinte:</p>
     * <ul>
     * <li>**{@code securityMatcher("/actuator/**")}**: Define que esta cadeia de filtros só se aplica a requisições para o Actuator.</li>
     * <li>**{@code anyRequest().permitAll()}**: Libera o acesso a todos os endpoints do Actuator (sem autenticação).</li>
     * <li>**{@code csrf().disable()}**: Desabilita a proteção CSRF, comum em APIs REST.</li>
     * </ul>
     *
     * @param http Objeto para configurar a segurança web.
     * @return O {@link SecurityFilterChain} configurado.
     * @throws Exception Em caso de erro na configuração.
     */
    @Bean
    @Order(1) // Este filter chain roda antes dos demais
    public SecurityFilterChain actuatorFilterChain(HttpSecurity http) throws Exception {

        http
                .securityMatcher("/actuator/**") //  Restrito SOMENTE ao actuator
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() //  libera todos os endpoints actuator
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}