package com.anapedra.stock_manager.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingExampleService {

    private static final Logger logger = LoggerFactory.getLogger(LoggingExampleService.class);

    /**
     * Exemplo de um método que demonstra o uso de logging.
     */
    public void executeExampleOperation(long resourceId) {
        logger.info("SERVICE INFO: Iniciando processamento da operação. Recurso ID: {}", resourceId);
        
        try {
            if (resourceId % 2 != 0) {
                throw new IllegalArgumentException("ID ímpar não permitido.");
            }
            logger.info("SERVICE INFO: A operação foi processada com sucesso. Recurso ID: {}", resourceId);
            
        } catch (IllegalArgumentException e) {
            logger.error("SERVICE ERROR: Falha na operação para o Recurso ID {}. Motivo: {}", resourceId, e.getMessage());
        }
    }
}