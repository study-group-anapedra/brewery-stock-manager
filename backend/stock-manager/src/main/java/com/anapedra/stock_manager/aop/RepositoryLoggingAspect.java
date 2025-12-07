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

@Aspect
@Component
public class RepositoryLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryLoggingAspect.class);

    /**
     * Define o Pointcut para TODOS os métodos em TODAS as interfaces
     * dentro do pacote com.anapedra.stock_manager.repositories.
     * O '..*' garante que ele pegue todas as interfaces, e o '.*(..)' pega todos os métodos.
     */
    @Pointcut("execution(* com.anapedra.stock_manager.repositories..*.*(..))")
    public void allRepositoryMethods() {}


    @Before("allRepositoryMethods()")
    public void logMethodCall(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String args = Arrays.toString(joinPoint.getArgs());

        logger.info("REPOSITORY BEFORE: Chamando método {} no {} com argumentos: {}",
                methodName, className, args);
    }


    @Around("allRepositoryMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - startTime;
            logger.debug("REPOSITORY AROUND: Método {} concluído em {} ms.",
                    joinPoint.getSignature().getName(), duration);

            return result;
        } catch (Throwable e) {
            logger.error("REPOSITORY ERROR: Erro ao executar método {}: {}",
                    joinPoint.getSignature().getName(), e.getMessage(), e);
            throw e;
        }
    }


    @AfterReturning(pointcut = "allRepositoryMethods()", returning = "result")
    public void logMethodReturn(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();

        if (result instanceof Page<?> page) {
            logger.info("REPOSITORY AFTER: Método {} retornou Page de {} itens (Total de páginas: {}).",
                    methodName, page.getNumberOfElements(), page.getTotalPages());
        } else if (result instanceof List<?> list) {
             logger.info("REPOSITORY AFTER: Método {} retornou uma lista de {} itens.",
                    methodName, list.size());
        } else if (result == null) {
            logger.info("REPOSITORY AFTER: Método {} concluído, retornou nulo (void/null).", methodName);
        }
    }
}