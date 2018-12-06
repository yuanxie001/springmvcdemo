package us.zoom.spring.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FreemarkerController {

    @RequestMapping("/welcome.html")
    public String welcome(@RequestParam(name = "user")String user,Model model){
        model.addAttribute("user",user);
        model.addAttribute("latestProduct.url","index.html");
        model.addAttribute("latestProduct.name","首页");
        return "weblcome";
    }
}
