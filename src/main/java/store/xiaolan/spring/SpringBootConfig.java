package store.xiaolan.spring;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@MapperScan("store.xiaolan.spring.mapper")
@EnableAspectJAutoProxy
@EnableMongoRepositories(basePackages="store.xiaolan.spring.component")
public class SpringBootConfig {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootConfig.class);
    }
}
