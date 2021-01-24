package spring.priority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HelloController {
    @Autowired
    private HelloService helloService;
    @Value("name")
    private String name;

    public String say(){
        return "result:" + helloService.sayHello();
    }
}
