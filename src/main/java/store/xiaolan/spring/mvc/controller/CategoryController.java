package store.xiaolan.spring.mvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.xiaolan.spring.mvc.vo.CategoryVo;
import store.xiaolan.spring.service.CategoryService;

import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/product/category")
    public List<CategoryVo> list(){
        return categoryService.list();
    }

}
