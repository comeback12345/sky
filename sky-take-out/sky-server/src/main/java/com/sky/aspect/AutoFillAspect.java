package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

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

         MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
         AutoFill autoFill = methodSignature.getMethod().getAnnotation(AutoFill.class);
         OperationType operationType = autoFill.value();

         //获取当前被拦截方法的参数——实体对象
         Object[] args = joinPoint.getArgs();
         if (args == null || args.length == 0){
             return;
         }

        Object object = args[0];

         //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        if(operationType==OperationType.INSERT){
            try {
                //为4个公共字段赋值
                Method setCreateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
                Method setCreateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setUpdateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setCreateTime.invoke(object,now);
                setCreateUser.invoke(object,currentId);
                setUpdateTime.invoke(object,now);
                setUpdateUser.invoke(object,currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else if(operationType==OperationType.UPDATE){
            try {
                //为2个公共字段赋值
                Method setUpdateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setUpdateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setUpdateTime.invoke(object,now);
                setUpdateUser.invoke(object,currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
}
