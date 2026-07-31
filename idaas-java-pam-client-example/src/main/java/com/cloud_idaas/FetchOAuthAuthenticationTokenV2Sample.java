package com.cloud_idaas;

import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.domain.OAuthAuthenticationTokenResponse;
import com.cloud_idaas.pam.domain.OAuthAuthorizationFlow;
import com.cloud_idaas.pam.option.FetchOAuthAuthenticationOptions;

/**
 * Sample for fetching an OAuth authentication token via the 2LO (m2m) flow
 * using {@code fetchOAuthAuthenticationTokenV2}.
 *
 * <p>For the 3LO (user_federation) flow, see {@link OAuth3loAtomicSample} and
 * {@link OAuth3loEndToEndSample}.
 */
public class FetchOAuthAuthenticationTokenV2Sample {

    public static void main(String[] args) {
        // Initialize (automatically load configuration file)
        IDaaSCredentialProviderFactory.init();

        // Create PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // Fetch OAuth authentication token (2LO / m2m flow)
        // Without optional parameters
        OAuthAuthenticationTokenResponse response = pamClient.fetchOAuthAuthenticationTokenV2(
                "your-credential-identifier", OAuthAuthorizationFlow.M2M);
        // With optional parameters
        // FetchOAuthAuthenticationOptions options = FetchOAuthAuthenticationOptions.builder()
        //         .scope("your-scope")
        //         .build();
        // OAuthAuthenticationTokenResponse response = pamClient.fetchOAuthAuthenticationTokenV2(
        //         "your-credential-identifier", OAuthAuthorizationFlow.M2M, options);

        System.out.println("Access Token: " + response.getOauthAccessTokenContent().getAccessTokenValue());
    }
}
