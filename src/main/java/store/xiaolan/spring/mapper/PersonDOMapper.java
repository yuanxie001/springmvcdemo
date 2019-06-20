package store.xiaolan.spring.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import store.xiaolan.spring.domian.PersonDO;
import store.xiaolan.spring.domian.PersonDOExample;

public interface PersonDOMapper {
    long countByExample(PersonDOExample example);

    int deleteByExample(PersonDOExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PersonDO record);

    int insertSelective(PersonDO record);

    List<PersonDO> selectByExample(PersonDOExample example);

    PersonDO selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PersonDO record, @Param("example") PersonDOExample example);

    int updateByExample(@Param("record") PersonDO record, @Param("example") PersonDOExample example);

    int updateByPrimaryKeySelective(PersonDO record);

    int updateByPrimaryKey(PersonDO record);
}