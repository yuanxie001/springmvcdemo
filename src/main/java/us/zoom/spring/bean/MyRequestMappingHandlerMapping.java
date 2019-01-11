package us.zoom.spring.bean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import us.zoom.spring.common.annonation.CustomMethod;
import us.zoom.spring.common.annonation.MethodRegister;

import javax.servlet.http.HttpServletRequest;

@Component
public class MyRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    SpelExpressionParser spelExpressionParser;

    @Override
    public int getOrder() {
        return super.getOrder() - 1;
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = getHandlerMethods();
        filterRequestMappings(handlerMethods);
        doWithChangeMethod(handlerMethods);
    }

    private void doWithChangeMethod(Map<RequestMappingInfo, HandlerMethod> handlerMethods) {
        ApplicationContext applicationContext = getApplicationContext();
        Map<RequestMappingInfo, HandlerMethod> changeMethodMap = new HashMap<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            CustomMethod customMethod = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), CustomMethod.class);
            if (customMethod == null) {
                continue;
            }
            Class beanType = customMethod.beanType();
            String methodName = customMethod.methodName();
            Class[] methodParamtersClass = customMethod.methodParamters();
            Object bean = BeanFactoryUtils.beanOfType(applicationContext, beanType, false, true);
            if (bean == null) {
                throw new BeanCreationException("bean can not is null");
            }
            Method method = ClassUtils.getMethod(beanType, methodName, methodParamtersClass);
            if (method != null) {
                registerMapping(entry.getKey(), bean, method);
            }
        }
    }

    protected void filterRequestMappings(Map<RequestMappingInfo, HandlerMethod> handlerMethods) {
        List<RequestMappingInfo> requestMappingInfos = new ArrayList<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            Method method = handlerMethod.getMethod();
            RequestMappingInfo requestMappingInfo = entry.getKey();
            if (!canRegister(method)) {
                requestMappingInfos.add(requestMappingInfo);
            }
            registerHandlerMethod(handlerMethod.getBean(), method, requestMappingInfo);
        }
        for (RequestMappingInfo requestMappingInfo : requestMappingInfos) {
            unregisterMapping(requestMappingInfo);
        }

    }

    private boolean canRegister(Method method) {
        MethodRegister annotation = AnnotationUtils.findAnnotation(method, MethodRegister.class);
        if (annotation == null) {
            return true;
        }
        Class[] includeClass = annotation.includeClass();
        Class[] excludeClass = annotation.excludeClass();
        if (includeClass.length == 0 && excludeClass.length == 0) {
            return true;
        }

        ApplicationContext applicationContext2 = getApplicationContext();
        for (Class type : includeClass) {
            String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(applicationContext2, type);
            if (beanNames != null && beanNames.length > 0) {
                return true;
            }

        }

        for (Class type : excludeClass) {
            String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(applicationContext2, type);
            if (beanNames != null && beanNames.length > 0) {
                logger.info("method be filter:method name:" + method.getName() + " match bean class:" + type.getName());
                return false;
            }

        }

        return true;
    }

    @Override
    protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) throws Exception {
        HandlerMethod handlerMethod = super.lookupHandlerMethod(lookupPath, request);


        return handlerMethod;
    }
}
