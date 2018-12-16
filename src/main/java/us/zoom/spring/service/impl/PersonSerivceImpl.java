package us.zoom.spring.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import us.zoom.spring.common.CacheControl;
import us.zoom.spring.common.annonation.TimeCountEnable;
import us.zoom.spring.domian.PersonDO;
import us.zoom.spring.mapper.PersonDOMapperExt;
import us.zoom.spring.service.PersonSerivce;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class PersonSerivceImpl implements PersonSerivce {
    @Resource
    private PersonDOMapperExt personDOMapperExt;

    @Autowired
    private CacheControl cacheControl;
    @Override
    @Cacheable(value = "person",key = "#id",condition = "#cacheControl.canCache(#id,'person')")
    @TimeCountEnable
    public PersonDO getPersonById(Long id) {
        PersonDO personDO = personDOMapperExt.selectByPrimaryKey(id);
        return personDO;
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED)
    public PersonDO savePerson(PersonDO personDO) {
        personDO.setIsDelete("N");
        personDO.setCreateTime(new Date());
        personDOMapperExt.insertSelective(personDO);
        return personDO;
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED)
    // value作为key的前缀的。beforeInvocation的参数控制删除缓存的行为是在调用之前还是之后。key是控制获取缓存的逻辑的。这里的意思是第一个参数的id属性值。
    @CacheEvict(value = "person",beforeInvocation = false,key = "#personDO.id")
    @TimeCountEnable
    public PersonDO updatePerson(PersonDO personDO) {
        int i = personDOMapperExt.updateByPrimaryKeySelective(personDO);
        return personDO;
    }


}
