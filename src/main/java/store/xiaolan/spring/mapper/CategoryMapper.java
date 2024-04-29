package store.xiaolan.spring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import store.xiaolan.spring.domian.Category;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
