package com.anapedra.stock_manager.services;

import com.anapedra.stock_manager.domain.entities.Role;
import com.anapedra.stock_manager.domain.entities.User;
import com.anapedra.stock_manager.services.exceptions.ForbiddenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;


    @Mock
    private UserService userService;

    private User adminUser;
    private User regularUser1;
    private User regularUser2;
    private Long targetId;
    private Role adminRole;
    private Role clientRole;

    @BeforeEach
    void setUp() {

        adminRole = new Role(1L, "ROLE_ADMIN");
        clientRole = new Role(2L, "ROLE_CLIENT");


        Instant now = Instant.now();
        
        adminUser = new User(1L, "Admin Name", "admin@example.com", "999999999", LocalDate.of(1990, 1, 1),
                "password", now, now, "11111111111");
        adminUser.addRole(adminRole);

        regularUser1 = new User(2L, "User 1 Name", "user1@example.com", "988888888", LocalDate.of(1995, 5, 5),
                "password", now, now, "22222222222");
        regularUser1.addRole(clientRole);

        regularUser2 = new User(3L, "User 2 Name", "user2@example.com", "977777777", LocalDate.of(2000, 10, 10),
                "password", now, now, "33333333333");
        regularUser2.addRole(clientRole);

        targetId = regularUser1.getId();
    }

    void mockAuthenticatedUser(User user) {
        when(userService.authenticated()).thenReturn(user);
    }



    @Test
    @DisplayName("authenticatedUser deve retornar o usuário logado quando existe")
    void authenticatedUser_shouldReturnUser_whenAuthenticated() {
        mockAuthenticatedUser(regularUser1);
        User result = authService.authenticatedUser();
        assertNotNull(result);
        assertEquals(regularUser1.getId(), result.getId());
    }

    @Test
    @DisplayName("authenticatedUser deve lançar ForbiddenException quando não há usuário")
    void authenticatedUser_shouldThrowForbiddenException_whenNotAuthenticated() {
        when(userService.authenticated()).thenReturn(null);
        assertThrows(ForbiddenException.class, () -> authService.authenticatedUser());
    }



    @Test
    @DisplayName("validateAdmin deve permitir acesso para ROLE_ADMIN")
    void validateAdmin_shouldGrantAccess_whenUserIsAdmin() {
        mockAuthenticatedUser(adminUser);
        assertDoesNotThrow(() -> authService.validateAdmin());
    }

    @Test
    @DisplayName("validateAdmin deve lançar ForbiddenException para ROLE_CLIENT")
    void validateAdmin_shouldDenyAccess_whenUserIsNotAdmin() {
        mockAuthenticatedUser(regularUser1);
        assertThrows(ForbiddenException.class, () -> authService.validateAdmin());
    }



    @Test
    @DisplayName("validateSelfOrAdmin deve permitir acesso (Self) quando ID logado é igual ao ID do recurso")
    void validateSelfOrAdmin_shouldGrantAccess_whenAccessingSelf() {
        mockAuthenticatedUser(regularUser1);
        
        assertDoesNotThrow(() -> authService.validateSelfOrAdmin(regularUser1.getId()));
    }

    @Test
    @DisplayName("validateSelfOrAdmin deve permitir acesso (Admin) quando usuário é ADMIN e acessa outro ID")
    void validateSelfOrAdmin_shouldGrantAccess_whenUserIsAdminAndAccessingOther() {
        mockAuthenticatedUser(adminUser);
        
        assertDoesNotThrow(() -> authService.validateSelfOrAdmin(targetId));
    }

    @Test
    @DisplayName("validateSelfOrAdmin deve lançar ForbiddenException quando usuário comum acessa ID de outro")
    void validateSelfOrAdmin_shouldDenyAccess_whenRegularUserAccessesOther() {
        mockAuthenticatedUser(regularUser2);
        
        assertThrows(ForbiddenException.class, () -> authService.validateSelfOrAdmin(targetId));
    }


    @Test
    @DisplayName("validateSelf deve permitir acesso quando ID logado é igual ao ID do recurso")
    void validateSelf_shouldGrantAccess_whenAccessingSelf() {
        mockAuthenticatedUser(regularUser1);

        assertDoesNotThrow(() -> authService.validateSelf(regularUser1.getId()));
    }

    @Test
    @DisplayName("validateSelf deve lançar ForbiddenException quando acessando ID de outro usuário")
    void validateSelf_shouldDenyAccess_whenAccessingOther() {
        mockAuthenticatedUser(regularUser2);

        assertThrows(ForbiddenException.class, () -> authService.validateSelf(targetId));
    }

    @Test
    @DisplayName("validateSelf deve lançar ForbiddenException quando Admin acessa outro ID (Admin não é Self)")
    void validateSelf_shouldDenyAccess_whenAdminAccessesOther() {
        mockAuthenticatedUser(adminUser);
        
        assertThrows(ForbiddenException.class, () -> authService.validateSelf(targetId));
    }


    @Test
    @DisplayName("isAdmin deve retornar true quando o usuário logado é ADMIN")
    void isAdmin_shouldReturnTrue_whenUserIsAdmin() {
        mockAuthenticatedUser(adminUser);
        assertTrue(authService.isAdmin());
    }

    @Test
    @DisplayName("isAdmin deve retornar false quando o usuário logado não é ADMIN")
    void isAdmin_shouldReturnFalse_whenUserIsNotAdmin() {
        mockAuthenticatedUser(regularUser1);
        assertFalse(authService.isAdmin());
    }
}