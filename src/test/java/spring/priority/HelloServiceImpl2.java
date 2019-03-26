package spring.priority;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.Priority;

@Priority(1)
@Service
//@Order(2)
public class HelloServiceImpl2 implements HelloService {
    @Override
    public String sayHello() {
        return "I'm priority 2";
    }
}
