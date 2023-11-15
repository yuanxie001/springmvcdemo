package spring.priority;

import jakarta.annotation.Priority;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;



@Service
@Priority(1)
public class HelloServiceImpl1 implements HelloService {
    @Override
    public String sayHello() {
        return "I'm priority 1";
    }
}
