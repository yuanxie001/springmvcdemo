package store.xiaolan.spring.common;

import jakarta.annotation.Resource;
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
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Set;

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
    private final static Set<Class> IGNORE_ID_CLASS = Set.of(String.class, Integer.class,Long.class,int.class,long.class, BigDecimal.class);
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

            if (clsArray.length ==1 && IGNORE_ID_CLASS.contains(clsArray[0])) {
                key = key + args[0];
                return getObject(proceedingJoinPoint,annotation, key, args);
            }

            BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(args[0]);
            Object id = beanWrapper.getPropertyValue("id");
            // 这步是解决插入时id为空删除缓存的情形
            // 这是一种解决方案，另外一种是用CachePut注解。这种更加合适。
            if (id == null){
                Object proceed = proceedingJoinPoint.proceed(args);
                return proceed;
            }
            if (id instanceof Long) {
                key = key + id;
            }
        }
        return getObject(proceedingJoinPoint, annotation, key, args);
    }

    private Object getObject(ProceedingJoinPoint proceedingJoinPoint, CacheEvict annotation, String key, Object[] args) throws Throwable {
        // 之前这个逻辑是为了防止并发读的case.但这样不就是导致直接操作db了嘛,和爽删没什么区别
//        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
//        stringStringValueOperations.set(key, VALUE, 100, TimeUnit.SECONDS);
        if (annotation.beforeInvocation()) {
            stringRedisTemplate.delete(key);
        }
        Object proceed = proceedingJoinPoint.proceed(args);
        if (!annotation.beforeInvocation()) {
            stringRedisTemplate.delete(key);
        }
        return proceed;
    }
}
