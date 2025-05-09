package store.xiaolan.spring;

import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.util.UrlPathHelper;
import store.xiaolan.spring.bean.SupportExtRequestHandlerMapping;
import store.xiaolan.spring.common.TrackingIdFilter;
import store.xiaolan.spring.component.mongo.id.SnowflakeIdGenerate;
import store.xiaolan.spring.mvc.controller.interceptor.TestInterceptor;

import java.text.SimpleDateFormat;
import java.util.*;

@Configuration
@PropertySource("classpath:config.properties")
public class SpringMvcConfig implements WebMvcConfigurer, WebMvcRegistrations {
    @Autowired
    private TestInterceptor testInterceptor;
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 添加拦截器支持
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(testInterceptor).addPathPatterns("/order/**");
        registry.addInterceptor(new LocaleChangeInterceptor());
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        WebMvcConfigurer.super.configureMessageConverters(converters);
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                .indentOutput(true)
                .dateFormat(new SimpleDateFormat("yyyy-MM-dd"))
                .modulesToInstall(new ParameterNamesModule());
        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
        converters.add(new MappingJackson2XmlHttpMessageConverter(builder.createXmlMapper(true).build()));
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
        urlPathHelper.setUrlDecode(false);
        //特定的注解加上特定的前缀
        //configurer.addPathPrefix("/api", HandlerTypePredicate.forAnnotation(RestController.class));
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
        // 用于开启 /userinfo/123?f=json 的支持,替换format为f
        configurer.parameterName("f");
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

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**");
        registry.addMapping("/person/**");
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource resourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("i18n/test");
        return resourceBundleMessageSource;
    }

    @Bean("snowflakeIdGenerate")
    public SnowflakeIdGenerate getIdGenerate(@Value("${id-generator.machine-code:1}") Long machineCode) {
        return new SnowflakeIdGenerate(machineCode);
    }

    @Bean
    public FilterRegistrationBean<TrackingIdFilter> trackingIdFilter() {
        FilterRegistrationBean<TrackingIdFilter> filterBean = new FilterRegistrationBean<>();
        TrackingIdFilter filter = new TrackingIdFilter();
        filterBean.setFilter(filter);
        filterBean.addUrlPatterns("/*");
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST,DispatcherType.ERROR,DispatcherType.ASYNC);
        filterBean.setDispatcherTypes(dispatcherTypes);
        return filterBean;
    }

    @Bean
    public FilterRegistrationBean<CharacterEncodingFilter> encodingCharacterFilter() {
        FilterRegistrationBean<CharacterEncodingFilter> filterBean = new FilterRegistrationBean<>();
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        /*将文本过滤器类注册到容器中*/
        filter.setEncoding("utf-8");
        filter.setForceEncoding(true);
        filterBean.setFilter(filter);
        filterBean.addUrlPatterns("/*");
        return filterBean;
    }
//    因为这个逻辑不需要了.所以取消注册自定义的request mapping handler mapping.
    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new SupportExtRequestHandlerMapping();
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true);
        registrar.registerFormatters(registry);
    }

    @Bean
    public RetryTemplate retryTemplate(RetryPolicy retryPolicy) {
        RetryTemplate template = new RetryTemplate();
        template.setRetryPolicy(retryPolicy);
        return template;
    }

    @Bean
    public SimpleRetryPolicy retryPolicy() {
        return new SimpleRetryPolicy(
                3, // 最多重试次数
                Map.of(Exception.class, true),true
        );
    }
}
