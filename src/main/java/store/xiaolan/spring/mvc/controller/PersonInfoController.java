package store.xiaolan.spring.mvc.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import store.xiaolan.spring.domian.PersonDO;
import store.xiaolan.spring.service.PersonSerivce;
import store.xiaolan.spring.mvc.vo.PersonVo;

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

    @RequestMapping(value = "/update",method = RequestMethod.PATCH)
    public PersonVo updatePerson(@RequestBody PersonVo personVo){
        PersonDO personDO = new PersonDO();
        BeanUtils.copyProperties(personVo,personDO);
        PersonDO person = personSerivce.updatePerson(personDO);
        personVo.setId(person.getId());
        return personVo;
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    public Boolean deletePerson(@PathVariable("id")Long id){
        personSerivce.deletePerson(id);
        return true;
    }
}
