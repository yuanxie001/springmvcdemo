package spring.priority;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Client {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(HelloConfigration.class);
        HelloService helloService = applicationContext.getBean(HelloService.class);
        System.out.println(helloService.sayHello());

        HelloController helloController = applicationContext.getBean(HelloController.class);
        System.out.println(helloController.say());

    }

}
