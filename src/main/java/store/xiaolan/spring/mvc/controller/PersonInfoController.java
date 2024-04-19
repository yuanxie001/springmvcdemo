package store.xiaolan.spring.mvc.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import store.xiaolan.spring.domian.PersonDO;
import store.xiaolan.spring.service.PersonSerivce;
import store.xiaolan.spring.mvc.vo.PersonVo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import static org.springframework.web.servlet.HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE;
@Controller
@RequestMapping("/person")
@Slf4j
public class PersonInfoController {
    @Autowired
    private PersonSerivce personSerivce;
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    @ResponseBody
    public PersonVo getPerson(@PathVariable("id")Long id){
        PersonDO person = personSerivce.getPersonById(id);
        if (person == null) {
            return null;
        }

        PersonVo personVo = new PersonVo();
        BeanUtils.copyProperties(person,personVo);
        return  personVo;
    }
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public PersonVo savePerson(@RequestBody PersonVo personVo){
        PersonDO personDO = new PersonDO();
        BeanUtils.copyProperties(personVo,personDO);
        PersonDO person = personSerivce.savePerson(personDO);
        personVo.setId(person.getId());
        return personVo;
    }

    @RequestMapping(value = "/update",method = RequestMethod.PATCH)
    @ResponseBody
    public PersonVo updatePerson(@RequestBody PersonVo personVo){
        PersonDO personDO = new PersonDO();
        BeanUtils.copyProperties(personVo,personDO);
        PersonDO person = personSerivce.updatePerson(personDO);
        personVo.setId(person.getId());
        return personVo;
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Boolean deletePerson(@PathVariable("id")Long id){
        personSerivce.deletePerson(id);
        return true;
    }

    @GetMapping("/list")
    @ResponseBody
    public List<PersonVo> listPerson(@RequestParam String name){
        List<PersonDO> personDOS = personSerivce.queryByName(name);
        return personDOS.stream().map(personDO -> {
            PersonVo personVo = new PersonVo();
            BeanUtils.copyProperties(personDO, personVo);
            return personVo;
        }).toList();
    }

    @GetMapping("/forward/{id}")
    public void forwardPerson(HttpServletRequest request,
                                HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI().replace("/forward","");
        request.getRequestDispatcher(uri).forward(request,response);
    }

    @GetMapping(value = "/include/**")
    public void includePerson(HttpServletRequest request,
                              HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI().replace("/include","");
        if (StringUtils.equals(request.getParameter("f"),"json") ||
            StringUtils.endsWith(request.getRequestURI(),".json")) {
            response.setContentType("application/json");
        } else if (StringUtils.equals(request.getParameter("f"),"xml") ||
                StringUtils.endsWith(request.getRequestURI(),".xml")) {
            response.setContentType("application/xml");
        } else if (StringUtils.isNoneEmpty(request.getHeader("Accept")) &&
                request.getHeader("Accept").startsWith("*/*")){
            response.setContentType("application/json");
        } else if (StringUtils.isNoneEmpty(request.getHeader("Accept"))){
            response.setContentType(request.getHeader("Accept"));
        } else {
            response.setContentType("application/json");
        }
        log.info("include uri:{}",uri);
        request.getRequestDispatcher(uri).include(request,response);

    }
}
