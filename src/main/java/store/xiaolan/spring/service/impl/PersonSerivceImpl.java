package store.xiaolan.spring.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import store.xiaolan.spring.common.CacheControl;
import store.xiaolan.spring.common.annonation.TimeCountEnable;
import store.xiaolan.spring.domian.PersonDO;
import store.xiaolan.spring.mapper.PersonDOMapperExt;
import store.xiaolan.spring.service.PersonSerivce;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class PersonSerivceImpl implements PersonSerivce {
    @Resource
    private PersonDOMapperExt personDOMapperExt;

    @Autowired
    private CacheControl cacheControl;

    public CacheControl cacheControl(){
        return cacheControl;
    }

    @Override
    @Cacheable(value = "person",key = "#id")
    @TimeCountEnable
    public PersonDO getPersonById(Long id) {
        PersonDO personDO = personDOMapperExt.selectByPrimaryKey(id);
        String isDelete = personDO.getIsDelete();
        if (StringUtils.equals(isDelete,"Y")){
            return null;
        }
        return personDO;
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED)
    @CachePut(value = "person",key = "#result.id")
    public PersonDO savePerson(PersonDO personDO) {
        personDO.setIsDelete("N");
        personDO.setCreateTime(new Date());
        personDOMapperExt.insertSelective(personDO);
        return personDO;
    }

    // value作为key的前缀的,beforeInvocation的参数控制删除缓存的行为是在调用之前还是之后
    // key是控制获取缓存的逻辑的,这里的意思是第一个参数的id属性值
    @Override
    @Transactional(propagation= Propagation.REQUIRED)
    @CacheEvict(value = "person",beforeInvocation = false,key = "#personDO.id")
    @TimeCountEnable
    public PersonDO updatePerson(PersonDO personDO){
        int i = personDOMapperExt.updateByPrimaryKeySelective(personDO);
        return personDO;
    }


}
