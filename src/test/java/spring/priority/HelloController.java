package spring.priority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HelloController {
    @Autowired
    private HelloService helloService;

    public String say(){
        return "result:" + helloService.sayHello();
    }
}
