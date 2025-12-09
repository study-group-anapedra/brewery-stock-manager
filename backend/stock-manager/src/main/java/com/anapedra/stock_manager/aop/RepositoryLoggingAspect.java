package com.anapedra.stock_manager.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Aspecto de logging responsável por interceptar chamadas a métodos
 * em todas as interfaces de repositório (JPA/Spring Data) da aplicação.
 *
 * <p>Este aspecto realiza os seguintes tipos de logging:</p>
 * <ul>
 * <li>Registro de chamadas de método (Before).</li>
 * <li>Medição do tempo de execução e tratamento de erros (Around).</li>
 * <li>Registro do valor de retorno (AfterReturning), com tratamento especial para Page e List.</li>
 * </ul>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Aspect
@Component
public class RepositoryLoggingAspect {

    /**
     * Logger para registro de eventos e rastreamento de execução específicos de AOP.
     */
    private static final Logger logger = LoggerFactory.getLogger(RepositoryLoggingAspect.class);

    /**
     * Define o Pointcut para **TODOS** os métodos em **TODAS** as interfaces
     * dentro do pacote {@code com.anapedra.stock_manager.repositories} e seus subpacotes.
     * O padrão {@code execution(* com.anapedra.stock_manager.repositories..*.*(..))}
     * garante que todas as chamadas aos repositórios sejam interceptadas.
     */
    @Pointcut("execution(* com.anapedra.stock_manager.repositories..*.*(..))")
    public void allRepositoryMethods() {}


    /**
     * Advice executado **antes** da chamada de qualquer método de repositório.
     * Registra o nome do método, a classe e os argumentos passados.
     *
     * @param joinPoint O ponto de junção (JoinPoint) que fornece informações sobre a execução do método.
     */
    @Before("allRepositoryMethods()")
    public void logMethodCall(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String args = Arrays.toString(joinPoint.getArgs());

        logger.info("REPOSITORY BEFORE: Chamando método {} no {} com argumentos: {}",
                methodName, className, args);
    }


    /**
     * Advice executado **em torno** da chamada de qualquer método de repositório.
     * É usado para medir o tempo de execução e capturar exceções.
     *
     * @param joinPoint O ponto de junção processável (ProceedingJoinPoint) que permite executar o método original.
     * @return O resultado da execução do método original.
     * @throws Throwable Se o método original ou o advice lançarem uma exceção.
     */
    @Around("allRepositoryMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            // Executa o método do repositório original
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - startTime;
            logger.debug("REPOSITORY AROUND: Método {} concluído em {} ms.",
                    joinPoint.getSignature().getName(), duration);

            return result;
        } catch (Throwable e) {
            // Registra exceções lançadas pelo repositório ou pelo banco de dados
            logger.error("REPOSITORY ERROR: Erro ao executar método {}: {}",
                    joinPoint.getSignature().getName(), e.getMessage(), e);
            throw e;
        }
    }


    /**
     * Advice executado **após** um método de repositório retornar com sucesso (sem exceções).
     * Registra informações sobre o resultado, com tratamento especial para objetos
     * {@link Page} e {@link List}.
     *
     * @param joinPoint O ponto de junção.
     * @param result O valor de retorno do método.
     */
    @AfterReturning(pointcut = "allRepositoryMethods()", returning = "result")
    public void logMethodReturn(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();

        if (result instanceof Page<?> page) {
            // Caso o retorno seja uma paginação (Page)
            logger.info("REPOSITORY AFTER: Método {} retornou Page de {} itens (Total de páginas: {}).",
                    methodName, page.getNumberOfElements(), page.getTotalPages());
        } else if (result instanceof List<?> list) {
             // Caso o retorno seja uma lista (List)
             logger.info("REPOSITORY AFTER: Método {} retornou uma lista de {} itens.",
                    methodName, list.size());
        } else if (result == null) {
            // Caso o retorno seja nulo (void ou Optional.empty())
            logger.info("REPOSITORY AFTER: Método {} concluído, retornou nulo (void/null).", methodName);
        }
        // Nota: Outros tipos de retorno (como Optional ou DTOs individuais) não são logados detalhadamente aqui.
    }
}