package com.cloud_idaas;

import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.domain.JwtContent;

public class ObtainJwtAuthenticationTokenSample {
    public static void main(String[] args) {
        // Initialize (auto-load configuration file)
        IDaaSCredentialProviderFactory.init();

        // Create PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // Obtain JWT authentication token by derived short token
        JwtContent jwtContent = pamClient.obtainJwtAuthenticationToken("your-consumer-id", "your-authentication-token-id");
        System.out.println("JWT: " + jwtContent.getJwtValue());
        System.out.println("Derived Short Token: " + jwtContent.getDerivedShortToken());
    }
}
