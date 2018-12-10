package us.zoom.spring.mvc.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import us.zoom.spring.domian.PersonDO;
import us.zoom.spring.mvc.vo.PersonVo;
import us.zoom.spring.service.PersonSerivce;

@RestController
@RequestMapping("/person")
public class PersonInfoController {
    @Autowired
    private PersonSerivce personSerivce;
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public PersonVo getPerson(@PathVariable("id")Long id){
        PersonDO person = personSerivce.getPersonById(id);
        PersonVo personVo = new PersonVo();
        BeanUtils.copyProperties(person,personVo);
        return  personVo;
    }
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public PersonVo savePerson(@RequestBody PersonVo personVo){
        PersonDO personDO = new PersonDO();
        BeanUtils.copyProperties(personVo,personDO);
        PersonDO person = personSerivce.savePerson(personDO);
        personVo.setId(person.getId());
        return personVo;
    }
}
