package store.xiaolan.spring.mvc.controller.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;



@Component
public class TestInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(TestInterceptor.class);
    @Override
   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            logger.info("方法所在的类,bean class name:{}",handlerMethod.getBeanType().getName());
            logger.info("方法名称,method name:{}",handlerMethod.getMethod().getName());
        }
        logger.info("拦截器调用开始");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.info("拦截器调用完成");
    }
}
