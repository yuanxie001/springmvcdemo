package us.zoom.spring.service;

import us.zoom.spring.domian.PersonDO;

public interface PersonSerivce {
    PersonDO getPersonById(Long id);

    /**
     * 创建方法
     * @param personDO
     * @return
     */
    PersonDO savePerson(PersonDO personDO);

    PersonDO updatePerson(PersonDO personDO);
}
