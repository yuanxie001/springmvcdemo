package us.zoom.spring.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.invoke.MethodHandles;
import java.util.*;

/**
 * bean factroy post processor. if existed sub class and super class in applicationContext,
 * this class can remove super class bean from application. it can instead of @Primary
 */
@Component
public class PsoBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        String[] beanNames = registry.getBeanDefinitionNames();
        Map<Class, String> classBeanNameHashMap = new HashMap<>(beanNames.length);
        ClassLoader classLoader = getClass().getClassLoader();
        // step.1 general bean name and bean class map.
        for (String beanName:beanNames) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName == null){
                continue;
            }

            if (!beanClassName.startsWith("us.zoom")){
                continue;
            }
            Class beanClass = null;
            if (beanDefinition instanceof AbstractBeanDefinition){
                AbstractBeanDefinition abstractBeanDefinition = (AbstractBeanDefinition) beanDefinition;
                if (abstractBeanDefinition.hasBeanClass()){
                    beanClass = abstractBeanDefinition.getBeanClass();
                } else {
                    try {
                        beanClass = abstractBeanDefinition.resolveBeanClass(classLoader);
                    } catch (ClassNotFoundException e) {
                        logger.error("bean class not found. class name:{}",beanClassName);
                    }
                }
            } else {
                try {
                    beanClass = ClassUtils.forName(beanClassName,classLoader);
                } catch (ClassNotFoundException e) {
                    logger.error("bean class not found. class name:{}",beanClassName);
                }
            }
            if (beanClass != null){
                classBeanNameHashMap.put(beanClass,beanName);
            }
        }
        // step.2 find all bean super class also as bean in this applicationContext.find the super class bean name
        List<String> removeBeanDefinitionList = new ArrayList<>();
        Set<Map.Entry<Class, String>> entrySet = classBeanNameHashMap.entrySet();
        for (Map.Entry<Class,String> entry:entrySet) {
            Class beanClass = entry.getKey();
            Class superclass = beanClass.getSuperclass();
            String beanName = classBeanNameHashMap.get(superclass);
            if (beanName != null){
                removeBeanDefinitionList.add(beanName);
            }
        }
        // step.3 remove super class bean name.
        logger.info("remove bean,size:{},bean names:{}",removeBeanDefinitionList.size(),removeBeanDefinitionList);
        removeBeanDefinitionList.forEach(beanName -> {
            if (registry.containsBeanDefinition(beanName)){
                registry.removeBeanDefinition(beanName);
            }
        });
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
