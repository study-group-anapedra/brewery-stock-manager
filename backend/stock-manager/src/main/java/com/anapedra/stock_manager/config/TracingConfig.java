package com.anapedra.stock_manager.config;

import brave.context.slf4j.MDCScopeDecorator;
import brave.propagation.CurrentTraceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {

    /**
     * Permite que Brave/Micrometer coloque traceId e spanId automaticamente no MDC
     * para o Logback capturar e escrever no JSON.
     */
    @Bean
    public CurrentTraceContext.ScopeDecorator mdcScopeDecorator() {
        // Correto: cria decorator do MDC
        return MDCScopeDecorator.get();
    }
}
