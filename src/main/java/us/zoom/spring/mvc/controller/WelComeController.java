package us.zoom.spring.mvc.controller;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 默认测试，主要用来测试获取初始化默认配置的地方在哪里
 */
@Controller
public class WelComeController implements EnvironmentAware {
    private Environment environment;
    //移除匹配所有的方法，因为这个方法在的时候，静态资源永远都无法处理到。
    // 导致这个原因主要在于request url解析器先解析的是requestMappingHandler这个。这个匹配到了，则无法匹配静态资源匹配器
    // 顺序有关
//    @RequestMapping(value="/**")
//    @ResponseBody
//    public Object index(HttpServletRequest request){
//        return "like you!";
//    }

    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    @ResponseBody
    public Object index2(HttpServletRequest request) {
        String name = environment.getProperty("name");
        return "welcome you!";
    }

    @RequestMapping(value = "/id/{id:^1[34578]\\d{9}$}", method = RequestMethod.GET)
    @ResponseBody
    public Object path(@PathVariable("id") String id) {
        environment.getProperty("name");
        return "welcome :" + id;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
