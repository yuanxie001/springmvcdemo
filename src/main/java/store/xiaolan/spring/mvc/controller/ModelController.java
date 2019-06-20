package store.xiaolan.spring.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 由于model和map返回都是同一个对象，都是ModelAndViewContainer里面的默认model。
 * 所以，不管用model还是map，又或者modelandView，modelMap行为都是一致的。
 */
@Controller
public class ModelController {
    /**
     * model里面填充的主要是flashMap,SessionAttribute注解注释的,ModelAttribute 注释的方法的参数。
     * 注意：查询参数，header参数，cookie参数，url里面的参数，body里面的参数。model不能自动获取。
     * <p>
     * 由于@ModelAttribute注解和@RequestMapping注解用的参数解析器是一套。
     * 所以，在ModelAttribute注解注释的方法，可以获取到任何@RequestrianMapping方法的参数。
     * 可以通过以上方法迂回获得上面无法直接获取的参数。
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "model", method = RequestMethod.POST)
    @ResponseBody
    public Object testModel(Model model) {
        Map<String, Object> stringObjectMap = model.asMap();
        Object name = stringObjectMap.get("name");
        return name;
    }

    @RequestMapping(value = "map", method = RequestMethod.POST)
    @ResponseBody
    public Object testModp(Map model) {

        Object name = model.get("name");
        return name;
    }
}
