package spring.priority;

import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.Priority;

@Service
@Priority(1)
public class HelloServiceImpl1 implements HelloService {
    @Override
    public String sayHello() {
        return "I'm priority 1";
    }
}
