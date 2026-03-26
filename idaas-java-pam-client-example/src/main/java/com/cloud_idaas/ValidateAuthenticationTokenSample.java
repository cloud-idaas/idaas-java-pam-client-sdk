package com.cloud_idaas;

import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.option.ValidateAuthenticationTokenOptions;

public class ValidateAuthenticationTokenSample {

    public static void main(String[] args) {
        // Initialize (auto-load configuration file)
        IDaaSCredentialProviderFactory.init();

        // Create PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // Validate authentication token
        // Without optional parameters
        Boolean isValid = pamClient.validateAuthenticationToken("your-token");
        // With optional parameters
        //ValidateAuthenticationTokenOptions options = ValidateAuthenticationTokenOptions.builder()
        //        .tokenTypeHint("your-token-type-hint")
        //        .build();
        //Boolean isValid = pamClient.validateAuthenticationToken("your-token", options);

        System.out.println("Token is valid: " + isValid);
    }
}
