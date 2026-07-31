package com.cloud_idaas.pam.domain;

/**
 * Constants for the OAuth authorization flow type used by
 * {@code fetchOAuthAuthenticationTokenV2}.
 *
 * <p>This value is used only for SDK-side validation and is NOT sent to the
 * server. The server determines the actual flow based on the CredentialProvider
 * configuration.
 */
public final class OAuthAuthorizationFlow {

    /**
     * Machine-to-Machine flow, corresponding to the 2LO (client_credentials) flow.
     */
    public static final String M2M = "m2m";

    /**
     * User federation flow, corresponding to the 3LO (authorization_code) flow.
     */
    public static final String USER_FEDERATION = "user_federation";

    private OAuthAuthorizationFlow() {
    }
}
