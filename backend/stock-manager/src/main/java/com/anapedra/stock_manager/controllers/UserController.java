package com.anapedra.stock_manager.controllers;

import com.anapedra.stock_manager.domain.dtos.UserDTO;
import com.anapedra.stock_manager.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador REST responsável por gerenciar operações relacionadas a usuários.
 *
 * <p>Expõe endpoints para funcionalidades como a obtenção de dados do usuário autenticado.
 * Utiliza anotações do Spring Security para controle de acesso baseado em papéis (RBAC)
 * e integra-se ao OpenAPI/Swagger para documentação da API.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
public class UserController {

    /**
     * Logger para registro de eventos e rastreamento de execução.
     */
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * Serviço responsável pela lógica de negócio e acesso a dados de usuários.
     */
    @Autowired
    private UserService service;

    /**
     * Endpoint para retornar os dados do usuário autenticado atualmente.
     *
     * <p>Requer que o usuário tenha o papel 'ROLE_ADMIN' ou 'ROLE_CLIENT'.</p>
     *
     * @return {@link ResponseEntity} contendo o {@link UserDTO} com os dados do usuário logado.
     */
    @Operation(summary = "Get authenticated user", description = "Returns information about the currently authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User data retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - user does not have the required role")
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe() {
        logger.info("GET /users/me iniciado. Tentativa de obter dados do usuário autenticado.");

        UserDTO dto = service.getMe();

        logger.info("GET /users/me finalizado. Status: 200 OK. Usuário ID: {}, Email: {}", dto.getId(), dto.getUsername());
        return ResponseEntity.ok(dto);
    }
}