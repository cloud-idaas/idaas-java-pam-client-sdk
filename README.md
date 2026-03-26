# idaas-java-pam-client-sdk

[![Java Version](https://img.shields.io/badge/java-8%2B-blue)](https://www.java.com/)
[![License](https://img.shields.io/badge/license-Apache%202.0-green.svg)](LICENSE)
[![Development Status](https://img.shields.io/badge/status-Beta-orange)](https://mvnrepository.com/artifact/com.cloud-idaas/idaas-java-pam-client)

[简体中文](README_zh.md)

## Features

- **Credential Management**: Support for retrieving API Keys, OAuth authentication tokens, JWT authentication tokens, and other credentials
- **Authentication Token Lifecycle Management**: Support for generating, querying, revoking, reinstating, and validating authentication tokens

## Requirements

- JDK 1.8 or above
- Maven

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.cloud-idaas</groupId>
    <artifactId>idaas-java-pam-client</artifactId>
    <version>0.0.2-beta</version>
</dependency>
```
[Latest Version](https://mvnrepository.com/artifact/com.cloud-idaas/idaas-java-pam-client)

## Quick Start

> **Important**: Before using this SDK, you need to complete the initialization configuration of idaas-java-core-sdk.    
> For details, please refer to: https://github.com/cloud-idaas/idaas-java-core-sdk/blob/main/README.md

### 1. Configuration File

Create a configuration file at `~/.cloud_idaas/client_config.json`:

```json
{
    "idaasInstanceId": "your-idaas-instance-id",
    "clientId": "your-client-id",
    "issuer": "your-idaas-issuer-url",
    "tokenEndpoint": "your-idaas-token-endpoint",
    "scope": "your-requested-scope",
    "developerApiEndpoint": "your-developer-api-endpoint",
    "authnConfiguration": {
        "identityType": "CLIENT",
        "authnMethod": "CLIENT_SECRET_POST",
        "clientSecretEnvVarName": "IDAAS_CLIENT_SECRET"
    }
}
```

### 2. Environment Variables

Set the environment variable:

```bash
export IDAAS_CLIENT_SECRET="your-client-secret"
```

### 3. Usage in Code

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;

public class Sample {

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
```

## API Reference

### getApiKey

Purpose: Retrieve a valid API Key.

Request Parameters:

| **Parameter** | **Type** | **Required** | **Description**                                                    |
| --- | --- | --- |-----------------------------------------------------------|
| credentialIdentifier | String | Yes | The business identifier of the credential.<br>*   How to obtain: In the EIAM Console, navigate to Credentials -> Credentials, and fill in when creating a credential. |

Response:

| **Parameter** | **Type** | **Always Returned** | **Description**                   |
| --- | --- | --- |------------------|
| apiKey | String | Yes | The content of the API Key.<br>*   Note: Contains sensitive information. |

### fetchOAuthAuthenticationToken

Purpose: Retrieve a valid OAuth authentication token.

Request Parameters:

| **Parameter**                      | **Type** | **Required** | **Description**                                                                                                          |
|------------------------------|--------| --- |-----------------------------------------------------------------------------------------------------------------|
| credentialProviderIdentifier | String | Yes | The business identifier of the credential provider.<br>*   How to obtain: In the EIAM Console, navigate to Credentials -> Credential Providers, and fill in when creating a credential provider.                                              |
| scope | String | No | The scope in OAuth protocol.<br>*   Multiple scopes should be separated by spaces. <br>*   Maximum length is 256 characters. <br>*   If not specified, the Scope configured when creating the credential provider will be used for the OAuth request. |

Response:

| **Parameter** | **Type** | **Always Returned** | **Description**                                                        |
| --- | --- | --- |---------------------------------------------------------------|
| accessTokenValue | String | Yes | Corresponds to the access_token in the OAuth AccessToken response.<br>*   Note: Contains sensitive information. |


### generateJwtAuthenticationToken

Purpose: Retrieve a valid JWT authentication token.

Request Parameters:

| **Parameter** | **Type** | **Required** | **Description**                                                                                                                                                                                                                         |
| --- | --- | --- |------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| credentialProviderIdentifier | String | Yes | The business identifier of the credential provider.<br>*   How to obtain: In the EIAM Console, navigate to Credentials -> Credential Providers, and fill in when creating a credential provider.                                                                                                                                                             |
| issuer | String | No | Corresponds to the `iss` field in JWT.<br>*   If the caller wants the issued JWT to have a custom issuer, this field can be used.<br>*   If not provided, defaults to the issuer of the corresponding JWT credential provider (indicating the JWT is issued by IDaaS EIAM).<br>*   Note: If an **issuer whitelist** is configured on the credential provider, the provided issuer value will be validated against the whitelist during JWT issuance; if not in the whitelist, issuance will fail. |
| subject | String | Yes | Corresponds to the `sub` field in JWT.                                                                                                                                                                                                         |
| audiences | List<String> | Yes | Corresponds to the `aud` field in JWT.<br>*   Multiple audiences can be provided.<br>*   Important: Must not start with IDaaS reserved audience prefix: `urn:cloud:idaas`.                                                                                                                               |
| customClaims | Map<String,Object> | No | Custom Claims.<br>*   Note: This is a map structure where the key must be a String, and the value can be any type.                                                                                                                                                             |
| expiration | Integer | No | The validity period of the JWT in seconds.<br>*   Note: If not provided, the validity period configured on the corresponding JWT provider will be used.                                                                                                                                                                        |
| includeDerivedShortToken | boolean | No | Whether to generate a derived short token.                                                                                                                                                                                                         |

Response:

| **Parameter** | **Type** | **Always Returned** | **Description**                                                                                  |
| --- | --- | --- |-----------------------------------------------------------------------------------------|
| jwtContent | Object | Yes | The content of the JWT authentication token.                                                                         |
| └jwtValue | String | Yes | The JWT content.<br>*   Note: Contains sensitive information.                                                               |
| └derivedShortToken | String | No | The derived short token of the JWT.<br>*   Note: Has the same effect as the JWT authentication token itself, used to solve the problem of JWT token length incompatibility on certain platforms.<br>*   This field itself is also a **sensitive field**. |

### obtainJwtAuthenticationTokenByDerivedShortToken

Purpose: Retrieve a JWT authentication token using a derived short token.

Request Parameters:

| **Parameter** | **Type** | **Required** | **Description**                                                    |
| --- | --- | --- |-----------------------------------------------------------|
| derivedShortToken | String | Yes | The derived short token of the JWT authentication token. |

Response:

| **Parameter** | **Type** | **Always Returned** | **Description**                   |
| --- | --- | --- |------------------|
| jwtContent | Object | Yes | The content of the JWT authentication token.                                                                         |
| └jwtValue | String | Yes | The JWT content.<br>*   Note: Contains sensitive information.                                                               |
| └derivedShortToken | String | No | The derived short token of the JWT.<br>*   Note: Has the same effect as the JWT authentication token itself, used to solve the problem of JWT token length incompatibility on certain platforms.<br>*   This field itself is also a **sensitive field**. |

### listAuthenticationTokens

Purpose: List authentication tokens.

Request Parameters:

| **Parameter** | **Type** | **Required** | **Description**                 |
| --- |--------| --- |------------------------|
| consumerId | String | Yes | The consumer ID of the authentication token.            |
| credentialProviderId | String | Yes | The credential provider identifier.               |
| nextToken | String | No | Pagination token for the next page starting position index. |
| maxResults | Long   | No | Maximum number of records to return in this query.     |
| revoked | Boolean | No | Whether the authentication token has been revoked.             |
| expired | Boolean | No | Whether the authentication token has expired.             |

**Response**:

| **Parameter**                    | **Type** | **Always Returned** | **Description**                                                 |
|----------------------------| --- |---|--------------------------------------------------------|
| nextTokenPageableResponse  | NextTokenPageableResponse | Yes | Paginated query results.                                                |
| └ entities                 | List | Yes | List of authentication tokens.                                                |
| └└ instanceId              | String | Yes | The IDaaS instance ID.                                          |
| └└ authenticationTokenId   | String | Yes | The authentication token ID.                                                |
| └└ credentialProviderId    | String | Yes | The credential provider identifier.                                               |
| └└ createTime              | Long | No | The creation time of the authentication token, Unix timestamp.                                    |
| └└ updateTime              | Long | No | The last update time of the authentication token, Unix timestamp.                                    |
| └└ authenticationTokenType | String<br> | Yes | The type of the authentication token.<br>*   Enum values: `oauth_access_token`, `jwt`.          |
| └└ revoked                 | Boolean | Yes | Whether the authentication token has been revoked.                                             |
| └└ creatorType             | String<br> | Yes | The creator type of the authentication token.<br>*   Enum value: `application`                   |
| └└ creatorId               | String | Yes | The creator ID of the authentication token.                                            |
| └└ consumerType            | String<br> | Yes | The consumer type of the authentication token.<br>*   Enum values: `custom` (custom type), `application` (application) |
| └└ consumerId              | String | Yes | The consumer ID of the authentication token.                                            |
| └└ expirationTime          | Integer | Yes | The expiration time of the authentication token, Unix timestamp.                                     |
| └ totalCount               | Long   | Yes | The total number of authentication token records.                                             |
| └ nextToken                | String | Yes | Pagination token for the next page starting position index.                                 |
| └ maxResults               | Long   | Yes | Maximum number of records returned in this query.                                     |

### validateAuthenticationToken

Purpose: Validate an authentication token.

Request Parameters:

| **Parameter** | **Type** | **Required** | **Description**                                                                                                                    |
| --- | --- | --- |---------------------------------------------------------------------------------------------------------------------------|
| token | String | Yes | The plaintext of the authentication token.<br>*   Note: Sensitive field<br>*   This field can accept either `jwtContent.jwtValue` or `jwtContent.derivedShortToken`. Both the JWT token itself and its corresponding derived short token can be used for validation. |

**Response**:

| **Parameter** | **Type** | **Always Returned** | **Description** |
| --- | --- | --- | --- |
| active | Boolean | Yes | Whether the authentication token is still valid. |

### revokeAuthenticationToken

Purpose: Revoke an authentication token.

Request Parameters:

| **Parameter** | **Type** | **Required** | **Description**                                                                                                                    |
| --- | --- | --- |---------------------------------------------------------------------------------------------------------------------------|
| token | String | Yes | The plaintext of the authentication token.<br>*   Note: Sensitive field<br>*   This field can accept either `jwtContent.jwtValue` or `jwtContent.derivedShortToken`. Both the JWT token itself and its corresponding derived short token can be used for revocation. |
| token_type_hint | String | No | A hint about the type of the token.<br>*   Currently not required.                                                                                                  |

**Response**:
None

### revokeAuthenticationTokenByConsumer

Purpose: Revoke authentication tokens by consumer ID.

Request Parameters:

| **Parameter** | **Type** | **Required** | **Description**      |
| --- | --- | --- |-------------|
| consumerId | String | Yes | The consumer ID of the authentication token. |
| credentialProviderId | String | Yes | The credential provider identifier.    |

**Response**:
None

### reinstateAuthenticationToken

Purpose: Reinstate a revoked authentication token.

Request Parameters:

| **Parameter** | **Type** | **Required** | **Description**      |
| --- | --- | --- |-------------|
| token | String | Yes | The plaintext of the authentication token.    |
| token_type_hint | String | No | A hint about the type of the token.<br>*   Currently not required.    |

**Response**:
None

### reinstateAuthenticationTokenByConsumer

Purpose: Reinstate authentication tokens by consumer ID.

Request Parameters:

| **Parameter** | **Type** | **Required** | **Description**      |
| --- | --- | --- |-------------|
| consumerId | String | Yes | The consumer ID of the authentication token. |
| credentialProviderId | String | Yes | The credential provider identifier.    |

**Response**:
None


## Complete Examples

For complete examples, see the `idaas-java-pam-client-example/` directory:

### Get API Key

```java
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
```

### Get API Key (Based on Token Exchange)

IDaaS supports token exchange capabilities. You can use the Access Token of the M2M client application configured in the user access profile to exchange for the Access Token of a credential, and then obtain the API Key with user identity.

```java
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
```

### Get OAuth Authentication Token

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.option.FetchOAuthAuthenticationOptions;

public class FetchOAuthAuthenticationTokenSample {

    public static void main(String[] args) {
        // Initialize (automatically load configuration file)
        IDaaSCredentialProviderFactory.init();

        // Create PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // Get OAuth authentication token
        // Without optional parameters
        String token = pamClient.fetchOAuthAuthenticationToken("your-credential-identifier");
        // With optional parameters
        // FetchOAuthAuthenticationOptions options = FetchOAuthAuthenticationOptions.builder()
        //         .scope("your-scope")
        //         .build();
        // String token = pamClient.fetchOAuthAuthenticationToken("your-credential-identifier", options);
        
        System.out.println("OAuth Token: " + token);
    }
}
```

### Generate JWT Authentication Token

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.domain.JwtContent;
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
        JwtContent jwtContent = pamClient.generateJwtAuthenticationToken(
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
        //JwtContent jwtContent = pamClient.generateJwtAuthenticationToken(
        //        "credential-provider-identifier",
        //        "subject",
        //        audiences,
        //        options
        //);

        System.out.println("JWT: " + jwtContent.getJwtValue());
        System.out.println("Derived Short Token: " + jwtContent.getDerivedShortToken());
    }
}
```

### Obtain JWT Authentication Token by Derived Short Token

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.domain.JwtContent;

public class ObtainJwtAuthenticationTokenByDerivedShortTokenSample {

    public static void main(String[] args) {
        // Initialize (automatically loads configuration file)
        IDaaSCredentialProviderFactory.init();

        // Create PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // Obtain JWT authentication token by derived short token
        JwtContent jwtContent = pamClient.obtainJwtAuthenticationTokenByDerivedShortToken("your-derived-short-token");
        
        System.out.println("JWT: " + jwtContent.getJwtValue());
        System.out.println("Derived Short Token: " + jwtContent.getDerivedShortToken());
    }
}
```

### List Authentication Tokens

```java
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
```

### Validate Authentication Token

```java
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
```

### Revoke Specified Authentication Token

```java
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
```

### Revoke Authentication Token by Consumer

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;

public class RevokeAuthenticationTokenByConsumerSample {

    public static void main(String[] args) {
        // Initialize (automatically loads configuration file)
        IDaaSCredentialProviderFactory.init();

        // Create PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // Revoke authentication token by consumer
        pamClient.revokeAuthenticationTokenByConsumer("consumer-id", "your-token");
    }
}
```

### Reinstate Revoked Authentication Token

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.option.ReinstateAuthenticationTokenOptions;

public class ReinstateAuthenticationTokenSample {

    public static void main(String[] args) {
        // Initialize (automatically load configuration file)
        IDaaSCredentialProviderFactory.init();

        // Create PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // Reinstate the revoked authentication token
        // Without optional parameters
        pamClient.reinstateAuthenticationToken("your-token");
        // With optional parameters
        //ReinstateAuthenticationTokenOptions options = ReinstateAuthenticationTokenOptions.builder()
        //        .tokenTypeHint("your-token-type-hint")
        //        .build();
        //pamClient.reinstateAuthenticationToken("your-token", options);
    }
}
```

### Reinstate Authentication Token by Consumer

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;

public class ReinstateAuthenticationTokenByConsumerSample {

    public static void main(String[] args) {
        // Initialize (automatically loads configuration file)
        IDaaSCredentialProviderFactory.init();

        // Create PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // Reinstate authentication token by consumer
        pamClient.reinstateAuthenticationTokenByConsumer("consumer-id", "your-token");
    }
}
```

## Support and Feedback

- **Email**: cloudidaas@list.alibaba-inc.com
- **Issue Feedback**: Please submit an Issue if you have any questions or suggestions

## License

This project is licensed under the [Apache License 2.0](LICENSE).
