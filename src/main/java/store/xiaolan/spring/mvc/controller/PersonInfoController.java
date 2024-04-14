package store.xiaolan.spring.mvc.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import store.xiaolan.spring.domian.PersonDO;
import store.xiaolan.spring.service.PersonSerivce;
import store.xiaolan.spring.mvc.vo.PersonVo;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonInfoController {
    @Autowired
    private PersonSerivce personSerivce;
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
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

    @GetMapping("/list")
    public List<PersonVo> listPerson(@RequestParam String name){
        List<PersonDO> personDOS = personSerivce.queryByName(name);
        return personDOS.stream().map(personDO -> {
            PersonVo personVo = new PersonVo();
            BeanUtils.copyProperties(personDO, personVo);
            return personVo;
        }).toList();
    }
}
