package com.anapedra.stock_manager.services.exceptions;

/**
 * Exceção de aplicação utilizada para indicar que uma operação
 * de negócio não pode ser concluída devido a uma regra violada (ex: estoque insuficiente).
 */
public class BusinessRuleException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construtor padrão que aceita uma mensagem descritiva.
     * @param msg A mensagem de erro.
     */
    public BusinessRuleException(String msg) {
        super(msg);
    }

    /**
     * Construtor que aceita uma mensagem e a causa raiz (Throwable).
     * @param msg A mensagem de erro.
     * @param cause A causa raiz da exceção.
     */
    public BusinessRuleException(String msg, Throwable cause) {
        super(msg, cause);
    }
}