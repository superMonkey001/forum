package cn.edu.hncj.forum.aop;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect
@Slf4j
public class LogAspect {
    @Pointcut("@annotation(cn.edu.hncj.forum.aop.LogAnnotation)")
    public void pt(){}
    @Around("pt()")
    public Object log(ProceedingJoinPoint joinPoint)  {
        //前置通知
        long beginTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long time = endTime - beginTime;
            //保存日志
            recordLog(joinPoint,time);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return result;
    }

    private void recordLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogAnnotation annotation = method.getAnnotation(LogAnnotation.class);
        log.info("===============start=================");
        log.info("module:{}",annotation.module());
        log.info("operator:{}",annotation.operator());
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        log.info("request method:{}",className+"."+methodName+"()");
        Object[] args = joinPoint.getArgs();
        String params = JSON.toJSONString(args[0]);
        log.info("params:{}",params);
        log.info("execute time:{}ms",time);
        log.info("===============end===================");
    }

}
