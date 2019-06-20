package store.xiaolan.spring.mvc.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ExceptionController {

    @RequestMapping("exception")
    @ResponseBody
    public Object execption(String name){
        if (StringUtils.isBlank(name)){
            throw new NullPointerException("名称不能为空");
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("name",name);
        return map;
    }
}
