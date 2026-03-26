package com.cloud_idaas;

import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.option.FetchOAuthAuthenticationOptions;

public class FetchOAuthAuthenticationTokenSample {

    public static void main(String[] args) {
        // Initialize (automatically load configuration file)
        IDaaSCredentialProviderFactory.init();

        // Create PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // Get OAuth authentication token
        // Without optional parameters
        String token = pamClient.fetchOAuthAuthenticationToken("your-credential-identifier");
        // With optional parameters
        // FetchOAuthAuthenticationOptions options = FetchOAuthAuthenticationOptions.builder()
        //         .scope("your-scope")
        //         .build();
        // String token = pamClient.fetchOAuthAuthenticationToken("your-credential-identifier", options);
        System.out.println("OAuth Token: " + token);
    }
}
