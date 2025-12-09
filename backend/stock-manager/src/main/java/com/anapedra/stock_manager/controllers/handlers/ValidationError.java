package com.anapedra.stock_manager.controllers.handlers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Extensão de {@link CustomError} utilizada especificamente para representar
 * erros de validação de dados de entrada (Validation Errors), correspondendo
 * tipicamente ao status HTTP 422 Unprocessable Entity.
 *
 * <p>Esta classe armazena uma lista de mensagens de erro detalhadas,
 * onde cada erro está associado a um campo específico do DTO.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 * @see CustomError
 * @see FieldMessage
 */
public class ValidationError extends CustomError {

    /**
     * Lista de mensagens de erro específicas do campo (field), detalhando
     * qual campo falhou na validação e por qual motivo.
     */
    private List<FieldMessage> errors = new ArrayList<>();

    /**
     * Construtor para inicializar o objeto de erro de validação.
     *
     * @param timestamp Data e hora exata em que o erro ocorreu.
     * @param status Código de status HTTP (normalmente 422).
     * @param error Título curto do erro (ex: "Validation Error").
     * @param path O caminho (endpoint) da requisição que gerou o erro.
     */
    public ValidationError(Instant timestamp, Integer status, String error, String path) {
        super(timestamp, status, error, path);
    }

    /**
     * Retorna a lista de erros detalhados por campo.
     *
     * @return Uma {@link List} de {@link FieldMessage}.
     */
    public List<FieldMessage> getErrors() {
        return errors;
    }

    /**
     * Adiciona uma nova mensagem de erro à lista, removendo qualquer mensagem
     * anterior que possa existir para o mesmo nome de campo.
     *
     * @param fieldName O nome do campo do DTO que falhou na validação.
     * @param message A mensagem detalhada do erro de validação.
     */
    public void addError(String fieldName, String message) {
        // Remove a mensagem existente para o campo antes de adicionar a nova (evitando duplicidade)
        errors.removeIf(x -> x.getFieldName().equals(fieldName));
        errors.add(new FieldMessage(fieldName, message));
    }
}