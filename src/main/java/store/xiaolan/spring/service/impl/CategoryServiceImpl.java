package store.xiaolan.spring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import store.xiaolan.spring.domian.Category;
import store.xiaolan.spring.mapper.CategoryMapper;
import store.xiaolan.spring.service.CategoryService;

import java.util.List;
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> list() {
        List<Category> categories = categoryMapper.selectList(null);
        return categories;
    }
}
