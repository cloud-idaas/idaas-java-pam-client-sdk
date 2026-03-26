package com.cloud_idaas;

import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;

public class GetApiKeySample {

    public static void main(String[] args) {
        // Initialize (automatically loads configuration file)
        IDaaSCredentialProviderFactory.init();

        // Create PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // Get API Key
        String apiKey = pamClient.getApiKey("your-credential-identifier");
        System.out.println("API Key: " + apiKey);
    }
}