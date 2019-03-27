package spring.current.inject;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 构造器注入和depends-on属性之间的关联. 这两者都存在循环注入的问题.
 */
public class SpringConfig {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application.xml");
        Object currectBean = applicationContext.getBean("currectBean");
    }
}
