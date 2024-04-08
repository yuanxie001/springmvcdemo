package store.xiaolan.spring;


import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.util.UrlPathHelper;
import store.xiaolan.spring.mvc.controller.interceptor.TestInterceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Configuration
@PropertySource("classpath:config.properties")
public class SpringMvcConfig implements WebMvcConfigurer,WebMvcRegistrations {
    @Autowired
    private TestInterceptor testInterceptor;
    @Autowired
    private ApplicationContext applicationContext;
    @Value("${disable.uri}")
    private String disableUir;
    /**
     * 添加拦截器支持
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(testInterceptor).addPathPatterns("/g*").excludePathPatterns("/gt*");
        registry.addInterceptor(new LocaleChangeInterceptor());
    }

    /**
     * 解决enable-matrix-variables=true,开启支持的问题。
     *
     * @param configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setRemoveSemicolonContent(false);
        configurer.setUrlPathHelper(urlPathHelper);
    }

    /**
     * 以下三个配置是完成json和xml输出的自动配置选项的。根据后缀名显示输出的
     *
     * @param configurer
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        // 扩展名至mimeType的映射,即 /user.json => application/json
        configurer.favorPathExtension(true);
        // 用于开启 /userinfo/123?format=json 的支持
        configurer.favorParameter(true);
        // 是否忽略Accept Header
        configurer.ignoreAcceptHeader(false);
        // 扩展名到MIME的映射；favorPathExtension, favorParameter是true时起作用
        Map<String, MediaType> map = new HashMap<>();
        map.put("json", MediaType.APPLICATION_JSON);
        map.put("xml", MediaType.APPLICATION_XML);
        configurer.mediaTypes(map);
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        // ContentNegotiatingViewResolver不能够添加，只能用下面这个启用。但启用之后怎么按照我们想要的配置获得结果
        // spring boot会先检查容器中有没有ContentNegotiatingViewResolver这个类的bean，没有就新建一个，有就直接启用。
        // 就是下面这个bean注解，提供一个ContentNegotiatingViewResolver来处理。
        registry.enableContentNegotiation(true);
        Map<String, ViewResolver> stringViewResolverMap = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ViewResolver.class);
        Optional<ViewResolver> any = stringViewResolverMap.values().stream().filter(viewResolver -> viewResolver.getClass().equals(FreeMarkerViewResolver.class)).findAny();
        any.ifPresent(registry::viewResolver);

    }
    @Bean
    public MessageSource messageSource(){
        ReloadableResourceBundleMessageSource resourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("i18n/test");
        return resourceBundleMessageSource;
    }
//    因为这个逻辑不需要了.所以取消注册自定义的request mapping handler mapping.
//    @Override
//    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
//        MyRequestMappingHandlerMapping myRequestMappingHandlerMapping = new MyRequestMappingHandlerMapping();
//        myRequestMappingHandlerMapping.setDisableUri(disableUir);
//        return myRequestMappingHandlerMapping;
//    }
}
