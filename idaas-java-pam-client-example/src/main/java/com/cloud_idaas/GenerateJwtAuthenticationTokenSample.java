package com.cloud_idaas;

import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.domain.JwtContent;
import com.cloud_idaas.pam.domain.JwtTokenResponse;
import com.cloud_idaas.pam.option.GenerateJwtAuthenticationOptions;

import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class GenerateJwtAuthenticationTokenSample {

    public static void main(String[] args) {
        // Initialize (automatically load configuration file)
        IDaaSCredentialProviderFactory.init();

        // Create PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        List<String> audiences = Arrays.asList("audience1", "audience2");

        // Generate JWT authentication token
        // Without optional parameters
        JwtTokenResponse jwtTokenResponse = pamClient.generateJwtAuthenticationToken(
                "credential-provider-identifier",
                "subject",
                audiences
        );
        // With optional parameters
        //Map<String, Object> customClaims = new HashMap<>();
        //customClaims.put("key", "value");
        //GenerateJwtAuthenticationOptions options = GenerateJwtAuthenticationOptions.builder()
        //        .issuer("issuer")
        //        .customClaims(customClaims)
        //        .expiration(3600)
        //        .includeDerivedShortToken(true)
        //        .build();
        //JwtTokenResponse jwtTokenResponse = pamClient.generateJwtAuthenticationToken(
        //        "credential-provider-identifier",
        //        "subject",
        //        audiences,
        //        options
        //);

        System.out.println("Authentication Token Id" + jwtTokenResponse.getAuthenticationTokenId());
        System.out.println("Consumer Type: " + jwtTokenResponse.getConsumerType());
        System.out.println("Consumer ID: " + jwtTokenResponse.getConsumerId());
        System.out.println("JWT Token: " + jwtTokenResponse.getJwtContent().getJwtValue());
        System.out.println("Derived Short Token: " + jwtTokenResponse.getJwtContent().getDerivedShortToken());
    }
}