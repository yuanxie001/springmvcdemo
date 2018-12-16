package us.zoom.spring.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/*
*
 * 用aop实现缓存的强一致性处理。
 *
 * 因为我们一般情况下，在处理缓存和db双写的时候，先删缓存和先更新db都会存在脏数据的可能性。首先我们来分析下这两种情况的发生：
 * 1、先删缓存：先删缓存的情况下，
*/
@Aspect
@Component
public class CacheControl {
    private final static Logger logger = LoggerFactory.getLogger(CacheControl.class);
    private final static String CACHEING_PRIFIX = "cache_create_";
    private final static String VALUE = "VALUE";
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public boolean canCache(Long id, String prifix) {
        String key = CACHEING_PRIFIX + prifix + "::" + id;
        while (stringRedisTemplate.hasKey(key)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                logger.warn("exception:{}", e);
            }
        }
        return true;
    }

    @Pointcut("@annotation(org.springframework.cache.annotation.CacheEvict)")
    public void cacheEvict() {
    }

    @Around("cacheEvict()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        if (args==null || args.length==0){
            return proceedingJoinPoint.proceed(args);
        }

        Class[] clsArray = new Class[args.length];
        for (int i=0;i<args.length;i++) {
            clsArray[i]=args[i].getClass();
        }

        Signature signature = proceedingJoinPoint.getSignature();
        Class declaringType = signature.getDeclaringType();
        String name = signature.getName();
        Method method = declaringType.getMethod(name, clsArray);
        CacheEvict annotation = AnnotationUtils.findAnnotation(method, CacheEvict.class);
        String key = "";
        if (annotation != null) {
            String[] value = annotation.value();
            if (value.length>0){
                key= CACHEING_PRIFIX + value[0] + "::";
            }
            BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(args[0]);
            Object id = beanWrapper.getPropertyValue("id");
            if (id instanceof Long){
                key=key+id;
            }
        }
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        stringStringValueOperations.set(key,VALUE,100, TimeUnit.SECONDS);
        Object proceed = proceedingJoinPoint.proceed(args);
        stringRedisTemplate.delete(key);
        return proceed;
    }
}
