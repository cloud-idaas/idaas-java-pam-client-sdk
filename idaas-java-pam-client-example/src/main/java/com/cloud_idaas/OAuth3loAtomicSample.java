package com.cloud_idaas;

import com.cloud_idaas.core.credential.IDaaSCredential;
import com.cloud_idaas.core.domain.constants.OAuth2Constants;
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.core.implementation.StaticIDaaSCredentialProvider;
import com.cloud_idaas.core.provider.IDaaSCredentialProvider;
import com.cloud_idaas.core.provider.IDaaSTokenExchangeCredentialProvider;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.domain.OAuthAuthenticationTokenResponse;
import com.cloud_idaas.pam.domain.OAuthAuthorizationFlow;
import com.cloud_idaas.pam.domain.OAuthAuthorizationSession;
import com.cloud_idaas.pam.domain.OAuthAuthorizationSessionResponse;
import com.cloud_idaas.pam.domain.PamClientConstants;

/**
 * Sample for the OAuth 3LO (user_federation) flow in atomic mode: the caller
 * orchestrates the flow by calling {@code fetchOAuthAuthenticationTokenV2} and
 * {@code getOAuthAuthorizationSession}, controlling the polling loop itself.
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
public class OAuth3loAtomicSample {

    public static void main(String[] args) throws InterruptedException {
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

        // 1. Initiate authorization (user_federation flow)
        OAuthAuthenticationTokenResponse response = pamClient.fetchOAuthAuthenticationTokenV2(
                credentialProviderIdentifier, OAuthAuthorizationFlow.USER_FEDERATION);

        // 2. Token already available -> use it directly
        if (response.hasOAuthAccessTokenContent()) {
            System.out.println("Access Token: " + response.getOauthAccessTokenContent().getAccessTokenValue());
            return;
        }

        // 3. User authorization required -> show the URL and poll the session
        OAuthAuthorizationSession session = response.getOauthAuthorizationSession();
        System.out.println("Please open this URL in a browser to authorize: " + session.getAuthorizationUrl());

        while (true) {
            OAuthAuthorizationSessionResponse sessionResponse = pamClient.getOAuthAuthorizationSession(session.getSessionUri());
            String status = sessionResponse.getSessionStatus();
            System.out.println("Session status: " + status);
            if (PamClientConstants.SESSION_STATUS_COMPLETED.equals(status)) {
                break;
            }
            if (PamClientConstants.SESSION_STATUS_FAILED.equals(status)
                    || PamClientConstants.SESSION_STATUS_EXPIRED.equals(status)) {
                System.out.println("Authorization not completed: " + status);
                return;
            }
            Thread.sleep(PamClientConstants.POLLING_INTERVAL_MILLIS);
        }

        // 4. Authorization completed -> fetch the token
        OAuthAuthenticationTokenResponse finalResponse = pamClient.fetchOAuthAuthenticationTokenV2(
                credentialProviderIdentifier, OAuthAuthorizationFlow.USER_FEDERATION);
        if (finalResponse.hasOAuthAccessTokenContent()) {
            System.out.println("Access Token: " + finalResponse.getOauthAccessTokenContent().getAccessTokenValue());
        }
    }
}
