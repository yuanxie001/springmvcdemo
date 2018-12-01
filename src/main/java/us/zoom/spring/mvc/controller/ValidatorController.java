package us.zoom.spring.mvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import us.zoom.spring.domian.User;
import us.zoom.spring.mvc.validator.UserValidator;

import java.util.List;

/**
 * InitBinder注解的方法每次调用之前都会之执行一次。执行的时机是数据绑定的时候。
 * 也就是参数解析的时候执行这个方法。执行完成之后执行校验方法完成校验。
 *
 * 对于非标准型的参数解析是在ModelAttributeMethodProcessor中处理的。
 */
@Controller
public class ValidatorController {
    @Autowired
    private UserValidator validator;
    @InitBinder
    private void initBinder(WebDataBinder binder){
        binder.addValidators(validator);
    }

    @RequestMapping(value = "/user/add",method = RequestMethod.POST)
    @ResponseBody
    public Object addUser(@Validated User user, BindingResult result){
        if (result.hasErrors()){
            List<ObjectError> allErrors = result.getAllErrors();
            for(ObjectError error:allErrors){
                String objectName = error.getObjectName();
                return "error name"+objectName+" messge"+error.getDefaultMessage();
            }
        }
        return "yes："+user.getName();
    }
}
