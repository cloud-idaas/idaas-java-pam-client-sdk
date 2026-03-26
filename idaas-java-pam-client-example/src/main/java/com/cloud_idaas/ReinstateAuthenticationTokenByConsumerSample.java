package com.cloud_idaas;

import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;

public class ReinstateAuthenticationTokenByConsumerSample {

    public static void main(String[] args) {
        // Initialize (automatically load configuration file)
        IDaaSCredentialProviderFactory.init();

        // Create PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // Reinstate authentication token by consumer
        pamClient.reinstateAuthenticationTokenByConsumer("consumer-id", "your-token");
    }
}
