package store.xiaolan.spring;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "thread-pool")
@Data
@Configuration
public class ThreadPoolProperties {
    private Integer coreSize;

    private Integer maxSize;

    private Integer ideTime;

    private Integer queueCapacity;
}
