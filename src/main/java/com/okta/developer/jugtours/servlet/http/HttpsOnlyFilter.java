package com.okta.developer.jugtours.servlet.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Force HTTPS in environments that support it.
 *
 * Inspects the X-Forwarded-Proto header to decide. This header is set by load balancers to inform
 * the proxied application of the protocol used by the client request.
 */
public class HttpsOnlyFilter implements Filter {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) 
    		throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String forwardedProtocolHeader = request.getHeader("x-forwarded-proto");
        String redirectUrl = null;
        if ("http".equalsIgnoreCase(forwardedProtocolHeader)) {
			try {
				redirectUrl = getReferrerUrl(request);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            logger.debug("Redirecting " + request.getRequestURL().toString() + " to " + redirectUrl + " to force https");
            ((HttpServletResponse) servletResponse).sendRedirect(redirectUrl);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
    }

    private String getReferrerUrl(HttpServletRequest request) throws URISyntaxException {
    	final String referrer = request.getHeader("referer");
    	String redirectUrl = null;
        if (referrer != null) {
        	redirectUrl = referrer;
        } else {
        	String requestUrl = request.getRequestURL().toString();
	    	URI requestUri = new URI(requestUrl);
	        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance()
	                .scheme(request.isSecure() ? "https" : "http")
	                .host(requestUri.getHost())
	                .path(requestUri.getPath())
	                .query(request.getQueryString());
	        if ((request.isSecure() && requestUri.getPort() != 443) || 
	        		(!request.isSecure() && requestUri.getPort() != 80)) {
	            uriComponentsBuilder.port(requestUri.getPort());
	        }
	        redirectUrl = uriComponentsBuilder.build().toUriString();
        }
        return redirectUrl;
    }
}
