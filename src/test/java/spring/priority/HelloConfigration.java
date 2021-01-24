package spring.priority;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages="spring.priority")
@PropertySource(value = {"classpath:test.properties"})
public class HelloConfigration {
}
