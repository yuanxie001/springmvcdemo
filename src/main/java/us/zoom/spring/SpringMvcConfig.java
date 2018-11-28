package us.zoom.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.util.UrlPathHelper;
import us.zoom.spring.mvc.controller.interceptor.TestInterceptor;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {
    @Autowired
    private TestInterceptor testInterceptor;

    /**
     * 添加拦截器支持
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(testInterceptor).addPathPatterns("/g*").excludePathPatterns("/gt*");
        registry.addInterceptor(new LocaleChangeInterceptor());
    }

    /**
     * 解决enable-matrix-variables=true,开启支持的问题。
     * @param configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setRemoveSemicolonContent(false);
        configurer.setUrlPathHelper(urlPathHelper);
    }
}
