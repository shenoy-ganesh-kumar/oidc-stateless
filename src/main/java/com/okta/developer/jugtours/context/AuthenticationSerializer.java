package com.okta.developer.jugtours.context;

import org.springframework.security.core.Authentication;

public interface AuthenticationSerializer {
    String serialize(Authentication authentication);

    Authentication deserialize(String serialisedAuthentication);
}
