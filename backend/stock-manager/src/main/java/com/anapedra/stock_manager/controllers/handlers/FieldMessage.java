package com.anapedra.stock_manager.controllers.handlers;

/**
 * Classe auxiliar utilizada para encapsular detalhes de um erro de validação
 * de campo individual.
 *
 * <p>Esta estrutura é usada dentro do {@link com.anapedra.stock_manager.controllers.handlers.ValidationError}
 * para fornecer ao cliente (frontend) informações claras sobre quais campos
 * do DTO falharam e por quê.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
public class FieldMessage {

    /**
     * O nome do campo do DTO que contém o erro de validação.
     */
    private String fieldName;

    /**
     * A mensagem detalhada do erro de validação para o campo.
     */
    private String message;

    /**
     * Construtor para inicializar a mensagem de erro de campo.
     *
     * @param fieldName O nome do campo do DTO que falhou.
     * @param message A mensagem descritiva do erro.
     */
    public FieldMessage(String fieldName, String message) {
        this.fieldName = fieldName;
        this.message = message;
    }

    /**
     * Retorna o nome do campo.
     *
     * @return O nome do campo (String).
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Retorna a mensagem de erro associada ao campo.
     *
     * @return A mensagem de erro (String).
     */
    public String getMessage() {
        return message;
    }
}