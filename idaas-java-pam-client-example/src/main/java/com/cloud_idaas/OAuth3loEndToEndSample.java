package com.cloud_idaas;

import com.cloud_idaas.core.credential.IDaaSCredential;
import com.cloud_idaas.core.domain.constants.OAuth2Constants;
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.core.implementation.StaticIDaaSCredentialProvider;
import com.cloud_idaas.core.provider.IDaaSCredentialProvider;
import com.cloud_idaas.core.provider.IDaaSTokenExchangeCredentialProvider;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.domain.OAuthAuthenticationTokenResponse;
import com.cloud_idaas.pam.option.PollOAuthAuthenticationTokenOptions;

/**
 * Sample for the OAuth 3LO (user_federation) flow in end-to-end mode:
 * {@code pollOAuthAuthenticationToken} encapsulates the full flow (initiate
 * authorization -> notify URL via callback -> poll -> fetch token). The method
 * is synchronous and blocks the calling thread for up to 180 seconds.
 *
 * <p>Prerequisites:
 * <ol>
 *   <li>Configure environment variables properly.</li>
 *   <li>Create an OAuth (user_federation) credential provider in the IDaaS console.</li>
 *   <li>In the 3LO scenario, the session APIs require a user-auth access token: the
 *   sample below exchanges a subject token (token exchange) and builds the PAM
 *   client from the exchanged credential.</li>
 * </ol>
 */
public class OAuth3loEndToEndSample {

    public static void main(String[] args) {
        // Initialize (auto-load configuration file)
        IDaaSCredentialProviderFactory.init();

        // OAuth (user_federation / 3LO) credential provider identifier
        String credentialProviderIdentifier = "your-oauth-3lo-credential-provider-identifier";

        // Subject token to be exchanged for a user-auth access token
        String subjectToken = "your-subject-token";

        // Build the PAM client from a user-auth credential (token exchange),
        // as required by the 3LO session APIs.
        IDaaSTokenExchangeCredentialProvider tokenExchangeProvider = IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider();
        IDaaSCredential credential = tokenExchangeProvider.getCredential(subjectToken, OAuth2Constants.ACCESS_TOKEN_TYPE, OAuth2Constants.ACCESS_TOKEN_TYPE);
        IDaaSCredentialProvider credentialProvider = StaticIDaaSCredentialProvider.builder()
                .setCredential(credential)
                .build();
        IDaaSPamClient pamClient = IDaaSPamClient.builder()
                .credentialProvider(credentialProvider)
                .build();

        // End-to-end: the SDK initiates authorization, invokes the callback with the
        // URL, polls the session, and finally returns a response containing the token.
        // Without optional parameters
        OAuthAuthenticationTokenResponse response = pamClient.pollOAuthAuthenticationToken(
                credentialProviderIdentifier,
                // Agent/CLI scenario: print the URL for the user to open in a browser
                authorizationUrl -> System.out.println("Please open this URL in a browser to authorize: " + authorizationUrl));
        // With optional parameters
        // PollOAuthAuthenticationTokenOptions options = PollOAuthAuthenticationTokenOptions.builder()
        //         .maxPollingRetries(60)
        //         .build();
        // OAuthAuthenticationTokenResponse response = pamClient.pollOAuthAuthenticationToken(
        //         credentialProviderIdentifier,
        //         authorizationUrl -> System.out.println("Please open this URL in a browser to authorize: " + authorizationUrl),
        //         options);

        System.out.println("Access Token: " + response.getOauthAccessTokenContent().getAccessTokenValue());
    }
}
