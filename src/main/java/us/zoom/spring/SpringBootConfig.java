package us.zoom.spring;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("us.zoom.spring.mapper")
public class SpringBootConfig {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootConfig.class);
    }
}
