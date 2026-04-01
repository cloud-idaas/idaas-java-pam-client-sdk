# idaas-java-pam-client-sdk

[![Java Version](https://img.shields.io/badge/java-8%2B-blue)](https://www.java.com/)
[![License](https://img.shields.io/badge/license-Apache%202.0-green.svg)](LICENSE)
[![Development Status](https://img.shields.io/badge/status-Beta-orange)](https://mvnrepository.com/artifact/com.cloud-idaas/idaas-java-pam-client)

[English](README.md)

## 功能特性

- **凭据管理**：支持获取 API Key、OAuth 认证令牌、JWT 认证令牌等凭据
- **认证令牌生命周期管理**：支持生成、查询、吊销、恢复、验证认证令牌

## 环境要求

- 安装 JDK 1.8 或以上版本
- 安装 Maven

## 安装

在 `pom.xml` 中添加以下依赖：

```xml
<dependency>
    <groupId>com.cloud-idaas</groupId>
    <artifactId>idaas-java-pam-client</artifactId>
    <version>0.0.3-beta</version>
</dependency>
```
[最新版本](https://mvnrepository.com/artifact/com.cloud-idaas/idaas-java-pam-client)

## 快速开始

> **重要提示**：在使用 SDK 之前，需要先完成 idaas-java-core-sdk 的初始化配置。    
> 具体参考：https://github.com/cloud-idaas/idaas-java-core-sdk/blob/main/README_zh.md

### 1. 配置文件

创建配置文件 `~/.cloud_idaas/client_config.json`：

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

### 2. 环境变量

设置环境变量：

```bash
export IDAAS_CLIENT_SECRET="your-client-secret"
```

### 3. 代码中使用

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;

public class Sample {

    public static void main(String[] args) {
        // 初始化（自动加载配置文件）
        IDaaSCredentialProviderFactory.init();

        // 创建 PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // 获取 API Key
        String apiKey = pamClient.getApiKey("your-credential-identifier");
        System.out.println("API Key: " + apiKey);
    }
}
```

## API 参考

### getApiKey

作用：获取一个有效的API Key。

请求入参：

| **参数名** | **类型** | **是否必填** | **描述**                                                    |
| --- | --- | --- |-----------------------------------------------------------|
| credentialIdentifier | String | 是 | 凭据的业务标识。<br>*   该字段的获取方式：在 EIAM 控制台的 凭据 -> 凭据 界面，创建凭据时填写。 |

响应：

| **参数名** | **类型** | **是否一定返回** | **描述**                   |
| --- | --- | --- |------------------|
| apiKey | String | 是 | API Key 的内容。<br>*   注意包含有敏感信息。 |

### fetchOAuthAuthenticationToken

作用：获取一个有效的OAuth认证令牌。

请求入参：

| **参数名**                      | **类型** | **是否必填** | **描述**                                                                                                          |
|------------------------------|--------| --- |-----------------------------------------------------------------------------------------------------------------|
| credentialProviderIdentifier | String | 是 | 凭据提供商的业务标识。<br>*   该字段的获取方式：在 EIAM 控制台的 凭据 -> 凭据提供商 界面，创建凭据提供商时填写。                                              |
| scope | String | 否 | OAuth 协议的 scope。<br>* 多个 scope 之间空格分隔。 <br>* 整体字段长度不超过 256 <br>*  该字段若不指定，则发起 OAuth 请求时，会以创建凭据提供商时填写的 Scope 为准。 |

响应：

| **参数名** | **类型** | **是否一定返回** | **描述**                                                        |
| --- | --- | --- |---------------------------------------------------------------|
| accessTokenValue | String | 是 | 对应 OAuth 协议中的 AccessToken 响应的 access_token。<br>*   注意包含有敏感信息。 |


### generateJwtAuthenticationToken

作用：获取一个有效的JWT认证令牌。

请求入参：

| **参数名** | **类型** | **是否必填** | **描述**                                                                                                                                                                                                                         |
| --- | --- | --- |--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| credentialProviderIdentifier | String | 是 | 凭据提供商的业务标识。<br>*   该字段的获取方式：在 EIAM 控制台的 凭据 -> 凭据提供商 界面，创建凭据提供商时填写。                                                                                                                                                             |
| issuer | String | 否 | 对应JWT的`iss`字段。<br>*   若调用方希望签发出的 JWT 的 issuer 为自己指定的值，则可以通过传入该字段实现。<br>*   不填的话，默认与对应的 JWT 凭据提供商的 issuer（该 issuer 标识 JWT 是由 IDaaS EIAM 签发）。<br>*   注意若在凭据提供商上 **配置了 issuer 白名单**，则在签发 JWT 时会校验传入的 issuer 值是否在允许的白名单内，若不在则签发失败。 |
| subject | String | 是 | 对应 JWT 的`sub`字段。                                                                                                                                                                                                               |
| audiences | List<String> | 是 | 对应 JWT 的`aud`字段。<br>*   可以传入多个 audience。<br>*   重要不能以 IDaaS 的保留 audience 前缀开头：`urn:cloud:idaas`。                                                                                                                               |
| customClaims | Map<String,Object> | 否 | 自定义 Claim。<br>*   提示在传值上是一个 map 类型结构，key 必须是 String，value 可以是任意类型。                                                                                                                                                             |
| expiration | Integer | 否 | JWT 的有效时长，单位秒。<br>*   注意不传递该字段时，以对应的 JWT 提供商处的有效时长配置为准。                                                                                                                                                                        |
| includeDerivedShortToken | boolean | 否 | 是否要生成派生短令牌。                                                                                                                                                                                                                    |

响应：

| **参数名**              | **类型** | **是否一定返回** | **描述**                                                                                  |
|----------------------|--------| --- |-----------------------------------------------------------------------------------------|
| JwtTokenResponse     | Object | 是 | JWT 认证令牌响应内容。                                                                         |
| └ authenticationTokenId | String | 是 | 认证令牌ID。                                                                                       |
| └ consumerType       | String | 是 | 认证令牌的使用者类型。<br>*   枚举值：`custom（自定义类型）、application（应用）`                                    |
| └ consumerId         | String | 是 | 认证令牌的使用者ID。                                                                              |
| └ jwtContent         | Object | 是 | JWT 类型的认证令牌的内容。                                                                         |
| └└ jwtValue          | String | 是 | JWT 内容。<br>*   注意包含有敏感信息。                                                               |
| └└ derivedShortToken | String | 否 | JWT 的派生短令牌。<br>*   注意效力等同于 JWT 认证令牌本身，用于解决 JWT 认证令牌长度过长在某些平台上无法兼容的问题。<br>*   该字段本身也是一个**敏感字段**。 |

### obtainJwtAuthenticationToken

作用：通过使用者 ID 和认证令牌 ID 获取 JWT 认证令牌。

请求入参：

| **参数名** | **类型** | **是否必填** | **描述**                                                    |
| --- | --- | --- |-----------------------------------------------------------|
| consumerId | String | 是 | 认证令牌的使用者ID。 |
| authenticationTokenId | String | 是 | 认证令牌ID。 |

响应：

| **参数名**             | **类型** | **是否一定返回** | **描述**                   |
|---------------------| --- | --- |------------------|
| jwtContent          | Object | 是 | JWT 类型的认证令牌的内容。                                                                         |
| └ jwtValue          | String | 是 | JWT 内容。<br>*   注意包含有敏感信息。                                                               |
| └ derivedShortToken | String | 否 | JWT 的派生短令牌。<br>*   注意效力等同于 JWT 认证令牌本身，用于解决 JWT 认证令牌长度过长在某些平台上无法兼容的问题。<br>*   该字段本身也是一个**敏感字段**。 |

### obtainJwtAuthenticationTokenByDerivedShortToken

作用：通过派生短令牌获取 JWT 认证令牌。

请求入参：

| **参数名** | **类型** | **是否必填** | **描述**                                                    |
| --- | --- | --- |-----------------------------------------------------------|
| derivedShortToken | String | 是 | JWT 认证令牌的派生短令牌。 |

响应：

| **参数名**             | **类型** | **是否一定返回** | **描述**                   |
|---------------------| --- | --- |------------------|
| jwtContent          | Object | 是 | JWT 类型的认证令牌的内容。                                                                         |
| └ jwtValue          | String | 是 | JWT 内容。<br>*   注意包含有敏感信息。                                                               |
| └ derivedShortToken | String | 否 | JWT 的派生短令牌。<br>*   注意效力等同于 JWT 认证令牌本身，用于解决 JWT 认证令牌长度过长在某些平台上无法兼容的问题。<br>*   该字段本身也是一个**敏感字段**。 |

### listAuthenticationTokens

作用：列举认证令牌。

请求入参：

| **参数名** | **类型** | **是否必填** | **描述**                 |
| --- |--------| --- |------------------------|
| consumerId | String | 是 | 认证令牌的使用者ID。            |
| credentialProviderId | String | 是 | 凭据提供商标识。               |
| nextToken | String | 否 | 分页查询，下一页的起始位置索引 Token。 |
| maxResults | Long   | 否 | 分页查询，本次查询返回的最大记录数。     |
| revoked | Boolean | 否 | 认证令牌是否已吊销。             |
| expired | Boolean | 否 | 认证令牌是否已过期。             |

**响应**：

| **参数名**                    | **类型** | **是否一定返回** | **描述**                                                 |
|----------------------------| --- |---|--------------------------------------------------------|
| nextTokenPageableResponse  | NextTokenPageableResponse | 是 | 分页查询结果。                                                |
| └ entities                 | List | 是 | 认证令牌列表。                                                |
| └└ instanceId              | String | 是 | IDaaS 的实例 ID。                                          |
| └└ authenticationTokenId   | String | 是 | 认证令牌ID。                                                |
| └└ credentialProviderId    | String | 是 | 凭据提供商标识。                                               |
| └└ createTime              | Long | 否 | 认证令牌的创建时间，Unix 时间戳。                                    |
| └└ updateTime              | Long | 否 | 认证令牌的更新时间，Unix 时间戳。                                    |
| └└ authenticationTokenType | String<br> | 是 | 认证令牌的类型。<br>*   枚举值：`oauth_access_token、jwt`。          |
| └└ revoked                 | Boolean | 是 | 认证令牌是否被吊销。                                             |
| └└ creatorType             | String<br> | 是 | 认证令牌的创建者类型。<br>*   枚举值：`application`                   |
| └└ creatorId               | String | 是 | 认证令牌的创建者ID。                                            |
| └└ consumerType            | String<br> | 是 | 认证令牌的使用者类型。<br>*   枚举值：`custom（自定义类型）、application（应用）` |
| └└ consumerId              | String | 是 | 认证令牌的使用者ID。                                            |
| └└ expirationTime          | Integer | 是 | 认证令牌的过期时间，Unix时间戳。                                     |
| └ totalCount               | Long   | 是 | 认证令牌的总记录数。                                             |
| └ nextToken                | String | 是 | 分页查询，下一页的起始位置索引 Token。                                 |
| └ maxResults               | Long   | 是 | 分页查询，本次查询返回的最大记录数。                                     |

### validateAuthenticationToken

作用：校验一个认证令牌。

请求入参：

| **参数名** | **类型** | **是否必填** | **描述**                                                                                                                    |
| --- | --- | --- |---------------------------------------------------------------------------------------------------------------------------|
| token | String | 是 | 认证令牌明文。<br>*   注意敏感字段<br>*   该字段传值，可以是`jwtContent.jwtValue`，也可以是`jwtContent.derivedShortToken`。也即JWT令牌本身和对应的派生短令牌均可以用于校验。 |

**响应**：

| **参数名** | **类型** | **是否一定返回** | **描述** |
| --- | --- | --- | --- |
| active | Boolean | 是 | 认证令牌是否依然有效。 |

### revokeAuthenticationToken

作用：吊销一个认证令牌。

请求入参：

| **参数名** | **类型** | **是否必填** | **描述**                                                                                                                    |
| --- | --- | --- |---------------------------------------------------------------------------------------------------------------------------|
| token | String | 是 | 认证令牌明文。<br>*   注意敏感字段<br>*   该字段传值，可以是`jwtContent.jwtValue`，也可以是`jwtContent.derivedShortToken`。也即JWT令牌本身和对应的派生短令牌均可以用于吊销。 |
| token_type_hint | String | 否 | 认证令牌类型提示。<br>*  当前暂无需传值。                                                                                                  |

**响应**：
无

### revokeAuthenticationTokenByConsumer

作用：通过使用者 ID 吊销认证令牌。

请求入参：

| **参数名** | **类型** | **是否必填** | **描述**      |
| --- | --- | --- |-------------|
| consumerId | String | 是 | 认证令牌的使用者ID。 |
| credentialProviderId | String | 是 | 凭据提供商标识。    |

**响应**：
无

### reinstateAuthenticationToken

作用：恢复一个认证令牌。

请求入参：

| **参数名** | **类型** | **是否必填** | **描述**      |
| --- | --- | --- |-------------|
| token | String | 是 | 认证令牌明文。    |
| token_type_hint | String | 否 | 认证令牌类型提示。<br>*  当前暂无需传值。    |

**响应**：
无

### reinstateAuthenticationTokenByConsumer

作用：通过使用者 ID 恢复认证令牌。

请求入参：

| **参数名** | **类型** | **是否必填** | **描述**      |
| --- | --- | --- |-------------|
| consumerId | String | 是 | 认证令牌的使用者ID。 |
| credentialProviderId | String | 是 | 凭据提供商标识。    |

**响应**：
无


## 完整示例

完整示例请参见 `idaas-java-pam-client-example/` 目录：

### 获取 API Key

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;

public class GetApiKeySample {

    public static void main(String[] args) {
        // 初始化（自动加载配置文件）
        IDaaSCredentialProviderFactory.init();

        // 创建 PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // 获取 API Key
        String apiKey = pamClient.getApiKey("your-credential-identifier");
        
        System.out.println("API Key: " + apiKey);
    }
}
```

### 获取 API Key (基于 token exchange)

IDaaS 支持令牌交换能力，可以使用用户访问配置文件中 M2M 客户端应用的 Access Token 交换获取凭据的 Access Token，以用户身份获取 API Key。

```java
import com.cloud_idaas.core.credential.IDaaSCredential;
import com.cloud_idaas.core.domain.constants.OAuth2Constants;
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.core.implementation.StaticCredentialProvider;
import com.cloud_idaas.core.provider.IDaaSCredentialProvider;
import com.cloud_idaas.core.provider.IDaaSTokenExchangeCredentialProvider;
import com.cloud_idaas.pam.IDaaSPamClient;

public class GetApiKeyByTokenExchangeSample {

    public static void main(String[] args) {
        // 初始化（自动加载配置文件）
        IDaaSCredentialProviderFactory.init();

        // 要交换的主体令牌
        String subjectToken = "your-subject-token";

        // 创建 Token Exchange 凭据提供器
        IDaaSTokenExchangeCredentialProvider tokenExchangeProvider = IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider();

        // 获取凭证
        IDaaSCredential credential = tokenExchangeProvider.getCredential(subjectToken, OAuth2Constants.ACCESS_TOKEN_TYPE, OAuth2Constants.ACCESS_TOKEN_TYPE);

        // 创建静态凭据提供器
        IDaaSCredentialProvider credentialProvider = StaticIDaaSCredentialProvider.builder()
                .setCredential(credential)
                .build();

        // 通过静态凭据提供器创建 PAM Client
        IDaaSPamClient pamClient = IDaaSPamClient.builder()
                .credentialProvider(credentialProvider)
                .build();

        // 获取 API Key
        String apiKey = pamClient.getApiKey("your-credential-identifier");
        
        System.out.println("API Key: " + apiKey);
    }
}
```

### 获取 OAuth 认证令牌

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.option.FetchOAuthAuthenticationOptions;

public class FetchOAuthAuthenticationTokenSample {

    public static void main(String[] args) {
        // 初始化（自动加载配置文件）
        IDaaSCredentialProviderFactory.init();

        // 创建 PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // 获取 OAuth 认证令牌
        // 不带可选参数
        String token = pamClient.fetchOAuthAuthenticationToken("your-credential-identifier");
        // 带可选参数
        // FetchOAuthAuthenticationOptions options = FetchOAuthAuthenticationOptions.builder()
        //         .scope("your-scope")
        //         .build();
        // String token = pamClient.fetchOAuthAuthenticationToken("your-credential-identifier", options);
        
        System.out.println("OAuth Token: " + token);
    }
}
```

### 生成 JWT 认证令牌

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
        // 初始化（自动加载配置文件）
        IDaaSCredentialProviderFactory.init();

        // 创建 PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        List<String> audiences = Arrays.asList("audience1", "audience2");

        // 生成 JWT 认证令牌
        // 不带可选参数
        JwtTokenResponse jwtTokenResponse = pamClient.generateJwtAuthenticationToken(
                "credential-provider-identifier",
                "subject",
                audiences
        );
        // 带可选参数
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

### 获取 JWT 认证令牌

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.domain.JwtContent;

public class ObtainJwtAuthenticationTokenSample {

    public static void main(String[] args) {
        // 初始化（自动加载配置文件）
        IDaaSCredentialProviderFactory.init();

        // 创建 PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // 通过使用者 ID 和认证令牌 ID 获取 JWT 认证令牌
        JwtContent jwtContent = pamClient.obtainJwtAuthenticationToken("your-consumer-id", "your-authentication-token-id");

        System.out.println("JWT: " + jwtContent.getJwtValue());
        System.out.println("Derived Short Token: " + jwtContent.getDerivedShortToken());
    }
}

```

### 通过派生短令牌获取 JWT 认证令牌

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.domain.JwtContent;

public class ObtainJwtAuthenticationTokenByDerivedShortTokenSample {

    public static void main(String[] args) {
        // 初始化（自动加载配置文件）
        IDaaSCredentialProviderFactory.init();

        // 创建 PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // 通过派生短令牌获取 JWT 认证令牌
        JwtContent jwtContent = pamClient.obtainJwtAuthenticationTokenByDerivedShortToken("your-derived-short-token");
        
        System.out.println("JWT: " + jwtContent.getJwtValue());
        System.out.println("Derived Short Token: " + jwtContent.getDerivedShortToken());
    }
}

```
### 查询认证令牌列表

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.domain.AuthenticationToken;
import com.cloud_idaas.pam.domain.NextTokenPageableResponse;
import com.cloud_idaas.pam.option.ListAuthenticationTokensOptions;

import java.util.List;

public class ListAuthenticationTokensSample {

    public static void main(String[] args) {
        // 初始化（自动加载配置文件）
        IDaaSCredentialProviderFactory.init();

        // 创建 PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // 查询认证令牌列表
        // 不带可选参数
        NextTokenPageableResponse<AuthenticationToken> tokens = pamClient.listAuthenticationTokens(
                "consumer-id",
                "credential-provider-id"
        );
        // 带可选参数
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

### 验证认证令牌

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.option.ValidateAuthenticationTokenOptions;

public class ValidateAuthenticationTokenSample {

    public static void main(String[] args) {
        // 初始化（自动加载配置文件）
        IDaaSCredentialProviderFactory.init();

        // 创建 PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // 验证认证令牌
        // 不带可选参数
        Boolean isValid = pamClient.validateAuthenticationToken("your-token");
        // 带可选参数
        //ValidateAuthenticationTokenOptions options = ValidateAuthenticationTokenOptions.builder()
        //        .tokenTypeHint("your-token-type-hint")
        //        .build();
        //Boolean isValid = pamClient.validateAuthenticationToken("your-token", options);

        System.out.println("Token is valid: " + isValid);
    }
}
```

### 吊销指定的认证令牌

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.option.ReinstateAuthenticationTokenOptions;

public class RevokeAuthenticationTokenSample {

    public static void main(String[] args) {
        // 初始化（自动加载配置文件）
        IDaaSCredentialProviderFactory.init();

        // 创建 PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // 吊销指定的认证令牌
        // 不带可选参数
        pamClient.revokeAuthenticationToken("your-token");
        // 带可选参数
        //ReinstateAuthenticationTokenOptions options = ReinstateAuthenticationTokenOptions.builder()
        //        .tokenTypeHint("your-token-type-hint")
        //        .build();
        //pamClient.reinstateAuthenticationToken("your-token", options);
    }
}
```

### 根据使用者吊销认证令牌

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;

public class RevokeAuthenticationTokenByConsumerSample {

    public static void main(String[] args) {
        // 初始化（自动加载配置文件）
        IDaaSCredentialProviderFactory.init();

        // 创建 PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // 根据使用者吊销认证令牌
        pamClient.revokeAuthenticationTokenByConsumer("consumer-id", "your-token");
    }
}
```

### 恢复已吊销的认证令牌

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.option.ReinstateAuthenticationTokenOptions;

public class ReinstateAuthenticationTokenSample {

    public static void main(String[] args) {
        // 初始化（自动加载配置文件）
        IDaaSCredentialProviderFactory.init();

        // 创建 PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // 恢复已吊销的认证令牌
        // 不带可选参数
        pamClient.reinstateAuthenticationToken("your-token");
        // 带可选参数
        //ReinstateAuthenticationTokenOptions options = ReinstateAuthenticationTokenOptions.builder()
        //        .tokenTypeHint("your-token-type-hint")
        //        .build();
        //pamClient.reinstateAuthenticationToken("your-token", options);
    }
}
```

### 根据使用者恢复认证令牌

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;

public class ReinstateAuthenticationTokenByConsumerSample {

    public static void main(String[] args) {
        // 初始化（自动加载配置文件）
        IDaaSCredentialProviderFactory.init();

        // 创建 PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // 根据使用者恢复认证令牌
        pamClient.reinstateAuthenticationTokenByConsumer("consumer-id", "your-token");
    }
}
```

### 支持与反馈

- **邮箱**：cloudidaas@list.alibaba-inc.com
- **问题反馈**：如有问题或建议，请提交 Issue

## 许可证

本项目基于 [Apache License 2.0](LICENSE) 许可证授权。
