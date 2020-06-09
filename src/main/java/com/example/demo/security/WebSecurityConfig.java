package com.example.demo.security;

import com.example.demo.controller.model.ApiError;
import com.example.demo.controller.model.ErrorInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**",
                "/csrf",
                "/",
                // TODO: remove
                "/apis/**"
        );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().httpBasic().disable().authorizeRequests().anyRequest().authenticated().and()
                .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class);
    }
}
