package com.anapedra.stock_manager.config.customgrant;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Implementação de {@link AuthenticationConverter} responsável por converter
 * uma requisição HTTP de concessão de senha (Custom Password Grant) em um
 * objeto de autenticação específico ({@link CustomPasswordAuthenticationToken}).
 *
 * <p>Este conversor é usado pelo Spring Authorization Server no endpoint /oauth2/token
 * quando o {@code grant_type} é definido como "password".</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 * @see AuthenticationConverter
 * @see CustomPasswordAuthenticationToken
 */
public class CustomPasswordAuthenticationConverter implements AuthenticationConverter {

    /**
     * Tenta converter os parâmetros da requisição HTTP em um {@link Authentication}
     * (neste caso, {@link CustomPasswordAuthenticationToken}).
     *
     * <p>Verifica se o grant type é "password" e se os parâmetros obrigatórios
     * (username e password) estão presentes e são únicos.</p>
     *
     * @param request A requisição HTTP.
     * @return O objeto {@link CustomPasswordAuthenticationToken} se o grant type for "password",
     * ou {@code null} se o grant type for diferente (delegando para outros conversores).
     * @throws OAuth2AuthenticationException Se os parâmetros obrigatórios (username, password)
     * estiverem ausentes ou duplicados, ou o scope estiver duplicado.
     */
    @Nullable
    @Override
    public Authentication convert(HttpServletRequest request) {

       // 1. Verifica se o grant type é "password"
       String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);

       if (!"password".equals(grantType)) {
          return null; // Não é um grant type 'password', delega para outro conversor
       }

       MultiValueMap<String, String> parameters = getParameters(request);

       // 2. Valida e extrai o parâmetro 'scope' (OPCIONAL)
       String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
       if (StringUtils.hasText(scope) &&
             parameters.get(OAuth2ParameterNames.SCOPE).size() != 1) {
          throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
       }

       // 3. Valida e extrai o parâmetro 'username' (OBRIGATÓRIO)
       String username = parameters.getFirst(OAuth2ParameterNames.USERNAME);
       if (!StringUtils.hasText(username) ||
             parameters.get(OAuth2ParameterNames.USERNAME).size() != 1) {
          throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
       }

       // 4. Valida e extrai o parâmetro 'password' (OBRIGATÓRIO)
       String password = parameters.getFirst(OAuth2ParameterNames.PASSWORD);
       if (!StringUtils.hasText(password) ||
             parameters.get(OAuth2ParameterNames.PASSWORD).size() != 1) {
          throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
       }

       // 5. Converte o scope para um Set de Strings
       Set<String> requestedScopes = null;
       if (StringUtils.hasText(scope)) {
          requestedScopes = new HashSet<>(
                Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
       }

       // 6. Extrai e armazena os parâmetros adicionais
       Map<String, Object> additionalParameters = new HashMap<>();
       parameters.forEach((key, value) -> {
          // Inclui apenas parâmetros que não são grant_type ou scope (username e password serão injetados pelo provider)
          if (!key.equals(OAuth2ParameterNames.GRANT_TYPE) &&
                !key.equals(OAuth2ParameterNames.SCOPE)) {
             additionalParameters.put(key, value.get(0));
          }
       });

       // 7. Obtém o Principal (o Cliente autenticado) do SecurityContext
       Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();

       // 8. Cria e retorna o token de autenticação customizado
       return new CustomPasswordAuthenticationToken(clientPrincipal, requestedScopes, additionalParameters);
    }

    /**
     * Extrai todos os parâmetros da requisição HTTP e os coloca em um {@link MultiValueMap}.
     *
     * @param request A requisição HTTP.
     * @return Um {@link MultiValueMap} contendo os parâmetros da requisição.
     */
    private static MultiValueMap<String, String> getParameters(HttpServletRequest request) {

       Map<String, String[]> parameterMap = request.getParameterMap();
       MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>(parameterMap.size());

       // Itera sobre o Map<String, String[]> e converte para MultiValueMap<String, String>
       parameterMap.forEach((key, values) -> {
          if (values.length > 0) {
             for (String value : values) {
                parameters.add(key, value);
             }
          }
       });
       return parameters;
    }
}