package store.xiaolan.spring;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@MapperScan("store.xiaolan.spring.mapper")
@EnableAspectJAutoProxy
@EnableMongoRepositories(basePackages="store.xiaolan.spring.component.mongo")
@EnableRedisHttpSession
public class SpringBootConfig {
    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(SpringBootConfig.class).
                bannerMode(Banner.Mode.OFF).properties("spring.tet=123424");
        SpringApplication build = builder.build();
        build.run(args);
    }
}
