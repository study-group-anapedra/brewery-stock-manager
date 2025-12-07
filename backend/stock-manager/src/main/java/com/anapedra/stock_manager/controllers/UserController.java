package com.anapedra.stock_manager.controllers;

import com.anapedra.stock_manager.domain.dtos.UserDTO;
import com.anapedra.stock_manager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger; // Import do Logger
import org.slf4j.LoggerFactory; // Import do LoggerFactory

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService service;

    /**
     * Endpoint: GET /users/me
     * Retorna os dados do usuário autenticado.
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
    @GetMapping(value = "/me")
    public ResponseEntity<UserDTO> getMe() {
        logger.info("CONTROLLER: GET /users/me iniciado. Tentativa de obter dados do usuário autenticado.");
        
        UserDTO dto = service.getMe();
        logger.info("CONTROLLER: GET /users/me finalizado. Status: 200 OK. Usuário ID: {}, Email: {}", dto.getId(), dto.getUsername());
        
        return ResponseEntity.ok(dto);
    }
}