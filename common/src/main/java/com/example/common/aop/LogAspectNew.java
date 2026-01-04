package com.example.common.aop;

import com.example.common.utils.UserUtils;
import com.example.common.domain.entity.User;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspectNew {


    private static final Logger log = LoggerFactory.getLogger(LogAspectNew.class);

    /** 计算操作消耗时间 */
    private static final ThreadLocal<Long> TIME_THREADLOCAL = new NamedThreadLocal<Long>("Cost Time");


    @Before(value = "@annotation(method)")
    public void doBefore(JoinPoint joinPoint, Log method)
    {
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }

    // TODO: 9.26 记得之后加进数据库
    // TODO: 10.21 算了就这样吧
    @AfterReturning(pointcut = "@annotation(method)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log method, Object jsonResult)
    {
        doAfter(joinPoint, method, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e 异常
     */
    @AfterThrowing(value = "@annotation(method)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log method, Exception e)
    {
        doAfter(joinPoint, method,  e);
    }

    private void doAfter(JoinPoint joinPoint, Log method, Object jsonResult)
    {
        User currentUser = UserUtils.getLoginUser();
        // 10.22 不用反射了，改直接用title提高效率
        //String methodName = joinPoint.getSignature().getName();
        if (currentUser != null) {
            // 使用当前用户信息
            log.info("Current user: {}\n Current method: {}\nCurrent result: {}\nTime resume: {}\nUser IP: {}\nUser Location: {}",
                    currentUser.getUserName(),
                    method.title(),
                    jsonResult,
                    System.currentTimeMillis() - TIME_THREADLOCAL.get(),
                    currentUser.getLoginIp(),
                    currentUser.getLoginLocation()
            );
        } else {
            // 没有用户登录时也记录日志
            log.info("Current user: anonymous\n Current method: {}\nCurrent result: {}\nTime resume: {}",
                    method.title(),
                    jsonResult,
                    System.currentTimeMillis() - TIME_THREADLOCAL.get()
            );
        }

    }

    private void doAfter(JoinPoint joinPoint, Log method, Exception e)
    {
        User currentUser = UserUtils.getLoginUser();
        if (currentUser != null) {
            // 使用当前用户信息
            log.info("Current user: {}\n Current method: {}\nCurrent exception: {}\nTime resume: {}\nUser IP: {}\nUser Location: {}",
                    currentUser.getUserName(),
                    method.title(),
                    e.getMessage(),
                    System.currentTimeMillis() - TIME_THREADLOCAL.get(),
                    currentUser.getLoginIp(),
                    currentUser.getLoginLocation()
            );
        } else {
            // 没有用户登录时也记录异常日志
            log.info("Current user: anonymous\n Current method: {}\nCurrent exception: {}\nTime resume: {}",
                    method.title(),
                    e.getMessage(),
                    System.currentTimeMillis() - TIME_THREADLOCAL.get()
            );
        }
    }
}
