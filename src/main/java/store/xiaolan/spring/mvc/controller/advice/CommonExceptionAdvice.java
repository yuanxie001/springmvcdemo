package store.xiaolan.spring.mvc.controller.advice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CommonExceptionAdvice {

    public static final String errorMsg = "errorMethod:{0}#{1},errorMSg:{2}";
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
        String beanName = method.getBeanType().getName();
        String msg = MessageFormat.format(errorMsg, beanName, method.getMethod().getName(), exception.getMessage());
        return ResultData.fail(500, msg);
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
    public Object handlerRuntimeExcetion(RuntimeException runtimeExecption, HandlerMethod method,
                                         HttpServletRequest request, HttpServletResponse response) {
        String beanName = method.getBeanType().getName();
        String msg = MessageFormat.format(errorMsg, beanName, method.getMethod().getName(), runtimeExecption.getMessage());
        response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        return ResultData.fail(500, msg);
    }
}
