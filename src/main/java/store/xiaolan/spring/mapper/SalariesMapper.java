package store.xiaolan.spring.mapper;



import store.xiaolan.spring.domian.Salaries;

import javax.annotation.Resource;
import java.util.List;
@Resource
public interface SalariesMapper {
    List<Salaries> listSalaries();
}
