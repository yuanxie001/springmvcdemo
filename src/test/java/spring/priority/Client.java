package spring.priority;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 用于验证spring的一个接口有多个实现类的情况下,依赖注入的优先次序问题.
 * case1: 如果多个实现类,没有做任何处理.则抛候选bean不是唯一的NoUniqueBeanDefinitionException异常
 * case2: 如果存在一个 @Primary ,则能完成正确注入.
 * case3: 存在多个@Primary实现类,则抛出NoUniqueBeanDefinitionException. message:more than one 'primary' bean found among candidates
 * case4: 如果存在 @Priority注解bean,只要里面的值不同.则注入成功.
 * case5: 如果多个@Priority注解bean,里面的值相同,则抛NoUniqueBeanDefinitionException
 *
 * @arnold
 */
public class Client {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(HelloConfigration.class);
        HelloService helloService = applicationContext.getBean(HelloService.class);
        System.out.println(helloService.sayHello());

        HelloController helloController = applicationContext.getBean(HelloController.class);
        System.out.println(helloController.say());

    }

}
