package com.anapedra.stock_manager.config;

import brave.context.slf4j.MDCScopeDecorator;
import brave.propagation.CurrentTraceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuração Spring responsável por configurar a integração
 * de rastreamento distribuído (Distributed Tracing), especificamente a
 * inclusão de metadados de rastreamento (Trace ID e Span ID) no MDC
 * (Mapped Diagnostic Context) do Logback/SLF4J.
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Configuration
public class TracingConfig {

    /**
     * Define um decorador de escopo que integra o contexto de rastreamento
     * (fornecido por Brave/Micrometer Tracing) com o MDC do SLF4J/Logback.
     *
     * <p>Isso permite que os Trace IDs e Span IDs sejam automaticamente
     * injetados nos logs de cada thread durante o processamento de uma requisição,
     * facilitando a correlação de eventos em um ambiente distribuído.</p>
     *
     * @return Uma instância de {@link CurrentTraceContext.ScopeDecorator} que utiliza o MDC.
     */
    @Bean
    public CurrentTraceContext.ScopeDecorator mdcScopeDecorator() {
        // Correto: cria decorator do MDC
        return MDCScopeDecorator.get();
    }
}