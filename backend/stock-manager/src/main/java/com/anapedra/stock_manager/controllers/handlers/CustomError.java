package com.anapedra.stock_manager.controllers.handlers;

import java.time.Instant;

/**
 * Classe DTO (Data Transfer Object) utilizada para padronizar o corpo da resposta
 * de erro (JSON) da API, frequentemente seguindo as diretrizes do RFC 7807 (Problem Details).
 */
public class CustomError {

    /**
     * Data e hora exata em que o erro ocorreu, em formato UTC.
     */
    private Instant timestamp;

    /**
     * Código de status HTTP da resposta (ex: 404, 422).
     */
    private Integer status;

    /**
     * Título curto e legível do erro (ex: "Resource Not Found" ou mensagem da exceção).
     */
    private String error;

    /**
     * O caminho (endpoint) da requisição que gerou o erro.
     */
    private String path;

    /**
     * Construtor para inicializar os detalhes do erro.
     *
     * @param timestamp Data e hora do erro.
     * @param status Código de status HTTP.
     * @param error Título do erro.
     * @param path Caminho da requisição.
     */
    public CustomError(Instant timestamp, Integer status, String error, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.path = path;
    }

    /**
     * Retorna a data e hora do erro.
     * @return O {@link Instant} do erro.
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Retorna o código de status HTTP.
     * @return O código de status.
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Retorna o título do erro.
     * @return A mensagem de erro.
     */
    public String getError() {
        return error;
    }

    /**
     * Retorna o caminho da requisição.
     * @return O path da requisição.
     */
    public String getPath() {
        return path;
    }
}