package store.xiaolan.spring.mvc.validator;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import store.xiaolan.spring.domian.User;

/**
 * 用@InitBinder的方式实现校验并不是一件好是。原因在于我们实现校验的时候，可能每个方法的校验逻辑不一致。
 * 但都需要传递同一个对象。这样，我们校验时，就不能只用一套校验逻辑处理。
 * 最好的方式还是加拦截器，在拦截器里面来进行参数校验。达到校验和逻辑相分离。
 *
 * 对比兼容和拦截器的方法，就是校验的位置。拦截器校验，可能会导致校验逻辑不够清晰。
 * 直接通过这种方式处理，校验逻辑更加清晰明确。各有利弊，看对比区别。
 */
@Component
public class UserValidator implements Validator {
    private static final Logger logger = LoggerFactory.getLogger(UserValidator.class);
    @Override
    public boolean supports(Class<?> clazz) {
        // 表示传递的clazz是不是user类型的子类。是的话则返回true
        return User.class.isAssignableFrom(clazz);
    }

    /**
     * 这里有一种兼容方法可以做到根据不同的请求来处理不同的校验逻辑的行为。可以根据request的uri来进行判断后再处理。逻辑如下:
     * 先通过RequestContextHolder来获取request，然后根据request来获取uri进行区别判断。
     * 这样就能根据uri来进行区别校验了。这样的逻辑是不是足够，还得根据业务需求来。
     * @param target
     * @param errors
     */
    @Override
    public void validate(Object target, Errors errors) {
        HttpServletRequest request = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes){
            logger.info("get request");
            ServletRequestAttributes servletSequestAttributes = (ServletRequestAttributes) requestAttributes;
            request = servletSequestAttributes.getRequest();
        }
        if (request!=null){
            String requestURI = request.getRequestURI();
            // 这里就根据uri进行区别判断了
            if (requestURI.endsWith("user/add")){
                User user = (User) target;
                if (StringUtils.isBlank(user.getName())) {
                    errors.reject("name", "name must not null");
                }
                if (StringUtils.containsAny(user.getName(), " ")) {
                    errors.reject("name", "name can't have blank");
                }
            }
        }


    }
}
