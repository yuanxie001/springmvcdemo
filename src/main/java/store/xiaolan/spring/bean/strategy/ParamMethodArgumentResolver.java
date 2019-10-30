package store.xiaolan.spring.bean.strategy;

import org.springframework.core.MethodParameter;

import java.util.Map;

public interface ParamMethodArgumentResolver {

    boolean supportParameter(MethodParameter methodParameter);

    Object convert(MethodParameter methodParameter, Map<String,?> uriVaribles,Object obj);
}
