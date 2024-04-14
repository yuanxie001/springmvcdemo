package store.xiaolan.spring.service;

import store.xiaolan.spring.domian.PersonDO;

import java.util.List;

public interface PersonSerivce {
    PersonDO getPersonById(Long id);

    /**
     * 创建方法
     * @param personDO
     * @return
     */
    PersonDO savePerson(PersonDO personDO);

    PersonDO updatePerson(PersonDO personDO);

    void deletePerson(Long id);

    List<PersonDO> queryByName(String name);
}
