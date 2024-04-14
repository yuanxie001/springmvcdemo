package store.xiaolan.spring.bean;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.filter.impl.Op;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.mvc.condition.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class SupportExtRequestHandlerMapping extends RequestMappingHandlerMapping {
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = getHandlerMethods();
        filterRequestMappings(handlerMethods);
    }

    private void filterRequestMappings(Map<RequestMappingInfo, HandlerMethod> handlerMethods) {
        List<RequestMappingInfo> removeRequestMappingInfos = new ArrayList<>();
        // 不采取直接取消注册的理由是存在并发修改异常.所以采用先记录,后删除的逻辑来处理
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            Method method = handlerMethod.getMethod();
            RequestMappingInfo requestMappingInfo = entry.getKey();
            Set<String> uriSet
                    = Optional.ofNullable(requestMappingInfo.getPatternsCondition())
                    .map(PatternsRequestCondition::getPatterns)
                    .orElse(Set.of());
            if (uriSet.contains("/error")) {
                continue;
            }
            Set<String> newUriSet = uriSet.stream().filter(uri -> !StringUtils.endsWithAny(uri, ".json", ".xml"))
                    .flatMap(uri -> Stream.of(uri + ".json", uri + ".xml"))
                    .collect(Collectors.toSet());
            if (CollectionUtils.isEmpty(newUriSet)) {
                continue;
            }
            RequestMappingInfo.Builder paths = RequestMappingInfo.paths(newUriSet.toArray(new String[0]));

            Optional.ofNullable(method.getAnnotation(RequestMapping.class))
                    .map(RequestMapping::params)
                    .ifPresent(paths::params);
            Optional.ofNullable(method.getAnnotation(RequestMapping.class))
                    .map(RequestMapping::method)
                    .ifPresent(paths::methods);
            Optional.ofNullable(method.getAnnotation(RequestMapping.class))
                    .map(RequestMapping::consumes)
                    .ifPresent(paths::consumes);
            Optional.ofNullable(method.getAnnotation(RequestMapping.class))
                    .map(RequestMapping::produces)
                    .ifPresent(paths::produces);
            Optional.ofNullable(method.getAnnotation(RequestMapping.class))
                    .map(RequestMapping::headers)
                    .ifPresent(paths::headers);
            log.info("uri:{},new uri:{}", uriSet, newUriSet);
            RequestMappingInfo build = paths.build();
            registerMapping(build, handlerMethod.getBean(), method);
        }
    }
}
