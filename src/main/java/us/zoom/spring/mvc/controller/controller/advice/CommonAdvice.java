package us.zoom.spring.mvc.controller.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CommonAdvice {
    /**
     * 异常解析捕获，可以捕获本类中抛出的所有指定类型的异常。
     * ResponseStatus注解放在捕获异常的方法上，可以自定义异常在返回时的状态码
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handlerException(NullPointerException exception, HandlerMethod method) {

        String name1 = method.getBeanType().getName();
        Map map = new HashMap();
        map.put("error", exception.getMessage());
        map.put("method_bean", name1);
        map.put("method_name", method.getMethod().getName());
        return map;
    }

    /**
     * ResponseStatus 这个注解很奇怪。如果加了reason字段，则将方法的返回信息全部丢弃了。
     * 走了是ResponseStatusExceptionResolver解析异常的逻辑。如果只想修改状态码，不希望原来的信息丢失。
     * 可以只加ResponseStatus注解和value内容，不能添加reason字段信息。
     * 也可以用response直接添加状态码来处理数值来处理。
     *
     * @param runtimeExecption
     * @return
     */
    @ExceptionHandler(value = RuntimeException.class)
    @ResponseBody
    //@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR,reason = "校验错误")
    public Object handlerRuntimeExcetion(RuntimeException runtimeExecption, HttpServletRequest request, HttpServletResponse response) {
        Map map = new HashMap();
        map.put("error", runtimeExecption.getMessage());
        response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        return map;
    }
}
