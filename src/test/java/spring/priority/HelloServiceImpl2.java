package spring.priority;

import jakarta.annotation.Priority;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;


@Priority(1)
@Service
//@Order(2)
public class HelloServiceImpl2 implements HelloService {
    @Override
    public String sayHello() {
        return "I'm priority 2";
    }
}
