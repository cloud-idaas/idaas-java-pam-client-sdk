package com.cloud_idaas.pam.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the 3LO domain models and the authorization flow constants.
 */
public class OAuthDomainTest {

    @Test
    void hasOAuthAccessTokenContent_WhenOnlyTokenPresent_ReturnsTrue() {
        OAuthAuthenticationTokenResponse response = new OAuthAuthenticationTokenResponse();
        response.setOauthAccessTokenContent(new OAuthAccessTokenContent());

        assertTrue(response.hasOAuthAccessTokenContent());
        assertFalse(response.hasOAuthAuthorizationSession());
    }

    @Test
    void hasOAuthAuthorizationSession_WhenOnlySessionPresent_ReturnsTrue() {
        OAuthAuthenticationTokenResponse response = new OAuthAuthenticationTokenResponse();
        response.setOauthAuthorizationSession(new OAuthAuthorizationSession());

        assertTrue(response.hasOAuthAuthorizationSession());
        assertFalse(response.hasOAuthAccessTokenContent());
    }

    @Test
    void convenienceMethods_WhenEmpty_ReturnFalse() {
        OAuthAuthenticationTokenResponse response = new OAuthAuthenticationTokenResponse();

        assertFalse(response.hasOAuthAccessTokenContent());
        assertFalse(response.hasOAuthAuthorizationSession());
    }

    @Test
    void oAuthAuthorizationFlow_ConstantValues() {
        assertEquals("m2m", OAuthAuthorizationFlow.M2M);
        assertEquals("user_federation", OAuthAuthorizationFlow.USER_FEDERATION);
    }
}
