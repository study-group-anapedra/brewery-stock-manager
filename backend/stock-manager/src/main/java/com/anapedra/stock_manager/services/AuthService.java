package com.anapedra.stock_manager.services;

import com.anapedra.stock_manager.domain.entities.User;
import com.anapedra.stock_manager.services.exceptions.ForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Serviço responsável por gerenciar e validar a autorização (permissões) do usuário autenticado.
 *
 * <p>Utiliza o Spring Security para recuperar o usuário autenticado e verifica se ele
 * possui os papéis necessários (ex: ROLE_ADMIN) ou se o acesso é permitido
 * para o próprio recurso (Self access).</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserService userService;

    /**
     * Construtor para injeção de dependência do {@link UserService}.
     *
     * @param userService O serviço de usuário, necessário para obter o usuário autenticado.
     */
    public AuthService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Valida se o usuário autenticado é o dono do recurso (Self)
     * OU se ele possui o papel de Administrador (ROLE_ADMIN).
     *
     * <p>Caso contrário, lança uma {@link ForbiddenException}.</p>
     *
     * @param userId O ID do usuário cujo recurso está sendo acessado.
     * @throws ForbiddenException Se o usuário não for o próprio dono nem Admin.
     */
    public void validateSelfOrAdmin(Long userId) {
        User me = authenticatedUser();

        logger.info("User '{}' (ID: {}) attempting to access resource for User ID: {}",
                me.getUsername(), me.getId(), userId);

        if (!me.getId().equals(userId) && !me.hasRole("ROLE_ADMIN")) {
            logger.warn("Access denied for User '{}' (ID: {}). Required: Self or Admin role.",
                    me.getUsername(), me.getId());
            throw new ForbiddenException("Access denied: Only the user itself or an admin can perform this action.");
        }

        logger.info("Access granted for User '{}' (ID: {})", me.getUsername(), me.getId());
    }

    /**
     * Valida se o usuário autenticado possui o papel de Administrador (ROLE_ADMIN).
     *
     * <p>Caso contrário, lança uma {@link ForbiddenException}.</p>
     *
     * @throws ForbiddenException Se o usuário não possuir o papel de Admin.
     */
    public void validateAdmin() {
        User me = authenticatedUser();

        logger.info("User '{}' (ID: {}) attempting ADMIN access", me.getUsername(), me.getId());

        if (!me.hasRole("ROLE_ADMIN")) {
            logger.warn("Access denied for User '{}' (ID: {}). Required: Admin role.", me.getUsername(), me.getId());
            throw new ForbiddenException("Access denied: Admins only.");
        }

        logger.info("Access granted for ADMIN User '{}' (ID: {})", me.getUsername(), me.getId());
    }

    /**
     * Valida se o usuário autenticado é o dono do recurso (Self access).
     *
     * <p>Permissões de Administrador não são consideradas aqui; a validação é estritamente
     * para acesso próprio.</p>
     *
     * @param userId O ID do usuário cujo recurso está sendo acessado.
     * @throws ForbiddenException Se o usuário não for o próprio dono.
     */
    public void validateSelf(Long userId) {
        User me = authenticatedUser();

        logger.info("User '{}' (ID: {}) attempting to access their own resource (User ID: {})",
                me.getUsername(), me.getId(), userId);

        if (!me.getId().equals(userId)) {
            logger.warn("Access denied for User '{}' (ID: {}). Required: Self access only.",
                    me.getUsername(), me.getId());
            throw new ForbiddenException("Access denied: Only the user itself can perform this action.");
        }

        logger.info("Access granted for User '{}' (ID: {}) to their own resource", me.getUsername(), me.getId());
    }

    /**
     * Verifica se o usuário autenticado possui o papel de Administrador.
     *
     * @return {@code true} se o usuário for Admin, {@code false} caso contrário.
     */
    public boolean isAdmin() {
        User user = authenticatedUser();
        return user.hasRole("ROLE_ADMIN");
    }

    /**
     * Obtém a entidade {@link User} do usuário autenticado.
     *
     * <p>Lança uma {@link ForbiddenException} se não houver um usuário autenticado
     * no contexto de segurança (o que raramente deve acontecer em um ambiente seguro).</p>
     *
     * @return A entidade {@link User} autenticada.
     * @throws ForbiddenException Se nenhum usuário estiver autenticado.
     */
    public User authenticatedUser() {
        return Optional.ofNullable(userService.authenticated())
                .orElseThrow(() -> new ForbiddenException("User not authenticated"));
    }
}