package store.xiaolan.spring.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.ServletRequestHandledEvent;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 一个事件监听器,监听request的请求处理. 可以做处理完之后的分析逻辑.比如处理url的监听逻辑,处理请求日志啥的.
 * 但如果要获取请求参数这里获取不到的.这个就局限了.但统计url和ip才次数还是可行的.
 */
@Component
public class TotalUrlListener {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String CACHE_KEY_PREFIX = "count_";

    // 创建一个DateTimeFormatter对象，指定格式
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmm");

    @EventListener(ServletRequestHandledEvent.class)
    public void onFlush(ServletRequestHandledEvent event) {
        String requestUrl = event.getRequestUrl();
        String method = event.getMethod();
        String clientAddress = event.getClientAddress();

        LocalDateTime ldt1 = LocalDateTime.now();
        String format = ldt1.format(formatter);
        String cacheKey = CACHE_KEY_PREFIX + format;
        logger.info("请求方式为:{}.uri为:{},远程地址为:{}", method, requestUrl, clientAddress);
        redisTemplate.opsForHash().increment(cacheKey, requestUrl, 1);
    }
}
