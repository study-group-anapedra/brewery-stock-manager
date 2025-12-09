package com.anapedra.stock_manager.services.exceptions;

/**
 * Exceção de aplicação utilizada para sinalizar que um recurso (entidade)
 * solicitado pelo identificador (ID) não foi encontrado no sistema (status HTTP 404 Not Found).
 *
 * <p>Esta é uma exceção não verificada (RuntimeException) e deve ser lançada
 * na camada de Serviço, sendo tratada por um Controller Advice para retornar
 * uma resposta adequada ao cliente.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@SuppressWarnings("serial")
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Construtor para inicializar a exceção com uma mensagem detalhada.
     *
     * @param msg A mensagem descritiva do erro, geralmente contendo o ID do recurso não encontrado.
     */
    public ResourceNotFoundException(String msg) {
        super(msg);
    }
}