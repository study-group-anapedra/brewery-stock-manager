package com.anapedra.stock_manager.controllers.handlers;

import com.anapedra.stock_manager.services.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

/**
 * Componente centralizado de tratamento de exceções para a camada de controle (Controllers).
 *
 * <p>A anotação {@code @ControllerAdvice} permite que esta classe intercepte exceções
 * lançadas em qualquer {@code @RestController}, mapeando-as para respostas HTTP
 * padronizadas usando a estrutura {@link CustomError} e {@link ValidationError}.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 * @see CustomError
 * @see ValidationError
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    /**
     * Trata a exceção {@link ResourceNotFoundException}, que ocorre quando um recurso
     * solicitado por ID não é encontrado (Status HTTP 404 Not Found).
     *
     * @param e A exceção {@link ResourceNotFoundException} capturada.
     * @param request A requisição HTTP que gerou a exceção.
     * @return Uma {@link ResponseEntity} com status HTTP 404 e o corpo de erro customizado.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    /**
     * Trata a exceção {@link DatabaseException}, geralmente lançada por violações
     * de integridade de dados (ex: tentar deletar um item referenciado) (Status HTTP 400 Bad Request).
     *
     * @param e A exceção {@link DatabaseException} capturada.
     * @param request A requisição HTTP que gerou a exceção.
     * @return Uma {@link ResponseEntity} com status HTTP 400 e o corpo de erro customizado.
     */
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<CustomError> database(DatabaseException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    /**
     * Trata exceções lançadas pelo Spring ao falhar a validação de argumentos
     * anotados com {@code @Valid} (Status HTTP 422 Unprocessable Entity).
     *
     * @param e A exceção {@link MethodArgumentNotValidException} capturada.
     * @param request A requisição HTTP que gerou a exceção.
     * @return Uma {@link ResponseEntity} com status HTTP 422 e o corpo de erro {@link ValidationError}.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomError> methodArgumentNotValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationError err = new ValidationError(Instant.now(), status.value(), "Dados inválidos", request.getRequestURI());
        
        // Coleta todos os erros de campo e os adiciona ao ValidationError
        for (FieldError f : e.getBindingResult().getFieldErrors()) {
            err.addError(f.getField(), f.getDefaultMessage());
        }
        return ResponseEntity.status(status).body(err);
    }

    /**
     * Trata a exceção {@link ForbiddenException}, indicando que o usuário está
     * autenticado, mas não possui permissão para acessar o recurso (Status HTTP 403 Forbidden).
     *
     * @param e A exceção {@link ForbiddenException} capturada.
     * @param request A requisição HTTP que gerou a exceção.
     * @return Uma {@link ResponseEntity} com status HTTP 403 e o corpo de erro customizado.
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<CustomError> forbidden(ForbiddenException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    /**
     * Trata a exceção {@link UnauthorizedException}, indicando falha de autenticação
     * (ex: token inválido ou ausente) (Status HTTP 401 Unauthorized).
     *
     * @param e A exceção {@link UnauthorizedException} capturada.
     * @param request A requisição HTTP que gerou a exceção.
     * @return Uma {@link ResponseEntity} com status HTTP 401 e o corpo de erro customizado.
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<CustomError> unauthorized(UnauthorizedException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }


    /**
     * Trata a exceção {@link InsufficientStockException}, lançada quando uma operação
     * de saída de estoque falha por falta de quantidade disponível (Status HTTP 400 Bad Request).
     *
     * @param e A exceção {@link InsufficientStockException} capturada.
     * @param request A requisição HTTP que gerou a exceção.
     * @return Uma {@link ResponseEntity} com status HTTP 400 e o corpo de erro customizado.
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<CustomError> handleInsufficientStockException(InsufficientStockException e, HttpServletRequest request) {
        String error = "Insufficient stock";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        // Nota: Mantido o uso de e.getMessage() como o parâmetro 'path' para aderir estritamente ao código original, apesar de ser um padrão não-convencional para o campo 'path'.
        CustomError err = new CustomError(Instant.now(), status.value(), error, e.getMessage());
        return ResponseEntity.status(status).body(err);
    }
}