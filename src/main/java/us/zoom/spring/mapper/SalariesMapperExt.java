package us.zoom.spring.mapper;



import org.springframework.context.annotation.Primary;
import us.zoom.spring.domian.Salaries;

import javax.annotation.Resource;
import java.util.List;
@Resource
@Primary
public interface SalariesMapperExt extends SalariesMapper{
    @Override
    List<Salaries> listSalaries();
}
