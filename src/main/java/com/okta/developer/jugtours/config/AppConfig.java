package com.okta.developer.jugtours.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.okta.developer.jugtours.context.CookieSecurityContextRepository;
import com.okta.developer.jugtours.context.JwtEncryption;
import com.okta.developer.jugtours.csrf.CookieCsrfTokenRepository;
import com.okta.developer.jugtours.savedrequest.CookieRequestCache;
import com.okta.developer.jugtours.servlet.http.HttpSessionCreatedListener;
import com.okta.developer.jugtours.servlet.http.HttpsOnlyFilter;
import com.okta.developer.jugtours.servlet.http.NoHttpSessionFilter;

import javax.servlet.http.HttpSessionListener;
import java.util.Collections;

@Configuration
class AppConfig {

	@Bean
    public ServletContextInitializer noSessionTrackingServletContextInitializer() {
        return servletContext -> servletContext.setSessionTrackingModes(Collections.emptySet());
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionListener> httpSessionCreatedListener() {
        ServletListenerRegistrationBean<HttpSessionListener> listenerRegistrationBean = 
        		new ServletListenerRegistrationBean<>();
        listenerRegistrationBean.setListener(new HttpSessionCreatedListener());
        return listenerRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<NoHttpSessionFilter> noHttpSessionFilter() {
        FilterRegistrationBean<NoHttpSessionFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new NoHttpSessionFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public CookieCsrfTokenRepository csrfTokenRepository() {
        return new CookieCsrfTokenRepository();
    }

    @Bean
    @Autowired
    public CookieSecurityContextRepository securityContextRepository(
    		@Value("${session.encryption.key.base64}") String sessionEncryptionKeyBase64) {
        return new CookieSecurityContextRepository(new JwtEncryption(sessionEncryptionKeyBase64));
    }

    @Bean
    public CookieRequestCache cookieRequestCache() {
        return new CookieRequestCache();
    }

    @Bean
    public FilterRegistrationBean<HttpsOnlyFilter> httpsOnlyFilter() {
        FilterRegistrationBean<HttpsOnlyFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new HttpsOnlyFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }
}