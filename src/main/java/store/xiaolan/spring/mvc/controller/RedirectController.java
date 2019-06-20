package store.xiaolan.spring.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class RedirectController {
    private static final Logger logger = LoggerFactory.getLogger(RedirectController.class);

    @RequestMapping(value = "/redirect.json", method = RequestMethod.GET)
    public String redirect(@RequestParam("att") String att, RedirectAttributes redirectAttributes) {
        logger.info("调用重定向的的handler，att:{}", att);
        redirectAttributes.addFlashAttribute("att", att);
        redirectAttributes.addAttribute("name", "zhangsan");
        return "redirect:get.json";
    }

    @RequestMapping(value = "/get.json", method = RequestMethod.GET)
    @ResponseBody
    public String get(@RequestParam("name") String name, Model model, HttpServletResponse response) {
        Map<String, Object> map = model.asMap();
        Object att = map.get("att");
        logger.info("获取到重定向的参数，att:{},name:{}", att, name);
        return "name:" + name + " att :" + att;
    }
}
