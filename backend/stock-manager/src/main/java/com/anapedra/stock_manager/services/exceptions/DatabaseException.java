package com.anapedra.stock_manager.services.exceptions;

/**
 * Exceção de aplicação utilizada para sinalizar erros relacionados a operações de
 * banco de dados que violam restrições de integridade, como a falha de integridade
 * referencial ao tentar excluir um registro que está sendo referenciado por outras
 * entidades (e.g., DataIntegrityViolationException).
 *
 * <p>Esta é uma exceção não verificada (RuntimeException) e deve ser lançada
 * na camada de Serviço para tratamento adequado no Controller (Handler).</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@SuppressWarnings("serial")
public class DatabaseException extends RuntimeException {

    /**
     * O serialVersionUID é um identificador universal para uma classe serializável.
     * Este valor é ignorado devido ao @SuppressWarnings("serial"), mas mantido por boas práticas.
     */
    // private static final long serialVersionUID = 1L; // Geralmente não é necessário quando @SuppressWarnings("serial") é usado.

    /**
     * Construtor para inicializar a exceção com uma mensagem detalhada.
     *
     * @param msg A mensagem descritiva do erro que ocorreu no banco de dados.
     */
    public DatabaseException(String msg) {
        super(msg);
    }
}