package com.okta.developer.jugtours.context;

import com.nimbusds.jwt.JWTClaimsSet;

public interface JwtClaimsSetVerifier {
    boolean verify(JWTClaimsSet claimsSet);
}
