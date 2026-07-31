# idaas-java-pam-client-sdk

[![Java Version](https://img.shields.io/badge/java-8%2B-blue)](https://www.java.com/)
[![License](https://img.shields.io/badge/license-Apache%202.0-green.svg)](LICENSE)
[![Development Status](https://img.shields.io/badge/status-Beta-orange)](https://mvnrepository.com/artifact/com.cloud-idaas/idaas-java-pam-client)

[简体中文](README_zh.md)

## Features

- **Credential Management**: Support for retrieving API Keys, OAuth authentication tokens, JWT authentication tokens, and other credentials
- **OAuth 2LO / 3LO**: Support for M2M (client credentials) and user federation (authorization code) flows, with end-to-end 3LO authorization orchestration
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
    <version>0.0.4-beta</version>
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
| credentialIdentifier | String | Yes | The business identifier of the credential.<br>*   How to obtain: In the EIAM Console, navigate to Credential -> Credential, and fill in when creating a credential. |

Response:

| **Parameter** | **Type** | **Always Returned** | **Description**                   |
| --- | --- | --- |------------------|
| apiKey | String | Yes | The content of the API Key.<br>*   Note: Contains sensitive information. |

### fetchOAuthAuthenticationToken

> **Deprecated**: Use [fetchOAuthAuthenticationTokenV2](#fetchoauthauthenticationtokenv2) instead. This method only supports 2LO and returns the access token string; its signature and return type remain unchanged for backward compatibility.

Purpose: Retrieve a valid OAuth authentication token.

Request Parameters:

| **Parameter**                      | **Type** | **Required** | **Description**                                                                                                          |
|------------------------------|--------| --- |-----------------------------------------------------------------------------------------------------------------|
| credentialProviderIdentifier | String | Yes | The business identifier of the credential provider.<br>*   How to obtain: In the EIAM Console, navigate to Credential -> Credential Provider, and fill in when creating a credential provider.                                              |
| scope | String | No | The scope in OAuth protocol.<br>*   Multiple scopes should be separated by spaces. <br>*   Maximum length is 256 characters. <br>*   If not specified, the Scope configured when creating the credential provider will be used for the OAuth request. |

Response:

| **Parameter** | **Type** | **Always Returned** | **Description**                                                        |
| --- | --- | --- |---------------------------------------------------------------|
| accessTokenValue | String | Yes | Corresponds to the access_token in the OAuth AccessToken response.<br>*   Note: Contains sensitive information. |


### fetchOAuthAuthenticationTokenV2

Purpose: Retrieve a valid OAuth authentication token, covering both the 2LO (m2m) and 3LO (user_federation) flows. Recommended replacement for `fetchOAuthAuthenticationToken`.

Request Parameters:

| **Parameter** | **Type** | **Required** | **Description** |
| --- | --- | --- | --- |
| credentialProviderIdentifier | String | Yes | The business identifier of the credential provider. |
| authorizationFlow | String | Yes | The authorization flow, either `OAuthAuthorizationFlow.M2M` (`m2m`) or `OAuthAuthorizationFlow.USER_FEDERATION` (`user_federation`).<br>*   Used only for SDK-side validation, NOT sent to the server. |
| scope | String | No | The scope in OAuth protocol (via `FetchOAuthAuthenticationOptions`). |
| forceAuthentication | Boolean | No | Whether to force re-authorization, ignoring any existing valid token (via `FetchOAuthAuthenticationOptions`). |
| customParameters | Map<String, String> | No | Custom parameters appended to the OAuth authorization URL query parameters (via `FetchOAuthAuthenticationOptions`). |

Response:

| **Parameter** | **Type** | **Always Returned** | **Description** |
| --- | --- | --- | --- |
| oAuthAuthenticationTokenResponse | Object | Yes | The OAuth authentication token response. `oauthAccessTokenContent` and `oauthAuthorizationSession` are mutually exclusive; check with `hasOAuthAccessTokenContent()` / `hasOAuthAuthorizationSession()`. |
| └ instanceId | String | No | The IDaaS instance ID. |
| └ authenticationTokenId | String | No | The authentication token ID. |
| └ authenticationTokenType | String | No | The authentication token type.<br>*   Enum: `oauth_access_token`. |
| └ credentialProviderId | String | No | The credential provider identifier. |
| └ consumerType | String | No | The consumer type of the authentication token.<br>*   Enum: `custom, application` |
| └ consumerId | String | No | The consumer ID of the authentication token. |
| └ creatorType | String | No | The creator type of the authentication token.<br>*   Enum: `application` |
| └ creatorId | String | No | The creator ID of the authentication token. |
| └ createTime | Long | No | The creation time of the authentication token, as a Unix timestamp. |
| └ updateTime | Long | No | The update time of the authentication token, as a Unix timestamp. |
| └ expirationTime | Long | No | The expiration time of the authentication token, as a Unix timestamp. |
| └ revoked | Boolean | No | Whether the authentication token has been revoked. |
| └ oauthAccessTokenContent | Object | No | The OAuth access token content. Present when a valid token is available. |
| └└ accessTokenValue | String | Yes | Corresponds to the access_token in the OAuth AccessToken response.<br>*   Note: Contains sensitive information. |
| └└ tokenType | String | Yes | The token type, usually `Bearer`. |
| └└ scope | String | No | The authorized scope. |
| └ oauthAuthorizationSession | Object | No | The OAuth authorization session info. Present when user authorization is required (3LO). |
| └└ sessionId | String | Yes | The authorization session ID. |
| └└ sessionUri | String | Yes | The authorization session URI, in the format `urn:ietf:params:oauth:request_uri:{sessionId}`. |
| └└ authorizationUrl | String | Yes | The URL the end user must open in a browser to complete authorization. |
| └└ sessionStatus | String | Yes | The authorization session status, e.g. `pending`. |

### getOAuthAuthorizationSession

Purpose: Query the status of an OAuth authorization session (3LO flow).

Request Parameters:

| **Parameter** | **Type** | **Required** | **Description** |
| --- | --- | --- | --- |
| sessionUri | String | Yes | The authorization session URI returned by `fetchOAuthAuthenticationTokenV2`. |

Response:

| **Parameter** | **Type** | **Always Returned** | **Description** |
| --- | --- | --- | --- |
| oAuthAuthorizationSessionResponse | Object | Yes | The OAuth authorization session query result. |
| └ instanceId | String | Yes | The IDaaS instance ID. |
| └ sessionId | String | Yes | The authorization session ID. |
| └ sessionUri | String | Yes | The authorization session URI, in the format `urn:ietf:params:oauth:request_uri:{sessionId}`. |
| └ sessionStatus | String | Yes | The session status.<br>*   Enum: `pending, callback_received, completed, failed, expired`. |
| └ credentialProviderIdentifier | String | Yes | The business identifier of the credential provider. |
| └ consumerType | String | Yes | The consumer type of the authentication token.<br>*   Enum: `custom, application` |
| └ consumerId | String | Yes | The consumer ID of the authentication token. |
| └ creatorType | String | Yes | The creator type of the authentication token.<br>*   Enum: `application` |
| └ creatorId | String | Yes | The creator ID of the authentication token. |
| └ authorizationUrl | String | No | The URL the end user must open to authorize. Returned when status is `pending`. |
| └ expirationTime | Long | Yes | The session expiration time, as a Unix timestamp. |
| └ authenticationTokenId | String | No | The associated authentication token ID. Returned when status is `completed`. |
| └ errorCode | String | No | The error code. Returned when status is `failed`. |
| └ errorDescription | String | No | The error description. Returned when status is `failed`. |

### pollOAuthAuthenticationToken

Purpose: End-to-end 3LO helper. Initiates authorization, notifies the caller of the authorization URL via callback, polls the session until completion, and returns a response containing the access token. Synchronous and blocks up to 180 seconds (polling interval fixed at 3 seconds).

Request Parameters:

| **Parameter** | **Type** | **Required** | **Description** |
| --- | --- | --- | --- |
| credentialProviderIdentifier | String | Yes | The business identifier of the credential provider. |
| onAuthorizationUrl | Consumer<String> | Yes | Callback invoked with the authorization URL when user authorization is required. |
| scope / forceAuthentication / customParameters | - | No | Same as `fetchOAuthAuthenticationTokenV2` (via `PollOAuthAuthenticationTokenOptions`). |
| maxPollingRetries | Integer | No | Maximum number of polling retries, default 60. The 180-second hard limit always takes precedence (via `PollOAuthAuthenticationTokenOptions`). |

Response:

| **Parameter** | **Type** | **Always Returned** | **Description** |
| --- | --- | --- | --- |
| oAuthAuthenticationTokenResponse | Object | Yes | The OAuth authentication token response. On success this method always contains `oauthAccessTokenContent` and never returns `oauthAuthorizationSession`. The top-level token fields are the same as the response of `fetchOAuthAuthenticationTokenV2`. |
| └ oauthAccessTokenContent | Object | Yes | The OAuth access token content. |
| └└ accessTokenValue | String | Yes | Corresponds to the access_token in the OAuth AccessToken response.<br>*   Note: Contains sensitive information. |
| └└ tokenType | String | Yes | The token type, usually `Bearer`. |
| └└ scope | String | No | The authorized scope. |


### generateJwtAuthenticationToken

Purpose: Retrieve a valid JWT authentication token.

Request Parameters:

| **Parameter** | **Type** | **Required** | **Description**                                                                                                                                                                                                                         |
| --- | --- | --- |------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| credentialProviderIdentifier | String | Yes | The business identifier of the credential provider.<br>*   How to obtain: In the EIAM Console, navigate to Credential -> Credential Provider, and fill in when creating a credential provider.                                                                                                                                                             |
| issuer | String | No | Corresponds to the `iss` field in JWT.<br>*   If the caller wants the issued JWT to have a custom issuer, this field can be used.<br>*   If not provided, defaults to the issuer of the corresponding JWT credential provider (indicating the JWT is issued by IDaaS EIAM).<br>*   Note: If an **issuer whitelist** is configured on the credential provider, the provided issuer value will be validated against the whitelist during JWT issuance; if not in the whitelist, issuance will fail. |
| subject | String | Yes | Corresponds to the `sub` field in JWT.                                                                                                                                                                                                         |
| audiences | List<String> | Yes | Corresponds to the `aud` field in JWT.<br>*   Multiple audiences can be provided.<br>*   Important: Must not start with IDaaS reserved audience prefix: `urn:cloud:idaas`.                                                                                                                               |
| customClaims | Map<String,Object> | No | Custom Claims.<br>*   Note: This is a map structure where the key must be a String, and the value can be any type.                                                                                                                                                             |
| expiration | Integer | No | The validity period of the JWT in seconds.<br>*   Note: If not provided, the validity period configured on the corresponding JWT provider will be used.                                                                                                                                                                        |
| includeDerivedShortToken | boolean | No | Whether to generate a derived short token.                                                                                                                                                                                                         |

Response:

| **Parameter**        | **Type** | **Always Returned** | **Description**                                                                                  |
|----------------------| --- | --- |-----------------------------------------------------------------------------------------|
| JwtTokenResponse     | Object | Yes | The content of the JWT authentication token response.                                                                                         |
| └ authenticationTokenId | String | Yes | The authentication token ID.                                                                                                                              |
| └ consumerType       | String | Yes | The consumer type of the authentication token.<br>*   Enum values: `custom` (custom type), `application` (application) |
| └ consumerId         | String | Yes | The consumer ID of the authentication token.                                                                              |
| └ jwtContent         | Object | Yes | The content of the JWT authentication token.                                                                         |
| └└ jwtValue          | String | Yes | The JWT content.<br>*   Note: Contains sensitive information.                                                               |
| └└ derivedShortToken | String | No | The derived short token of the JWT.<br>*   Note: Has the same effect as the JWT authentication token itself, used to solve the problem of JWT token length incompatibility on certain platforms.<br>*   This field itself is also a **sensitive field**. |

### obtainJwtAuthenticationToken

Purpose: Retrieve a JWT authentication token by consumer ID and authentication token ID.

Request Parameters:

| **Parameter** | **Type** | **Required** | **Description**                                                    |
| --- | --- | --- |-----------------------------------------------------------|
| consumerId | String | Yes | The consumer ID of the authentication token. |
| authenticationTokenId | String | Yes | The authentication token ID. |

Response:

| **Parameter**       | **Type** | **Always Returned** | **Description**                   |
|---------------------| --- | --- |------------------|
| jwtContent          | Object | Yes | The content of the JWT authentication token.                                                                         |
| └ jwtValue          | String | Yes | The JWT content.<br>*   Note: Contains sensitive information.                                                               |
| └ derivedShortToken | String | No | The derived short token of the JWT.<br>*   Note: Has the same effect as the JWT authentication token itself, used to solve the problem of JWT token length incompatibility on certain platforms.<br>*   This field itself is also a **sensitive field**. |

### obtainJwtAuthenticationTokenByDerivedShortToken

Purpose: Retrieve a JWT authentication token using a derived short token.

Request Parameters:

| **Parameter** | **Type** | **Required** | **Description**                                                    |
| --- | --- | --- |-----------------------------------------------------------|
| derivedShortToken | String | Yes | The derived short token of the JWT authentication token. |

Response:

| **Parameter**       | **Type** | **Always Returned** | **Description**                   |
|---------------------| --- | --- |------------------|
| jwtContent          | Object | Yes | The content of the JWT authentication token.                                                                         |
| └ jwtValue          | String | Yes | The JWT content.<br>*   Note: Contains sensitive information.                                                               |
| └ derivedShortToken | String | No | The derived short token of the JWT.<br>*   Note: Has the same effect as the JWT authentication token itself, used to solve the problem of JWT token length incompatibility on certain platforms.<br>*   This field itself is also a **sensitive field**. |

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

### Fetch OAuth Authentication Token

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

### Fetch OAuth Authentication Token V2 (2LO, Recommended)

Use `fetchOAuthAuthenticationTokenV2` with `m2m` flow explicitly.

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.domain.OAuthAuthenticationTokenResponse;
import com.cloud_idaas.pam.domain.OAuthAuthorizationFlow;

public class FetchOAuthAuthenticationTokenV2Sample {

    public static void main(String[] args) {
        // Initialize (automatically load configuration file)
        IDaaSCredentialProviderFactory.init();

        // Create PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // Fetch OAuth authentication token (2LO / M2M)
        OAuthAuthenticationTokenResponse response = pamClient.fetchOAuthAuthenticationTokenV2(
                "your-credential-provider-identifier", OAuthAuthorizationFlow.M2M);
        // With optional parameters
        // FetchOAuthAuthenticationOptions options = FetchOAuthAuthenticationOptions.builder()
        //         .scope("your-scope")
        //         .build();
        // OAuthAuthenticationTokenResponse response = pamClient.fetchOAuthAuthenticationTokenV2(
        //         "your-credential-provider-identifier", OAuthAuthorizationFlow.M2M, options);

        if (response.hasOAuthAccessTokenContent()) {
            System.out.println("Access Token: " + response.getOauthAccessTokenContent().getAccessTokenValue());
            System.out.println("Token Type: " + response.getOauthAccessTokenContent().getTokenType());
            System.out.println("Scope: " + response.getOauthAccessTokenContent().getScope());
        }
    }
}
```

### OAuth 3LO Authorization (End-to-End, Recommended)

`pollOAuthAuthenticationToken` encapsulates the full 3LO flow: initiate authorization → notify URL via callback → poll until user authorizes → fetch token. Best suited for Agent / CLI scenarios.

> 3LO session APIs require a user-auth access token; below we build the PAM client via **token exchange**.

```java
import com.cloud_idaas.core.credential.IDaaSCredential;
import com.cloud_idaas.core.domain.constants.OAuth2Constants;
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.core.implementation.StaticIDaaSCredentialProvider;
import com.cloud_idaas.core.provider.IDaaSCredentialProvider;
import com.cloud_idaas.core.provider.IDaaSTokenExchangeCredentialProvider;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.domain.OAuthAuthenticationTokenResponse;

public class OAuth3loEndToEndSample {

    public static void main(String[] args) {
        // Initialize (automatically load configuration file)
        IDaaSCredentialProviderFactory.init();

        // Exchange for a user-auth credential
        IDaaSTokenExchangeCredentialProvider tokenExchangeProvider = IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider();
        IDaaSCredential credential = tokenExchangeProvider.getCredential("your-subject-token", OAuth2Constants.ACCESS_TOKEN_TYPE, OAuth2Constants.ACCESS_TOKEN_TYPE);
        IDaaSCredentialProvider credentialProvider = StaticIDaaSCredentialProvider.builder()
                .setCredential(credential)
                .build();
        IDaaSPamClient pamClient = IDaaSPamClient.builder()
                .credentialProvider(credentialProvider)
                .build();

        // End-to-end OAuth token fetch (blocks until authorization completes)
        OAuthAuthenticationTokenResponse response = pamClient.pollOAuthAuthenticationToken(
                "your-oauth-3lo-credential-provider-identifier",
                authorizationUrl -> System.out.println("Please open this URL in a browser to authorize:\n" + authorizationUrl));
        // With optional parameters
        // PollOAuthAuthenticationTokenOptions options = PollOAuthAuthenticationTokenOptions.builder()
        //         .scope("your-scope")
        //         .forceAuthentication(true)
        //         .maxPollingRetries(60)
        //         .build();
        // OAuthAuthenticationTokenResponse response = pamClient.pollOAuthAuthenticationToken(
        //         "your-oauth-3lo-credential-provider-identifier",
        //         authorizationUrl -> System.out.println(authorizationUrl),
        //         options);

        if (response.hasOAuthAccessTokenContent()) {
            System.out.println("Access Token: " + response.getOauthAccessTokenContent().getAccessTokenValue());
        }
    }
}
```

### OAuth 3LO Authorization (Atomic Mode)

The caller orchestrates the polling loop, suitable for custom UI interactions or polling strategies.

```java
import com.cloud_idaas.core.credential.IDaaSCredential;
import com.cloud_idaas.core.domain.constants.OAuth2Constants;
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.core.implementation.StaticIDaaSCredentialProvider;
import com.cloud_idaas.core.provider.IDaaSCredentialProvider;
import com.cloud_idaas.core.provider.IDaaSTokenExchangeCredentialProvider;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.domain.OAuthAuthenticationTokenResponse;
import com.cloud_idaas.pam.domain.OAuthAuthorizationFlow;
import com.cloud_idaas.pam.domain.OAuthAuthorizationSession;
import com.cloud_idaas.pam.domain.OAuthAuthorizationSessionResponse;
import com.cloud_idaas.pam.domain.PamClientConstants;

public class OAuth3loAtomicSample {

    public static void main(String[] args) throws InterruptedException {
        IDaaSCredentialProviderFactory.init();

        IDaaSTokenExchangeCredentialProvider tokenExchangeProvider = IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider();
        IDaaSCredential credential = tokenExchangeProvider.getCredential("your-subject-token", OAuth2Constants.ACCESS_TOKEN_TYPE, OAuth2Constants.ACCESS_TOKEN_TYPE);
        IDaaSCredentialProvider credentialProvider = StaticIDaaSCredentialProvider.builder()
                .setCredential(credential)
                .build();
        IDaaSPamClient pamClient = IDaaSPamClient.builder()
                .credentialProvider(credentialProvider)
                .build();

        String credentialProviderIdentifier = "your-oauth-3lo-credential-provider-identifier";

        // 1. Initiate authorization (user_federation flow)
        OAuthAuthenticationTokenResponse response = pamClient.fetchOAuthAuthenticationTokenV2(
                credentialProviderIdentifier, OAuthAuthorizationFlow.USER_FEDERATION);

        if (response.hasOAuthAccessTokenContent()) {
            // 2. Token already available
            System.out.println("Access Token: " + response.getOauthAccessTokenContent().getAccessTokenValue());
        } else {
            // 3. User authorization required: show URL and poll session
            OAuthAuthorizationSession session = response.getOauthAuthorizationSession();
            System.out.println("Please open this URL in a browser to authorize:\n" + session.getAuthorizationUrl());

            while (true) {
                OAuthAuthorizationSessionResponse sessionResponse = pamClient.getOAuthAuthorizationSession(session.getSessionUri());
                String status = sessionResponse.getSessionStatus();
                System.out.println("Session status: " + status);
                if (PamClientConstants.SESSION_STATUS_COMPLETED.equals(status)) {
                    break;
                }
                if (PamClientConstants.SESSION_STATUS_FAILED.equals(status)
                        || PamClientConstants.SESSION_STATUS_EXPIRED.equals(status)) {
                    throw new RuntimeException("Authorization not completed: " + status);
                }
                Thread.sleep(PamClientConstants.POLLING_INTERVAL_MILLIS);
            }

            // 4. Authorization completed, fetch the token
            OAuthAuthenticationTokenResponse finalResponse = pamClient.fetchOAuthAuthenticationTokenV2(
                    credentialProviderIdentifier, OAuthAuthorizationFlow.USER_FEDERATION);
            System.out.println("Access Token: " + finalResponse.getOauthAccessTokenContent().getAccessTokenValue());
        }
    }
}
```

### Generate JWT Authentication Token

```java
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
```

### Obtain JWT Authentication Token

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.domain.JwtContent;

public class ObtainJwtAuthenticationTokenSample {

    public static void main(String[] args) {
        // Initialize (auto-load configuration file)
        IDaaSCredentialProviderFactory.init();

        // Create PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // Obtain JWT authentication token by consumer ID and authentication token ID
        JwtContent jwtContent = pamClient.obtainJwtAuthenticationToken("your-consumer-id", "your-authentication-token-id");

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
