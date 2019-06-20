package store.xiaolan.spring.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import store.xiaolan.spring.domian.User;

/**
 * 用来测试非标准类型的参数，spring mvc是如何解析的。
 *
 * 主要关键点在ModelAttributeMethodProcessor这个类中。这个类处理了所有的非标准型的参数绑定。
 * 标准类型如int，long之类的是RequestParamMethodArgumentResolver这个类解析的
 */
@Controller
public class ObjectController {
    @RequestMapping(value = {"/users/insert.*","/users/insert"},method = RequestMethod.POST)
    @ResponseBody
    public User insert(User user){
        return user;
    }
}
