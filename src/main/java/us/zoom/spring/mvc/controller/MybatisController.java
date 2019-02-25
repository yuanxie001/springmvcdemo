package us.zoom.spring.mvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import us.zoom.spring.domian.Salaries;
import us.zoom.spring.mapper.SalariesMapper;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class MybatisController {
    @Autowired
    private SalariesMapper salariesMapper;
    @RequestMapping("/salary/list")
    @ResponseBody
    public List<Salaries> list(){
        return salariesMapper.listSalaries();
    }
}
