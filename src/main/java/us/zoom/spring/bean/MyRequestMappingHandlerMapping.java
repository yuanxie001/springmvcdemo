package us.zoom.spring.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import us.zoom.spring.common.annonation.CustomGroup;
import us.zoom.spring.common.annonation.CustomMethod;
import us.zoom.spring.common.annonation.MethodRegister;

import javax.servlet.http.HttpServletRequest;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 自己实现的requestmappinghandlermapping的逻辑。用于替换已有方法和注册以前的方法.
 * 除去order的设置是因为已经实现替换原有的requestmapping的逻辑，不需要在进行处理了
 */
public class MyRequestMappingHandlerMapping extends RequestMappingHandlerMapping{

    private String disableUri;
    private SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public String getDisableUri() {
        return disableUri;
    }

    public void setDisableUri(String disableUri) {
        this.disableUri = disableUri;
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = getHandlerMethods();
        filterRequestMappings(handlerMethods);
        processChangeMethod(handlerMethods);
    }

    /**
     * 处理方法替换的逻辑，requestmapping方法的动态替换。不需要修改代码。
     * @param handlerMethods
     */
    protected void processChangeMethod(Map<RequestMappingInfo, HandlerMethod> handlerMethods) {
        ApplicationContext applicationContext = getApplicationContext();
        Map<RequestMappingInfo, HandlerMethod> changeMethodMap = new HashMap<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            CustomMethod customMethod = getCustomAnnotation(handlerMethod.getMethod());
            // customMethod 为null表示不存在定制方法处理，不需要处理这个逻辑
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
                unregisterMapping(entry.getKey());
                registerMapping(entry.getKey(), bean, method);
            }
        }
    }

    /**
     * 匹配方法上在当前环境下可以使用的定制方法
     * @param method
     * @return 方法上使用的注解，匹配到的结果
     */
    private CustomMethod getCustomAnnotation(Method method) {
        //查看方法上有没有对应的注解。如果有CustomMethod，则先处理CustomMethod的注解
        CustomMethod annotation = AnnotationUtils.findAnnotation(method, CustomMethod.class);
        CustomGroup customGroup = AnnotationUtils.findAnnotation(method, CustomGroup.class);
        // 如果两个注解都不存在方法上，则返回空，表示不存在定制
        if (annotation == null && customGroup == null) {
            return null;
        }
        // 存在这个方法上的CustomMethod，用spel表达式校验这个方法是否匹配当前环境。匹配则返回
        if (annotation != null) {
            Expression expression = spelExpressionParser.parseExpression(annotation.condition());
            Boolean isMatch = expression.getValue(Boolean.class);
            if (isMatch) {
                return annotation;
            }
        }
        // 如果一个方法存在多个定制的时候，走这个逻辑。下面的spel表达式用于匹配这个定制方法在当前环境是否生效问题。
        // 生效则返回那个注解，不生效则继续查看下一个。
        if (customGroup != null) {
            CustomMethod[] customMethods = customGroup.value();
            for (CustomMethod cm : customMethods) {
                Expression expression = spelExpressionParser.parseExpression(cm.condition());
                Boolean isMatch = expression.getValue(Boolean.class);
                if (isMatch) {
                    return cm;
                }
            }
        }
        // 上面注解都不生效的时候，返回null，表示不存在定制方法，不需要重新注册
        return null;
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
        if (!StringUtils.isEmpty(disableUri)) {
            String[] split = disableUri.split(",");
            Set<String> disableUriSet = new HashSet<>(Arrays.asList(split));

            // 这段可以处理对uri的配置信息的处理.读取配置,在这里做uri路径匹配校验处理.
            // 没有读取到就不做处理
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            Object attribute = requestAttributes.getAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
            if (attribute == null) {
                throw new RuntimeException("匹配消息出错,最佳匹配无效");
            }
            String bestPattern = (String) attribute;
            if (disableUriSet.contains(bestPattern)) {
                return null;
            }
        }
        // 处理方法,如果有这个注解,则过滤掉.
        Method method = handlerMethod.getMethod();
        boolean canRegister = canRegister(method);
        return canRegister ? handlerMethod : null;
    }

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {

        RequestMappingInfo mappingForMethod = super.getMappingForMethod(method, handlerType);

        // 检测一个类继承自一个超类,并且超类被@Controller.注解修饰
        Class superClass = handlerType.getSuperclass();

        if (superClass.isAnnotationPresent(Controller.class)) {
            // 我们有一个超类被Controller修饰的

            if (handlerType.isAnnotationPresent(Primary.class)) {
                // 并且有一个子类被@Primary修饰.返回这个子类方法.
                // 这个是handlerType为子类的情况.
                return mappingForMethod;
            }
        } else {
            // 没有超类, 因此我们需要查找其他实现关于这个类. 获取当前applicationContext里所有被Controller修饰的类
            Map<String, Object> controllerBeans = getApplicationContext().getBeansWithAnnotation(Controller.class);
            // 取得这个类的所有子类.
            List<Map.Entry<String, Object>> classesExtendingHandler = controllerBeans.entrySet().stream().filter(e ->
                    AopUtils.getTargetClass(e.getValue()).getSuperclass().getName().equalsIgnoreCase(handlerType
                            .getName()) &&
                            !AopUtils.getTargetClass(e.getValue()).getName().equalsIgnoreCase(handlerType.getName()))
                    .collect(Collectors.toList());


            if (classesExtendingHandler == null || classesExtendingHandler.isEmpty()) {
                // 没有子类的情况.这个handlerType是唯一的.则使用.
                return mappingForMethod;
            } else {
                // 有子类继承这个handler,

                // 查找所有的子类,找出被Primary修饰的那个;
                List<Map.Entry<String, Object>> classesWithPrimary = classesExtendingHandler
                        .stream()
                        .filter(e -> e.getValue().getClass().isAnnotationPresent(Primary.class) &&
                                !AopUtils.getTargetClass(e.getValue().getClass()).getName().equalsIgnoreCase
                                        (handlerType.getName()))
                        .collect(Collectors.toList());
                if (classesWithPrimary == null || classesWithPrimary.isEmpty()) {
                    // 没有子类被@Primary修饰,则返回空.表示没有匹配到.
                    return null;
                } else {
                    // 有一个子类或多个子类被@Primary修饰,
                    if (classesWithPrimary.size() == 1 && AopUtils.getTargetClass(classesWithPrimary.get(0).getValue
                            ()).getClass().getName().equalsIgnoreCase(handlerType.getName())) {
                        // We have only one and it is this one, return it.
                        return mappingForMethod;
                    } else if (classesWithPrimary.size() == 1 && !AopUtils.getTargetClass(classesWithPrimary.get(0)
                            .getValue()).getClass().getName().equalsIgnoreCase(handlerType.getName())) {
                        // 有子类实现的情况下,父类进来会走这里.表示注册过了.不用处理.等待子类处理.
                    } else {
                        // nothing.
                    }
                }
            }
        }

        // If it does, and it is marked with @Primary, then return info.

        // else If it does not extend a super with @Controller and there are no children, then return info;
        // 返回为null表示这个方法被过滤掉.不用注册.
        return null;
    }
}

