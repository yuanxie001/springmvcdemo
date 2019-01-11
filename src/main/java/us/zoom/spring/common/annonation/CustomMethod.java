package us.zoom.spring.common.annonation;

/**
 * 定制化注解,用于替换处理requestMapping方法的实现.
 */
public @interface CustomMethod {
    /**
     * 判断是否替换的条件.
     * @return
     */
    String condition() default "";

    /**
     * 用于被替换的方法所在的bean类型
     * @return
     */
    Class beanType();

    /**
     * 替换原来requestMapping方法的方法名
     * @return
     */
    String methodName();

    /**
     * 用于替换原来方法的方法参数个数.用来快捷定位到某个处理方法.
     * methodParamters为空时使用.
     * @return
     */
    int methodParamtersLength() default 0;

    /**
     * 用于替换原来方法的参数类型列表.有了这个方法,就能准确的定位到哪个方法.
     * @return
     */
    Class[] methodParamters() default {};
}
