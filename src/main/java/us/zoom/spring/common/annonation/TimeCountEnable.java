package us.zoom.spring.common.annonation;

import java.lang.annotation.*;

/**
 * 处理缓存的注解。可以使缓存生效
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TimeCountEnable {
    String value() default "";
}
