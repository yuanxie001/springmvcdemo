package store.xiaolan.spring.mapper;



import org.springframework.context.annotation.Primary;
import store.xiaolan.spring.domian.Salaries;

import jakarta.annotation.Resource;
import java.util.List;
@Resource
@Primary
public interface SalariesMapperExt extends SalariesMapper{
    @Override
    List<Salaries> listSalaries();
}
