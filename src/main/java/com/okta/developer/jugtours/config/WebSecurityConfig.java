package com.okta.developer.jugtours.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.okta.developer.jugtours.authentication.CookieSavedRequestAwareAuthenticationSuccessHandler;
import com.okta.developer.jugtours.authentication.StatelessSimpleUrlAuthenticationFailureHandler;
import com.okta.developer.jugtours.context.HttpCookieOAuth2AuthorizationRequestRepository;
import com.okta.developer.jugtours.context.JdbcOAuth2AuthorizationRequestRepository;

@EnableWebSecurity
@EnableConfigurationProperties
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
    private SecurityContextRepository securityContextRepository;

    @Autowired
    private CsrfTokenRepository csrfTokenRepository;

    @Autowired
    private RequestCache requestCache;
    
    @Autowired
    private JdbcOAuth2AuthorizationRequestRepository jdbcOAuth2AuthorizationRequestRepository;
    
    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    
    @Override
    public void configure(WebSecurity web) throws Exception {
    	
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .csrf()
            	.disable()
            .requestCache()
                .requestCache(requestCache)
                .and()
            .anonymous()
                .disable()
            .authorizeRequests()
                //.antMatchers("/", "/api/user").permitAll()
            	.antMatchers("/api/groups").permitAll()
                .anyRequest().fullyAuthenticated()
                .and()
        	.oauth2Login()
        		.authorizationEndpoint()
        			.authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository).and()
    			.successHandler(new SimpleUrlAuthenticationSuccessHandler("http://localhost:3000"))
        		.failureHandler(new SimpleUrlAuthenticationFailureHandler("http://localhost:3000?error=401"))
        		.and()
            .logout().disable();
    }
    
    //@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        configuration.setAllowCredentials(true);
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
