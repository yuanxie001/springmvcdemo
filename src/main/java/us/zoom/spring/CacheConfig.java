package us.zoom.spring;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * @EnableCaching 开启缓存注解，用来配置开启缓存模式的。相当于xml里面的<cache:annonation-driven/>
 */
@Configuration
@EnableCaching
public class CacheConfig {

}
