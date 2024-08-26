package com.sky.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 自定义切面，实现公共字段自动填充处理逻辑
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..))  && @annotation(com.sky.annotation.AutoFill)")//匹配所有的类里面所有的方法和所有的参数类型
    public void autoFillPointcut(){};

    /**
     * 前置通知，在通知中进行公共字段的赋值
     */
    @Before("autoFillPointcut()")//前置通知注解，匹配上execution(* com.sky.mapper.*.*(..)执行方法
    public void autoFill(JoinPoint joinPoint){
         log.info("开始进行公共字段的自动填充...");
    }
}
