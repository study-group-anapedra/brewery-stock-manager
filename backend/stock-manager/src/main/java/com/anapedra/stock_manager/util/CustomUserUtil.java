package com.anapedra.stock_manager.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Componente utilitário responsável por extrair informações do usuário autenticado
 * (principal) a partir do {@link SecurityContextHolder}.
 *
 * <p>Esta utilidade é projetada para funcionar em ambientes onde a autenticação
 * é baseada em tokens JWT (JSON Web Token), assumindo que o principal
 * no contexto de segurança é um objeto {@link Jwt}.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 * @see org.springframework.security.core.context.SecurityContextHolder
 * @see org.springframework.security.oauth2.jwt.Jwt
 */
@Component
public class CustomUserUtil {
    
    /**
     * Extrai o nome de usuário (username) do usuário autenticado a partir
     * do contexto de segurança atual.
     *
     * <p>Presume que o {@link Authentication#getPrincipal()} é um objeto {@link Jwt}
     * e que o claim "username" está presente no payload do token.</p>
     *
     * @return O nome de usuário (String) extraído do claim "username" do JWT.
     * @throws ClassCastException Se o principal não for do tipo {@link Jwt}.
     * @throws NullPointerException Se o contexto de segurança estiver vazio ou o claim "username" não existir.
     */
    public String getLoggedUsername() {
       // 1. Obtém o objeto Authentication do contexto de segurança.
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       
       // 2. Extrai o principal (que deve ser o JWT) e faz o cast.
       //    Atenção: Se o principal não for Jwt, isso lançará ClassCastException.
       Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
       
       // 3. Extrai o claim "username" do payload do JWT.
       return jwtPrincipal.getClaim("username");
    }
}