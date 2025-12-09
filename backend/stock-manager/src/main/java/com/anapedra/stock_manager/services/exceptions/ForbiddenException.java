package com.anapedra.stock_manager.services.exceptions;

/**
 * Exceção de aplicação utilizada para sinalizar que a requisição é válida,
 * mas o usuário não tem permissão para acessar o recurso solicitado ou executar
 * a operação (status HTTP 403 Forbidden).
 *
 * <p>Esta é uma exceção não verificada (RuntimeException) e deve ser lançada
 * na camada de Serviço para ser interceptada e tratada pelo Controller Advice.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@SuppressWarnings("serial")
public class ForbiddenException extends RuntimeException {

    /**
     * Construtor para inicializar a exceção com uma mensagem detalhada.
     *
     * @param msg A mensagem descritiva do erro indicando a falta de permissão.
     */
    public ForbiddenException(String msg) {
        super(msg);
    }
}