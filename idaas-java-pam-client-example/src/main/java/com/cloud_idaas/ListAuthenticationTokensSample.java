package com.cloud_idaas;

import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.domain.AuthenticationToken;
import com.cloud_idaas.pam.domain.NextTokenPageableResponse;
import com.cloud_idaas.pam.option.ListAuthenticationTokensOptions;

import java.util.List;

public class ListAuthenticationTokensSample {

    public static void main(String[] args) {
        // Initialize (auto-load configuration file)
        IDaaSCredentialProviderFactory.init();

        // Create PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // Query authentication token list
        // Without optional parameters
        NextTokenPageableResponse<AuthenticationToken> tokens = pamClient.listAuthenticationTokens(
                "consumer-id",
                "credential-provider-id"
        );
        // With optional parameters
        //ListAuthenticationTokensOptions options = ListAuthenticationTokensOptions.builder()
        //        .nextToken(null)
        //        .maxResults(10L)
        //        .revoked(false)
        //        .expired(false)
        //        .build();
        //NextTokenPageableResponse<AuthenticationToken> tokens = pamClient.listAuthenticationTokens(
        //        "consumer-id",
        //        "credential-provider-id",
        //        options
        //);

        System.out.println("Total Count: " + tokens.getTotalCount());
        System.out.println("Next Token: " + tokens.getNextToken());
        System.out.println("Max Results: " + tokens.getMaxResults());
        List<AuthenticationToken> authenticationTokens = tokens.getEntities();
        for (AuthenticationToken authenticationToken : authenticationTokens) {
            System.out.println(authenticationToken.getAuthenticationTokenId());
            System.out.println(authenticationToken.getAuthenticationTokenType());
            System.out.println(authenticationToken.getConsumerId());
            System.out.println(authenticationToken.getConsumerType());
            System.out.println(authenticationToken.getCreatorId());
            System.out.println(authenticationToken.getCreatorType());
            System.out.println(authenticationToken.getCredentialProviderId());
        }
    }
}
