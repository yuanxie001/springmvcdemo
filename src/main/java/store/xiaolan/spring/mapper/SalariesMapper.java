package store.xiaolan.spring.mapper;



import store.xiaolan.spring.domian.Salaries;

import jakarta.annotation.Resource;
import java.util.List;
@Resource
public interface SalariesMapper {
    List<Salaries> listSalaries();
}
