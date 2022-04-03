package spring.placeholder;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.MessageFormat;

public class Testapplication {
    public static void main(String[] args) {

//        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("placeholder.xml");
//        TestProroerties testbean = (TestProroerties) applicationContext.getBean("testbean");
//        System.out.println(testbean.displayName());
//        applicationContext.close();
        MessageFormat messageFormat = new MessageFormat("name");
        String s = messageFormat.toPattern();
        System.out.println(s);
    }
}
