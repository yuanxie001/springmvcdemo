package store.xiaolan.spring.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 通过拦截rest template来讲多个spring  cloud程序进行单机部署.完成全部功能.
 */
@Component("restTemplate")
public class MyRestTemplate extends RestTemplate implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    private MultiValueMap<String,RequestMappingInfo> lookupMapping = new LinkedMultiValueMap<>();

    private Map<RequestMappingInfo, HandlerMethod> handlerMethodMap;

    @Override
    protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) throws RestClientException {
        HandlerMethod handlerMethod = lookupHandlerMethod(url, method);
        Object invoke = null;
        if (handlerMethod != null){
            Method invokeMethod = handlerMethod.getMethod();
            Object bean = handlerMethod.getBean();
            try {
                invokeMethod.setAccessible(true);

                invoke = invokeMethod.invoke(bean);
                return (T)invoke;
            } catch (IllegalAccessException e) {
                logger.error("not access ",e);
            } catch (InvocationTargetException e) {
                logger.error("invoke error:",e);
            }
        }


        return null;
    }

    private HandlerMethod lookupHandlerMethod(URI url, HttpMethod method) {
        HandlerMethod handlerMethod = null;
        List<RequestMappingInfo> requestMappingInfos = lookupMapping.get(url.getPath());
        //查找绝对匹配的url.需要url和请求方法都符合的.但如果存在参数不符合就很有问题.这个还得考虑下.
        if (!CollectionUtils.isEmpty(requestMappingInfos)){
            Optional<RequestMappingInfo> optionalRequestMappingInfo = requestMappingInfos.stream()
                    .filter(requestMappingInfo -> requestMappingInfo.getMethodsCondition().getMethods().contains(method))
                    .findAny();
            if (optionalRequestMappingInfo.isPresent()){
                RequestMappingInfo requestMappingInfo = optionalRequestMappingInfo.get();
                handlerMethod = handlerMethodMap.get(requestMappingInfo);
            }
        }

        // 如果没有找到,则进行通配符的处理逻辑.用ant类型通配处理.
        PathMatcher pathMatcher = requestMappingHandlerMapping.getPathMatcher();
        List<RequestMappingInfo> requestMappingInfoList = lookupMapping.entrySet().stream()
                .filter(entry -> pathMatcher.match(entry.getKey(), url.getPath()))
                .map(entry -> entry.getValue())
                .flatMap(list -> list.stream())
                .collect(Collectors.toList());
        // 当结果只有一种情况时候的处理
        if (!CollectionUtils.isEmpty(requestMappingInfoList) && requestMappingInfoList.size() ==1 ){
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
        for(RequestMappingInfo requestMappingInfo : requestMappingInfos){
            PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
            Set<String> patterns = patternsCondition.getPatterns();
            for (String pattern:patterns){
                lookupMapping.add(pattern,requestMappingInfo);
            }
        }
    }
}
