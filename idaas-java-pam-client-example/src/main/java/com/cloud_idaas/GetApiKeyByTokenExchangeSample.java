package com.cloud_idaas;

import com.cloud_idaas.core.credential.IDaaSCredential;
import com.cloud_idaas.core.domain.constants.OAuth2Constants;
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.core.implementation.StaticIDaaSCredentialProvider;
import com.cloud_idaas.core.provider.IDaaSCredentialProvider;
import com.cloud_idaas.core.provider.IDaaSTokenExchangeCredentialProvider;
import com.cloud_idaas.pam.IDaaSPamClient;

public class GetApiKeyByTokenExchangeSample {

    public static void main(String[] args) {
        // Initialize (auto-load configuration file)
        IDaaSCredentialProviderFactory.init();

        // Subject token to be exchanged
        String subjectToken = "your-subject-token";

        // Create Token Exchange credential provider
        IDaaSTokenExchangeCredentialProvider tokenExchangeProvider = IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider();

        // Get credential
        IDaaSCredential credential = tokenExchangeProvider.getCredential(subjectToken, OAuth2Constants.ACCESS_TOKEN_TYPE, OAuth2Constants.ACCESS_TOKEN_TYPE);

        // Create static credential provider
        IDaaSCredentialProvider credentialProvider = StaticIDaaSCredentialProvider.builder()
                .setCredential(credential)
                .build();

        // Create PAM Client through static credential provider
        IDaaSPamClient pamClient = IDaaSPamClient.builder()
                .credentialProvider(credentialProvider)
                .build();

        // 获取 API Key
        String apiKey = pamClient.getApiKey("your-credential-identifier");
        System.out.println("API Key: " + apiKey);
    }
}
