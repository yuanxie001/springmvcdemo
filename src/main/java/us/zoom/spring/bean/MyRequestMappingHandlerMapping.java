package us.zoom.spring.bean;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import us.zoom.spring.common.annonation.CustomMethod;
import us.zoom.spring.common.annonation.MethodRegister;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;

@Component
public class MyRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private Set<String> disableUri;

    @Override
    public int getOrder() {
        // RequestMappingHandlerMapping 的默认值不是最小值,而是0,所以这里写-1,
        // 表示在系统自带的RequestMappingHandlerMapping之前执行.
        // 不直接替换是因为替换报错.不能为两个id相同的bean
        return -1;
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
        List<RequestMappingInfo> removeRequestMappingInfos = new ArrayList<>();
        // 不采取直接取消注册的理由是存在并发修改异常.所以采用先记录,后删除的逻辑来处理
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            Method method = handlerMethod.getMethod();
            RequestMappingInfo requestMappingInfo = entry.getKey();
            // 判断是否注册
            if (!canRegister(method)) {
                // 不能注册添加到移除注册信息里面去.
                removeRequestMappingInfos.add(requestMappingInfo);
            }
            registerHandlerMethod(handlerMethod.getBean(), method, requestMappingInfo);
        }
        for (RequestMappingInfo requestMappingInfo : removeRequestMappingInfos) {
            unregisterMapping(requestMappingInfo);
        }

    }

    /**
     * 处理方法可注册逻辑的.
     * 主要是依据spring 容器中是否存在对应的bean来控制这个mapping是否注册的.
     *
     * @param method
     * @return
     */
    private boolean canRegister(Method method) {
        //判断方法上有没有这个注解,没有就不做处理
        MethodRegister annotation = AnnotationUtils.findAnnotation(method, MethodRegister.class);
        if (annotation == null) {
            return true;
        }
        // 有这个注解的情况下,分别判断注解的属性.
        Class[] includeClass = annotation.includeClass();
        Class[] excludeClass = annotation.excludeClass();
        // 有这个注解,但是两个属性都没有,则不处理.直接返回
        if (includeClass.length == 0 && excludeClass.length == 0) {
            return true;
        }

        ApplicationContext applicationContext2 = getApplicationContext();
        // 先过滤白名单请求.白名单匹配到则不处理
        for (Class type : includeClass) {
            String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(applicationContext2, type);
            if (beanNames != null && beanNames.length > 0) {
                return true;
            }
        }
        // 如果白名单不为空而且走到这里,说明include所有的条件都不匹配.则直接返回false,表示不注册.
        if (includeClass.length > 0) {
            return false;
        }
        // 没有配置白名单,至配置黑名单的情况.匹配黑名单
        for (Class type : excludeClass) {
            String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(applicationContext2, type);
            if (beanNames != null && beanNames.length > 0) {
                logger.info("method be filter:method name:" + method.getName() + " match bean class:" + type.getName());
                return false;
            }
        }
        // 到这里表示黑名单没有匹配上,则表示允许注册.
        return true;
    }

    @Override
    protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) throws Exception {
        // 这个方法是控制注册逻辑的另外一种思路.这边是在方法查找的时候做过滤的.
        // 这个思路更加灵活,但是也有自己的问题.就是每次请求都要遍历所有的mapping,上面只有启动的时候匹配所有的mapping
        HandlerMethod handlerMethod = super.lookupHandlerMethod(lookupPath, request);
        if (CollectionUtils.isEmpty(disableUri)) {
            // 这段可以处理对uri的配置信息的处理.读取配置,在这里做uri路径匹配校验处理.
            // 没有读取到就不做处理
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            Object attribute = requestAttributes.getAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
            if (attribute == null) {
                throw new RuntimeException("匹配消息出错,最佳匹配无效");
            }
            String bestPattern = (String) attribute;
            if (disableUri.contains(bestPattern)) {
                return null;
            }
        }
        // 处理方法,如果有这个注解,则过滤掉.
        Method method = handlerMethod.getMethod();
        boolean canRegister = canRegister(method);
        return canRegister ? handlerMethod : null;
    }
}
