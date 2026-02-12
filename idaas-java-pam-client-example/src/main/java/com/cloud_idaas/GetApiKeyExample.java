package com.cloud_idaas;

import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;

/**
 * Hello world!
 */
public class GetApiKeyExample {
    public static void main(String[] args) {
        IDaaSCredentialProviderFactory.init();
        IDaaSPamClient pamClient = new IDaaSPamClient();
        String apiKey = pamClient.getApiKey("credentialIdentifier");
        System.out.println("Api Key: " + apiKey);
    }
}
