package com.okta.developer.jugtours.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.RequestCache;

import com.okta.developer.jugtours.context.HttpCookieOAuth2AuthorizationRequestRepository;

public class CookieSavedRequestAwareAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
		
	public CookieSavedRequestAwareAuthenticationSuccessHandler(RequestCache requestCache) {
        setRequestCache(requestCache);
    }
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws ServletException, IOException {
		logger.info("Clearing the authorization request cookies");
		HttpCookieOAuth2AuthorizationRequestRepository.deleteCookies(request, response); // TODO: Refactor
		super.onAuthenticationSuccess(request, response, authentication);
	}
}
