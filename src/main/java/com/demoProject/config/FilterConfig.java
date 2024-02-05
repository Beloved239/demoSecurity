package com.demoProject.config;

import org.apache.catalina.filters.RequestFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class FilterConfig {
    private static final String[] AUTH_LIST = {
            "/v1/api-docs",
            "/configuration/ui",
            "/swagger-resources",
            "/configuration/security",
            "/swagger-ui.html",
            "/swagger-ui",
            "/swagger-ui/*",
            "/swagger",
            "/swagger/*",

    };
//    @Bean
//    public FilterRegistrationBean<RequestFilter> requestFilter() {
//        FilterRegistrationBean<RequestFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(new RequestFilter());
//        registrationBean.addUrlPatterns("/v1/*"); // Specify the URL patterns for which the filter should be applied
//        registrationBean.setOrder(1); // Set the filter order if needed
//        return registrationBean;
//    }
//    @Bean
//    public FilterRegistrationBean<RequestLoggerFilter> optionsRequestFilter() {
//        FilterRegistrationBean<RequestLoggerFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(new RequestLoggerFilter());
//        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        registrationBean.addUrlPatterns("/*");
//        return registrationBean;
//    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("POST", "GET", "DELETE","UPDATE"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }






}
