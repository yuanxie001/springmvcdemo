package us.zoom.spring.mvc.controller.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CommonAdvice {
    /**
     * 异常解析捕获，可以捕获本类中抛出的所有指定类型的异常。
     * ResponseStatus注解放在捕获异常的方法上，可以自定义异常在返回时的状态码
     * @param exception
     * @return
     */
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handlerException(NullPointerException exception, HandlerMethod method){
        String name = method.getBeanType().getName();
        Map map=new HashMap();
        map.put("error",exception.getMessage());
        map.put("method_bean",name);
        map.put("method_name",method.getMethod().getName());
        return map;
    }
}
