package us.zoom.spring.mapper;



import us.zoom.spring.domian.Salaries;

import javax.annotation.Resource;
import java.util.List;
@Resource
public interface SalariesMapper {
    List<Salaries> listSalaries();
}
