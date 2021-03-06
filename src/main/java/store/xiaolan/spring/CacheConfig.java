package store.xiaolan.spring;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.net.UnknownHostException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @EnableCaching 开启缓存注解，用来配置开启缓存模式的。相当于xml里面的<cache:annonation-driven/>
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${threadpool.core.size}")
    private Integer coreSize;

    @Value("${threadpool.max.size}")
    private Integer maxSize;

    @Value("${threadpool.ide.time}")
    private Integer time;

    @Value("${threadpool.queue.capacity}")
    private Integer taskQueueCapacity;

    @Bean
    public StringRedisTemplate stringRedisTemplate(
            RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        // 使用Jackson2JsonRedisSerialize 替换默认序列化
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//
//        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        template.setValueSerializer(new GenericFastJsonRedisSerializer());

        return template;
    }
    @Bean
    public ThreadPoolTaskExecutor threadPoolExecutor(){
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(coreSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxSize);
        threadPoolTaskExecutor.setKeepAliveSeconds(time);
        threadPoolTaskExecutor.setQueueCapacity(taskQueueCapacity);
        return threadPoolTaskExecutor;
    }
}
