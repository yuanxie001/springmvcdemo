package spring.priority;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello() {
        return "I'm no priority";
    }
}
