package com.anapedra.stock_manager.services.exceptions;

/**
 * Exceção de Regra de Negócio utilizada para indicar que uma operação
 * de manipulação de estoque (saída, perda, etc.) não pode ser concluída
 * porque a quantidade solicitada é maior do que a quantidade disponível
 * no estoque atual.
 *
 * <p>Esta é uma exceção não verificada (RuntimeException) e deve ser tratada
 * na camada de Serviço, resultando tipicamente em um status HTTP 400 Bad Request
 * ou 422 Unprocessable Entity.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
public class InsufficientStockException extends RuntimeException {

    /**
     * O serialVersionUID é um identificador universal para uma classe serializável.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construtor para inicializar a exceção com uma mensagem detalhada.
     *
     * @param message A mensagem descritiva do erro, geralmente indicando o produto e as quantidades envolvidas.
     */
    public InsufficientStockException(String message) {
        super(message);
    }
}