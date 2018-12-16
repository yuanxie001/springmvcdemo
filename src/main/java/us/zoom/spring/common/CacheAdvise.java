package us.zoom.spring.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * spring boot的@Order注解似乎没有效果。原因在于并不能确定事务、缓存和自定义的aop的调用顺序。
 * 这个很麻烦。要是能控制的住这个，就能实现对应的功能。
 * @Author arnold
 */
@Aspect
@Component
@Order(999)//用于控制最后执行，保证前面的都执行了才执行它
public class CacheAdvise {

    private static final Logger logger = LoggerFactory.getLogger(CacheAdvise.class);

    @Pointcut("@annotation(us.zoom.spring.common.annonation.TimeCountEnable)")
    public void cacheable() {
    }

    @Around("cacheable()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long begain = System.currentTimeMillis();
        Object proceed = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        long end = System.currentTimeMillis();
        logger.debug("方法处理时间为{}ms",end-begain);
        return proceed;
    }
}
