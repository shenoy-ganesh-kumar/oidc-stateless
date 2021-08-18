package com.okta.developer.jugtours.savedrequest;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

public class CookieRequestCache implements RequestCache {

	public static final String DEFAULT_SAVEDREQUEST_COOKIE_NAME = "savedrequest";

    private final Base64.Encoder base64Encoder = Base64.getMimeEncoder(Integer.MAX_VALUE, new byte[]{'\n'});
    private final Base64.Decoder base64Decoder = Base64.getMimeDecoder();

    private String savedRequestCookieName = DEFAULT_SAVEDREQUEST_COOKIE_NAME;
    private String savedRequestCookiePath = null;
    private int savedRequestCookieMaxAgeSeconds = -1;  // default to session cookie (non-persistent)

    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
            String redirectUrlBase64 = base64Encoder.encodeToString(
            		"http://localhost:3000".getBytes(StandardCharsets.ISO_8859_1)); // TODO: Correct it.

            // FIXME: DRY
            Optional<Cookie> maybeCookie = Optional.empty();
            if(request.getCookies() != null) {
                maybeCookie = Arrays.stream(request.getCookies()).filter(
    				cookie -> cookie != null && savedRequestCookieName.equals(cookie.getName()))
            			.findFirst();
            }
            
            if (!maybeCookie.isPresent()) {
            	Cookie savedRequestCookie = new Cookie(savedRequestCookieName, redirectUrlBase64);
	            savedRequestCookie.setPath("/"); // TODO: Correct or refactor, only for base url?
	            savedRequestCookie.setMaxAge(savedRequestCookieMaxAgeSeconds);
	            savedRequestCookie.setSecure(request.isSecure());
	            savedRequestCookie.setHttpOnly(true);
	            response.addCookie(savedRequestCookie);
            }
    }

    @Override
    public SavedRequest getRequest(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() == null) {
            return null;
        }
        Optional<Cookie> maybeCookie = Arrays.stream(
    		request.getCookies()).filter(
				cookie -> cookie != null && savedRequestCookieName.equals(cookie.getName()))
        			.findFirst();
        if (!maybeCookie.isPresent()) {
            return null;
        }
        Cookie savedRequestCookie = maybeCookie.get();
        String redirectUrl = new String(base64Decoder.decode(
        		savedRequestCookie.getValue()), StandardCharsets.ISO_8859_1);
        return new SimpleSavedRequest(redirectUrl);
    }

    @Override
    public HttpServletRequest getMatchingRequest(HttpServletRequest request, HttpServletResponse response) {
        if (getRequest(request, response) != null) {
            removeRequest(request, response);
        }
        return null;
    }

    @Override
    public void removeRequest(HttpServletRequest request, HttpServletResponse response) {
        Cookie removeSavedRequestCookie = new Cookie(savedRequestCookieName, "");
        removeSavedRequestCookie.setPath(savedRequestCookiePath);
        removeSavedRequestCookie.setMaxAge(0);
        removeSavedRequestCookie.setSecure(request.isSecure());
        removeSavedRequestCookie.setHttpOnly(true);
        response.addCookie(removeSavedRequestCookie);
    }

    public void setSavedRequestCookieName(String savedRequestCookieName) {
        this.savedRequestCookieName = savedRequestCookieName;
    }

    public void setSavedRequestCookiePath(String savedRequestCookiePath) {
        this.savedRequestCookiePath = savedRequestCookiePath;
    }

    public void setSavedRequestCookieMaxAgeSeconds(int savedRequestCookieMaxAgeSeconds) {
        this.savedRequestCookieMaxAgeSeconds = savedRequestCookieMaxAgeSeconds;
    }
}