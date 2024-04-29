package store.xiaolan.spring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import store.xiaolan.spring.domian.Category;
import store.xiaolan.spring.mapper.CategoryMapper;
import store.xiaolan.spring.mvc.vo.CategoryVo;
import store.xiaolan.spring.service.CategoryService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<CategoryVo> list() {
        List<Category> categories = categoryMapper.selectList(null);
        Set<Long> parentSet = categories.stream()
                .filter(category -> category.getParent() == null)
                .map(Category::getId)
                .collect(Collectors.toSet());

        Map<Long, List<CategoryVo>> map = categories.stream()
                .filter(category -> category.getParent() != null)
                .collect(Collectors.groupingBy(Category::getParent,
                        Collectors.mapping(CategoryVo::build, Collectors.toList())));

        List<CategoryVo> categoryVoList = categories.stream()
                .map(CategoryVo::build)
                .peek(vo -> {
                    List<CategoryVo> categoryVos = map.get(vo.getCategoryId());
                    if (CollectionUtils.isNotEmpty(categoryVos)) {
                        vo.setCategoryChild(categoryVos);
                    }
                })
                .filter(vo -> parentSet.contains(vo.getCategoryId()))
                .sorted(Comparator.comparing(CategoryVo::getCategoryId))
                .toList();
        return categoryVoList;
    }
}
