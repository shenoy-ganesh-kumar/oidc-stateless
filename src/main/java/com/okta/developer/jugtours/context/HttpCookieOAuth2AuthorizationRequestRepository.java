package com.okta.developer.jugtours.context;

import java.util.Base64;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * Cookie based repository for storing Authorization requests
 */
@Component
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
	
	private static final String AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_authorization_request";
	public static final String LEMON_REDIRECT_URI_COOKIE_PARAM_NAME = "redirect_uri";
	
	private int cookieExpirySecs;
	
	public HttpCookieOAuth2AuthorizationRequestRepository() {
		cookieExpirySecs = 30;  //TODO: Replace with properties.
	}

	/**
	 * Load authorization request from cookie
	 */
	@Override
	public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
		Assert.notNull(request, "request cannot be null");
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals(AUTHORIZATION_REQUEST_COOKIE_NAME) || 
						cookies[i].getName().equals(LEMON_REDIRECT_URI_COOKIE_PARAM_NAME)) {
					return this.deserialize(cookies[i]);
				}
			}
		}
		return null;
	}

	/**
	 * Save authorization request in cookie
	 */
	@Override
	public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request,
			HttpServletResponse response) {
		Assert.notNull(request, "request cannot be null");
		Assert.notNull(response, "response cannot be null");
		
		if (authorizationRequest == null) {
			deleteCookies(request, response);
			return;
		}
		
		Cookie cookie = new Cookie(AUTHORIZATION_REQUEST_COOKIE_NAME, serialize(authorizationRequest));
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(cookieExpirySecs);
		response.addCookie(cookie);
		
		String lemonRedirectUri = request.getParameter(LEMON_REDIRECT_URI_COOKIE_PARAM_NAME);
		if (StringUtils.isNotBlank(lemonRedirectUri)) {
			cookie = new Cookie(LEMON_REDIRECT_URI_COOKIE_PARAM_NAME, lemonRedirectUri);
			cookie.setPath("/");
			cookie.setHttpOnly(true);
			cookie.setMaxAge(cookieExpirySecs);
			response.addCookie(cookie);
		}		
	}

	@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
		return loadAuthorizationRequest(request);
	}
	
	/**
	 * Utility for deleting related cookies
	 */
	public static void deleteCookies(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals(AUTHORIZATION_REQUEST_COOKIE_NAME) ||
					cookies[i].getName().equals(LEMON_REDIRECT_URI_COOKIE_PARAM_NAME)) {
					cookies[i].setValue("");
					cookies[i].setPath("/");
					cookies[i].setMaxAge(0);
					response.addCookie(cookies[i]);
				}
			}
		}
	}

	private String serialize(OAuth2AuthorizationRequest authorizationRequest) {
		return Base64.getUrlEncoder().encodeToString(
				SerializationUtils.serialize(authorizationRequest));
	}

	private OAuth2AuthorizationRequest deserialize(Cookie cookie) {
		return SerializationUtils.deserialize(
				Base64.getUrlDecoder().decode(cookie.getValue()));
	}
}