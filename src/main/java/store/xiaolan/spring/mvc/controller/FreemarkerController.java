package store.xiaolan.spring.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class FreemarkerController {

    @RequestMapping(value = {"/welcome","/welcome.*"})
    public String welcome(@RequestParam(name = "user")String user,Model model){
        model.addAttribute("user",user);
        Map<String,String> map= new HashMap();
        map.put("url","index.html");
        map.put("name","首页");
        model.addAttribute("latestProduct",map);
        return "welcome";
    }
}
