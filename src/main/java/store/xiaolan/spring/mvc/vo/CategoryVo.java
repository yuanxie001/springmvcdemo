package store.xiaolan.spring.mvc.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import store.xiaolan.spring.domian.Category;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "category")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryVo {
    private Long categoryId;
    private String categoryName;
    private List<CategoryVo> categoryChild;

    public static CategoryVo build(Category category) {
        CategoryVo categoryVo = new CategoryVo();
        categoryVo.setCategoryId(category.getId());
        categoryVo.setCategoryName(category.getName());
        return categoryVo;
    }
}
