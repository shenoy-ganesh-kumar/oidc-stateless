package com.okta.developer.jugtours.authentication;

import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;

public class StatelessExceptionMappingAuthenticationFailureHandler extends ExceptionMappingAuthenticationFailureHandler {
    public StatelessExceptionMappingAuthenticationFailureHandler() {
        super();
        setAllowSessionCreation(false);
    }

    @Override
    protected boolean isAllowSessionCreation() {
        return false;
    }

    @Override
    public void setAllowSessionCreation(boolean allowSessionCreation) {
        super.setAllowSessionCreation(false);
    }
}
