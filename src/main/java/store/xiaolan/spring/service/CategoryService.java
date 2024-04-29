package store.xiaolan.spring.service;

import store.xiaolan.spring.domian.Category;
import store.xiaolan.spring.mvc.vo.CategoryVo;

import java.util.List;

public interface CategoryService {
    List<CategoryVo> list();
}
