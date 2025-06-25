package com.example.userroleservice.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class ExecutionTimeAspect {
    private static final Logger logger = LoggerFactory.getLogger(ExecutionTimeAspect.class);

    @Around("@annotation(com.example.userroleservice.annotation.ExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed(); // proceed to the target method

        long duration = System.currentTimeMillis() - start;
        logger.info("Execution time of {}.{} = {} ms",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                duration);

        return result;
    }
}
