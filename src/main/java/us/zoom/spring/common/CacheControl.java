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
 */
@Aspect
@Component
public class CacheControl {
    private final static Logger logger = LoggerFactory.getLogger(CacheControl.class);
    private final static String CACHEING_PRIFIX = "cache_create_";
    private final static String VALUE = "VALUE";
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 这个方法书给@Cachable注解的condition条件用的。从而达到精细化控制操作缓存的时机。
     * 需要在添加上面那个注解的时候配置下，这个很操蛋。
     *
     * @param id
     * @param prifix
     * @return
     */
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
        if (args == null || args.length == 0) {
            return proceedingJoinPoint.proceed(args);
        }

        Class[] clsArray = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            clsArray[i] = args[i].getClass();
        }

        Signature signature = proceedingJoinPoint.getSignature();
        Class declaringType = signature.getDeclaringType();
        String name = signature.getName();
        Method method = declaringType.getMethod(name, clsArray);
        CacheEvict annotation = AnnotationUtils.findAnnotation(method, CacheEvict.class);
        String key = "";
        if (annotation != null) {
            String[] value = annotation.value();
            if (value.length > 0) {
                key = CACHEING_PRIFIX + value[0] + "::";
            }
            BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(args[0]);
            Object id = beanWrapper.getPropertyValue("id");
            if (id instanceof Long) {
                key = key + id;
            }
        }
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        stringStringValueOperations.set(key, VALUE, 100, TimeUnit.SECONDS);
        Object proceed = proceedingJoinPoint.proceed(args);
        stringRedisTemplate.delete(key);
        return proceed;
    }
}
