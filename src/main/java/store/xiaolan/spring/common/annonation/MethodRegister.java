package store.xiaolan.spring.common.annonation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MethodRegister {
    /**
     * if beanfactory match bean of class.
     * the MethodHandle will be register mapping to spring mvc.
     * if the has match, excludeClass will be ignore
     * @return
     */
    Class[] includeClass() default {};

    /**
     * if beanfactory match bean of class.
     * the MethodHandle will be not register mapping to spring mvc
     * @return
     */
    Class[] excludeClass() default {};
}
