package store.xiaolan.spring.common.annonation;

import java.lang.annotation.*;

/**
 * 用于处理多个定制方法的逻辑处理。单个逻辑不能
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomGroup {
    CustomMethod[] value() default {};
}

