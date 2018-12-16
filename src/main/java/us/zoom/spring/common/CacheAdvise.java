package us.zoom.spring.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import us.zoom.spring.common.annonation.EnableCache;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class CacheAdvise {
    private static final Logger logger = LoggerFactory.getLogger(CacheAdvise.class);
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static Map<String,Object> cache = new ConcurrentHashMap<>(256);
    @Pointcut("@annotation(us.zoom.spring.common.annonation.EnableCache)")
    public void performance(){}

    @Around("performance()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        logger.info("处理缓存开始");
        Object proceed = null;
        Object[] args = proceedingJoinPoint.getArgs();
        if (args.length==0 || !(args[0] instanceof Long)){
            proceed = proceedingJoinPoint.proceed(args);
            return proceed;
        }
        String kind = proceedingJoinPoint.getKind();
        Signature signature = proceedingJoinPoint.getSignature();
        String name = signature.getName();
        Class declaringType = signature.getDeclaringType();
        Method method = declaringType.getMethod(name, Long.class);
        if (method!=null){
            EnableCache annotation = AnnotationUtils.findAnnotation(method, EnableCache.class);
            if (annotation != null) {
                String value = annotation.value();
                String key = value + args[0];
                Object o = cache.get(key);
                if (o != null) {
                    logger.debug("命中缓存，key:{}",key);
                    return o;
                }
                logger.debug("设置缓存，key:{}",key);
                proceed = proceedingJoinPoint.proceed(args);
                cache.put(key, proceed);
            }
        }
        if (proceed==null){
            proceed=proceedingJoinPoint.proceed(args);
        }
        logger.debug("缓存处理结束");
        return proceed;
    }
}
