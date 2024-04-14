package store.xiaolan.spring.mvc.controller.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.support.ServletRequestHandledEvent;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import java.security.Principal;
import java.time.Instant;


@Component
@Order
public class TestInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(TestInterceptor.class);

    @Autowired
    private ApplicationContext applicationContext;

    private static final String BEST_MATCHING_PATTERN_ATTRIBUTE = HandlerMapping.class.getName() + ".bestMatchingPattern";
    private static final String START_TIME = "handler_start_time";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            logger.info("方法所在的类,bean class name:{}", handlerMethod.getBeanType().getName());
            logger.info("方法名称,method name:{}", handlerMethod.getMethod().getName());
        }
        long timestamp = Instant.now().toEpochMilli();
        request.setAttribute(START_TIME, timestamp);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.info("拦截器调用完成");

        Object bestUrl = request.getAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE);
        Object attribute = request.getAttribute(START_TIME);
        long cost = 0;
        if (attribute instanceof Long startTime) {
            logger.info("start time: {}",startTime);
            cost = Instant.now().toEpochMilli() - startTime;
        }

        this.applicationContext.publishEvent(
                new ServletRequestHandledEvent(this,
                        String.valueOf(bestUrl), request.getRemoteAddr(),
                        request.getMethod(), "spring mvc",
                        WebUtils.getSessionId(request), getUsernameForRequest(request),
                        cost, null, response.getStatus()));
    }

    String getUsernameForRequest(HttpServletRequest request) {
        Principal userPrincipal = request.getUserPrincipal();
        return (userPrincipal != null ? userPrincipal.getName() : null);
    }
}
