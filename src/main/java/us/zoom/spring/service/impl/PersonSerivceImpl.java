package us.zoom.spring.service.impl;

import org.springframework.stereotype.Service;
import us.zoom.spring.domian.PersonDO;
import us.zoom.spring.mapper.PersonDOMapperExt;
import us.zoom.spring.service.PersonSerivce;

import javax.annotation.Resource;

@Service
public class PersonSerivceImpl implements PersonSerivce {
    @Resource
    private PersonDOMapperExt personDOMapperExt;
    @Override
    public PersonDO getPersonById(Long id) {
        PersonDO personDO = personDOMapperExt.selectByPrimaryKey(id);
        return personDO;
    }
}
