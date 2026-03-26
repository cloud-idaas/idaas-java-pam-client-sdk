package com.cloud_idaas;

import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;

public class RevokeAuthenticationTokenByConsumerSample {

    public static void main(String[] args) {
        // Initialize (automatically load configuration file)
        IDaaSCredentialProviderFactory.init();

        // Create PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // Revoke authentication token by consumer
        pamClient.revokeAuthenticationTokenByConsumer("consumer-id", "your-token");
    }
}
