package com.anapedra.stock_manager.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Serviço de exemplo demonstrando a implementação e uso de logs
 * utilizando a interface {@link Logger} do SLF4J (Simple Logging Facade for Java).
 *
 * <p>Esta classe ilustra como registrar eventos em diferentes níveis (INFO e ERROR)
 * durante a execução de uma operação, o que é essencial para monitoramento
 * e diagnóstico em aplicações Spring Boot.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @see Logger
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class LoggingExampleService {

    private static final Logger logger = LoggerFactory.getLogger(LoggingExampleService.class);

    /**
     * Exemplo de um método que demonstra o uso de logging.
     *
     * <p>Simula uma operação que verifica um {@code resourceId} e registra
     * mensagens de INFO em caso de sucesso ou ERROR em caso de falha,
     * juntamente com a exceção capturada.</p>
     *
     * @param resourceId O ID do recurso que está sendo processado.
     */
    public void executeExampleOperation(long resourceId) {
        logger.info("SERVICE INFO: Iniciando processamento da operação. Recurso ID: {}", resourceId);
        
        try {
            if (resourceId % 2 != 0) {
                // Se o ID for ímpar, lançamos uma exceção para simular um erro de negócio
                throw new IllegalArgumentException("ID ímpar não permitido.");
            }
            logger.info("SERVICE INFO: A operação foi processada com sucesso. Recurso ID: {}", resourceId);
            
        } catch (IllegalArgumentException e) {
            // Registra o erro no nível ERROR, incluindo o ID do recurso e a mensagem de erro
            logger.error("SERVICE ERROR: Falha na operação para o Recurso ID {}. Motivo: {}", resourceId, e.getMessage());
        }
    }
}