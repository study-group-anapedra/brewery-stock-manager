package com.anapedra.stock_manager.services;

import com.anapedra.stock_manager.domain.entities.User;
import com.anapedra.stock_manager.services.exceptions.ForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserService userService;

    // Injeção de dependência via construtor (melhor prática)
    public AuthService(UserService userService) {
        this.userService = userService;
    }

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

    public void validateAdmin() {
        User me = authenticatedUser();

        logger.info("User '{}' (ID: {}) attempting ADMIN access", me.getUsername(), me.getId());

        if (!me.hasRole("ROLE_ADMIN")) {
            logger.warn("Access denied for User '{}' (ID: {}). Required: Admin role.", me.getUsername(), me.getId());
            throw new ForbiddenException("Access denied: Admins only.");
        }

        logger.info("Access granted for ADMIN User '{}' (ID: {})", me.getUsername(), me.getId());
    }

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

    public boolean isAdmin() {
        User user = authenticatedUser();
        return user.hasRole("ROLE_ADMIN");
    }

    public User authenticatedUser() {
        return Optional.ofNullable(userService.authenticated())
                .orElseThrow(() -> new ForbiddenException("User not authenticated"));
    }
}