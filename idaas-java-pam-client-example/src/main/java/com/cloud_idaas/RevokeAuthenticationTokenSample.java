package com.cloud_idaas;

import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.option.ReinstateAuthenticationTokenOptions;

public class RevokeAuthenticationTokenSample {

    public static void main(String[] args) {
        // Initialize (auto-load configuration file)
        IDaaSCredentialProviderFactory.init();

        // Create PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // Revoke the specified authentication token
        // Without optional parameters
        pamClient.revokeAuthenticationToken("your-token");
        // With optional parameters
        //ReinstateAuthenticationTokenOptions options = ReinstateAuthenticationTokenOptions.builder()
        //        .tokenTypeHint("your-token-type-hint")
        //        .build();
        //pamClient.reinstateAuthenticationToken("your-token", options);
    }
}
