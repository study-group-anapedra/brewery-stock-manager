package com.anapedra.stock_manager.services.exceptions;

/**
 * Exceção de aplicação utilizada para sinalizar que a requisição não possui credenciais
 * de autenticação válidas ou suficientes para acessar o recurso.
 * Corresponde ao status HTTP 401 (Unauthorized).
 *
 * <p>Esta é uma exceção não verificada (RuntimeException) e deve ser lançada
 * na camada de Serviço, sendo tratada por um Controller Advice para retornar
 * uma resposta adequada ao cliente, tipicamente pedindo autenticação.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
public class UnauthorizedException extends RuntimeException{

    /**
     * O serialVersionUID é um identificador universal para a classe serializável.
     */
    private static final long serialVersionUID=1L;

    /**
     * Construtor para inicializar a exceção com uma mensagem detalhada.
     *
     * @param msg A mensagem descritiva do erro, geralmente indicando a falha de autenticação.
     */
    public UnauthorizedException (String msg){
        super(msg);
    }
}