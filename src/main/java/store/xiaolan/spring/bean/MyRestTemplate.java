package store.xiaolan.spring.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.PathMatcher;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import store.xiaolan.spring.bean.strategy.ParamMethodArgumentResolver;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 通过拦截rest template来讲多个spring  cloud程序进行单机部署.完成全部功能.
 */

public class MyRestTemplate extends RestTemplate implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    private MultiValueMap<String, RequestMappingInfo> lookupMapping = new LinkedMultiValueMap<>();

    private Map<RequestMappingInfo, HandlerMethod> handlerMethodMap;

    private List<ParamMethodArgumentResolver> resolvers;

    private ApplicationContext applicationContext;
    @Override
    public <T> T execute(String url, HttpMethod method, @Nullable RequestCallback requestCallback,
                         @Nullable ResponseExtractor<T> responseExtractor, Map<String, ?> uriVariables)
            throws RestClientException {
        // 查找方法匹配的方法
        HandlerMethod handlerMethod = lookupHandlerMethod(url, method);
        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
        // 解析方法参数.
        Object[] objects = buildMethodParamter(methodParameters,uriVariables);


        Object invoke = null;


        if (handlerMethod != null) {
            Method invokeMethod = handlerMethod.getMethod();
            Object bean = handlerMethod.getBean();
            try {
                invokeMethod.setAccessible(true);
                invoke = invokeMethod.invoke(bean,objects);
                return (T) invoke;
            } catch (IllegalAccessException e) {
                logger.error("not access ", e);
            } catch (InvocationTargetException e) {
                logger.error("invoke error:", e);
            }
        }
        URI expanded = getUriTemplateHandler().expand(url, uriVariables);
        return doExecute(expanded, method, requestCallback, responseExtractor);
    }

    private Object[] buildMethodParamter(MethodParameter[] methodParameters,Map<String, ?> uriVariables) {
        Object[] parameters = new Object[methodParameters.length];
        for (int i=0;i<methodParameters.length;i++) {
            MethodParameter methodParameter = methodParameters[i];
            Optional<ParamMethodArgumentResolver> any = resolvers.stream().filter(paramMethodArgumentResolver -> paramMethodArgumentResolver.supportParameter(methodParameter)).findAny();
            if (any.isPresent()){
                ParamMethodArgumentResolver paramMethodArgumentResolver = any.get();
                parameters[i] = paramMethodArgumentResolver.convert(methodParameter, uriVariables, null);
            }
        }
        return parameters;
    }

    private HandlerMethod lookupHandlerMethod(String url, HttpMethod method) {
        HandlerMethod handlerMethod = null;
        List<RequestMappingInfo> requestMappingInfos = lookupMapping.get(url);
        //查找绝对匹配的url.需要url和请求方法都符合的.但如果存在参数不符合就很有问题.这个还得考虑下.
        if (!CollectionUtils.isEmpty(requestMappingInfos)) {
            Optional<RequestMappingInfo> optionalRequestMappingInfo = requestMappingInfos.stream()
                    .filter(requestMappingInfo -> requestMappingInfo.getMethodsCondition().getMethods().contains(method))
                    .findAny();
            if (optionalRequestMappingInfo.isPresent()) {
                RequestMappingInfo requestMappingInfo = optionalRequestMappingInfo.get();
                handlerMethod = handlerMethodMap.get(requestMappingInfo);
            }
        }

        // 如果没有找到,则进行通配符的处理逻辑.用ant类型通配处理.
        PathMatcher pathMatcher = requestMappingHandlerMapping.getPathMatcher();
        List<RequestMappingInfo> requestMappingInfoList = lookupMapping.entrySet().stream()
                .filter(entry -> pathMatcher.match(entry.getKey(), url))
                .map(entry -> entry.getValue())
                .flatMap(list -> list.stream())
                .collect(Collectors.toList());
        // 当结果只有一种情况时候的处理
        if (!CollectionUtils.isEmpty(requestMappingInfoList) && requestMappingInfoList.size() == 1) {
            RequestMappingInfo requestMappingInfo = requestMappingInfoList.get(0);
            return handlerMethodMap.get(requestMappingInfo);
        }

        // 当通配符匹配有多个路径处理
        // TODO 添加通配符路径排序规则. 获取最适合的处理方法


        return handlerMethod;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();
        Set<RequestMappingInfo> requestMappingInfos = handlerMethodMap.keySet();
        for (RequestMappingInfo requestMappingInfo : requestMappingInfos) {
            PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
            Set<String> patterns = patternsCondition.getPatterns();
            for (String pattern : patterns) {
                lookupMapping.add(pattern, requestMappingInfo);
            }
        }

        applicationContext = requestMappingHandlerMapping.getApplicationContext();

        Map<String, ParamMethodArgumentResolver> includingAncestors = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ParamMethodArgumentResolver.class, false, false);
        if (CollectionUtils.isEmpty(includingAncestors)){
            resolvers = Collections.unmodifiableList(new ArrayList<>(includingAncestors.values()));
        }

    }
}
