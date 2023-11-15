package store.xiaolan.spring.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;

import javax.sql.DataSource;

public class SecurityFilter implements WebSecurityConfigurer {
    @Autowired
    DataSource dataSource;


    @Override
    public void init(SecurityBuilder builder) throws Exception {

    }

    @Override
    public void configure(SecurityBuilder builder) throws Exception {
        builder.build();
    }
}
