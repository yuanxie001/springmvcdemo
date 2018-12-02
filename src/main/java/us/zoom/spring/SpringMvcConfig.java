package us.zoom.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.springframework.web.servlet.view.xml.MappingJackson2XmlView;
import org.springframework.web.util.UrlPathHelper;
import us.zoom.spring.mvc.controller.interceptor.TestInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {
    @Autowired
    private TestInterceptor testInterceptor;
    @Autowired
    private ContentNegotiationManager contentNegotiationManager;
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
        map.put("json",MediaType.APPLICATION_JSON);
        map.put("xml",MediaType.APPLICATION_XML);
        configurer.mediaTypes(map);
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        // ContentNegotiatingViewResolver不能够添加，只能用下面这个启用。但启用之后怎么按照我们想要的配置获得结果
        // spring boot会先检查容器中有没有ContentNegotiatingViewResolver这个类的bean，没有就新建一个，有就直接启用。
        // 就是下面这个bean注解，提供一个ContentNegotiatingViewResolver来处理。
        registry.enableContentNegotiation(true);
    }

    @Bean
    public ContentNegotiatingViewResolver contentNegotiatingViewResolver(){
        ContentNegotiatingViewResolver contentNegotiatingViewResolver = new ContentNegotiatingViewResolver();
        contentNegotiatingViewResolver.setContentNegotiationManager(contentNegotiationManager);
        contentNegotiatingViewResolver.setOrder(0);
        MappingJackson2JsonView mappingJackson2JsonView = new MappingJackson2JsonView();
        List<View> views = new ArrayList<>();
        views.add(mappingJackson2JsonView);
        MappingJackson2XmlView mappingJackson2XmlView = new MappingJackson2XmlView();
        views.add(mappingJackson2XmlView);
        contentNegotiatingViewResolver.setDefaultViews(views);
        return contentNegotiatingViewResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/static/**");
    }
}
