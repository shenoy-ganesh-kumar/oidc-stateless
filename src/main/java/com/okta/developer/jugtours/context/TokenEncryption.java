package com.okta.developer.jugtours.context;

public interface TokenEncryption {
    String encryptAndSign(String token);

    String decryptAndVerify(String encryptedToken);
}
