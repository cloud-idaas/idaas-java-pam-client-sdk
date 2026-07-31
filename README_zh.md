# idaas-java-pam-client-sdk

[![Java Version](https://img.shields.io/badge/java-8%2B-blue)](https://www.java.com/)
[![License](https://img.shields.io/badge/license-Apache%202.0-green.svg)](LICENSE)
[![Development Status](https://img.shields.io/badge/status-Beta-orange)](https://mvnrepository.com/artifact/com.cloud-idaas/idaas-java-pam-client)

[English](README.md)

## 功能特性

- **凭据管理**：支持获取 API Key、OAuth 认证令牌、JWT 认证令牌等凭据
- **OAuth 2LO / 3LO**：支持 M2M（客户端凭据）与用户联合（授权码）两种授权流程，并提供端到端的 3LO 授权编排
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
    <version>0.0.4-beta</version>
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

> **已废弃**：请改用 [fetchOAuthAuthenticationTokenV2](#fetchoauthauthenticationtokenv2)。该方法仅支持 2LO 且只返回 access token 字符串；为保证向后兼容，其签名与返回类型保持不变。

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


### fetchOAuthAuthenticationTokenV2

作用：获取有效的 OAuth 认证令牌，同时覆盖 2LO（`m2m`）与 3LO（`user_federation`）两种授权流程，返回信息完整的富对象。
> **注意**：3LO 场景需要用户身份（user-auth）的 Access Token。

请求入参：

| **参数名** | **类型** | **是否必填** | **描述** |
| --- | --- | --- | --- |
| credentialProviderIdentifier | String | 是 | 凭据提供商的业务标识。 |
| authorizationFlow | String | 是 | OAuth 授权流程类型。<br>*   取值：`OAuthAuthorizationFlow.M2M`（`m2m`，即 2LO / client_credentials）、`OAuthAuthorizationFlow.USER_FEDERATION`（`user_federation`，即 3LO / authorization_code）。 |
| scope | String | 否 | OAuth 协议中的 scope，多个以空格分隔。<br>*   通过 `FetchOAuthAuthenticationOptions` 传入。 |
| forceAuthentication | Boolean | 否 | 是否强制重新授权，忽略已有有效令牌。默认 `false`。<br>*   通过 `FetchOAuthAuthenticationOptions` 传入。 |
| customParameters | Map<String, String> | 否 | 自定义参数键值对，会附加到 OAuth 授权 URL 的 query 参数中。<br>*   例如 Google 的 `access_type=offline`、`prompt=consent`。<br>*   通过 `FetchOAuthAuthenticationOptions` 传入。 |

响应：`OAuthAuthenticationTokenResponse`

> `oauthAccessTokenContent` 与 `oauthAuthorizationSession` **互斥**，不会同时存在。可通过 `hasOAuthAccessTokenContent()` 与 `hasOAuthAuthorizationSession()` 判断当前场景。

| **参数名** | **类型** | **是否一定返回** | **描述** |
| --- | --- | --- | --- |
| instanceId | String | 否 | IDaaS 实例 ID。 |
| authenticationTokenId | String | 否 | 认证令牌 ID。 |
| credentialProviderId | String | 否 | 凭据提供商 ID。 |
| authenticationTokenType | String | 否 | 认证令牌类型，值为 `oauth_access_token`。 |
| revoked | Boolean | 否 | 认证令牌是否已被吊销。 |
| creatorType / creatorId | String | 否 | 认证令牌的创建者类型 / ID。 |
| consumerType / consumerId | String | 否 | 认证令牌的使用者类型 / ID。 |
| createTime / updateTime / expirationTime | Long | 否 | 创建 / 更新 / 过期时间，毫秒级 Unix 时间戳。 |
| oauthAccessTokenContent | Object | 否 | **场景一：令牌已可用**。 |
| └ accessTokenValue | String | 是 | access_token 值。<br>*   注意：包含敏感信息。 |
| └ tokenType | String | 否 | token_type，通常为 `Bearer`。 |
| └ scope | String | 否 | 授权范围。 |
| oauthAuthorizationSession | Object | 否 | **场景二：需要用户授权**（仅 3LO）。 |
| └ sessionId | String | 是 | 授权会话 ID。 |
| └ sessionUri | String | 是 | 授权会话 URI，用于后续查询会话状态。 |
| └ authorizationUrl | String | 是 | 引导用户授权的 URL，需交给终端用户在浏览器中打开。 |
| └ sessionStatus | String | 是 | 授权会话状态，此时为 `pending`。 |

### getOAuthAuthorizationSession

作用：查询 OAuth 授权会话的当前状态，用于 3LO 原子模式下自行编排轮询。

> **注意**：该接口要求 Bearer Token 为用户身份（user-auth）的 Access Token。

请求入参：

| **参数名** | **类型** | **是否必填** | **描述** |
| --- | --- | --- | --- |
| sessionUri | String | 是 | 授权会话 URI，来自 `fetchOAuthAuthenticationTokenV2` 返回的 `oauthAuthorizationSession.sessionUri`。 |

响应：`OAuthAuthorizationSessionResponse`

| **参数名** | **类型** | **是否一定返回** | **描述** |
| --- | --- | --- | --- |
| instanceId | String | 是 | IDaaS 实例 ID。 |
| sessionId | String | 是 | 授权会话 ID。 |
| sessionUri | String | 是 | 授权会话 URI。 |
| sessionStatus | String | 是 | 会话状态。<br>*   枚举值：`pending`（等待用户授权）、`callback_received`（已接收授权码，正在换取令牌）、`completed`（授权完成）、`failed`（授权失败）、`expired`（会话已过期）。<br>*   对应常量：`PamClientConstants.SESSION_STATUS_*`。 |
| credentialProviderIdentifier | String | 是 | 凭据提供商的业务标识。 |
| consumerType / consumerId | String | 是 | 使用者类型 / ID。 |
| creatorType / creatorId | String | 是 | 创建者类型 / ID。 |
| authorizationUrl | String | 否 | 授权 URL，`sessionStatus=pending` 时返回。 |
| expirationTime | Long | 是 | 会话过期时间，毫秒级 Unix 时间戳。 |
| authenticationTokenId | String | 否 | 关联的认证令牌 ID，`sessionStatus=completed` 时返回。 |
| errorCode | String | 否 | 错误码，`sessionStatus=failed` 时返回。 |
| errorDescription | String | 否 | 错误描述，`sessionStatus=failed` 时返回。 |

### pollOAuthAuthenticationToken

作用：3LO 端到端方法，内部自动完成「发起授权 → 回调通知授权 URL → 轮询等待 → 获取令牌」全流程，适合 Agent / CLI 等无复杂 UI 交互的场景。
> **注意**：该接口要求 Bearer Token 为用户身份（user-auth）的 Access Token。
> **阻塞特性**：该方法为同步阻塞方法，轮询期间会阻塞当前线程（最长 180 秒）。如需非阻塞行为，请改用原子方法自行编排，或在独立线程中调用。

请求入参：

| **参数名** | **类型** | **是否必填** | **描述** |
| --- | --- | --- | --- |
| credentialProviderIdentifier | String | 是 | 凭据提供商的业务标识。 |
| onAuthorizationUrl | Consumer<String> | 是 | 授权 URL 回调。当需要用户授权时，SDK 会以 `authorizationUrl` 为参数调用一次。<br>*   由调用方决定如何将 URL 传递给终端用户（如打印到控制台、返回给前端、打开系统浏览器）。<br>*   回调内部抛出的异常将**原样向上传播**，不被 SDK 包装。 |
| scope | String | 否 | OAuth 协议中的 scope，多个以空格分隔。<br>*   通过 `PollOAuthAuthenticationTokenOptions` 传入。 |
| forceAuthentication | Boolean | 否 | 是否强制重新授权，忽略已有有效令牌。默认 `false`。<br>*   通过 `PollOAuthAuthenticationTokenOptions` 传入。 |
| customParameters | Map<String, String> | 否 | 自定义参数键值对，会附加到 OAuth 授权 URL 的 query 参数中。<br>*   通过 `PollOAuthAuthenticationTokenOptions` 传入。 |
| maxPollingRetries | Integer | 否 | 最大轮询次数，默认 60。<br>*   轮询间隔固定为 3 秒（不可配置）。<br>*   内部存在 180 秒硬性超时上限：即使 `轮询间隔 × 最大次数` 超过 180 秒，也会在 180 秒后停止并抛出超时异常。<br>*   通过 `PollOAuthAuthenticationTokenOptions` 传入。 |

响应：`OAuthAuthenticationTokenResponse`（结构同 `fetchOAuthAuthenticationTokenV2`）

> 成功返回时结果**始终包含** `oauthAccessTokenContent`，**不会包含** `oauthAuthorizationSession`（授权逻辑已在方法内部处理完毕）。

异常：

| **场景** | **异常** | **错误码** |
| --- | --- | --- |
| 授权会话状态为 `failed` | `ClientException` | 透传服务端 `errorCode`（为空时回退 `authorization_failed`） |
| 授权会话状态为 `expired` | `ClientException` | `authorization_session_expired` |
| 轮询超时 / 次数用尽 | `ClientException` | `polling_timeout` |
| 回调函数内部异常 | 原始异常 | 不包装，原样传播 |


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

### 获取 OAuth 认证令牌（2LO，推荐）

使用 `fetchOAuthAuthenticationTokenV2` 并显式指定 `m2m` 流程。

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.pam.IDaaSPamClient;
import com.cloud_idaas.pam.domain.OAuthAuthenticationTokenResponse;
import com.cloud_idaas.pam.domain.OAuthAuthorizationFlow;

public class FetchOAuthAuthenticationTokenV2Sample {

    public static void main(String[] args) {
        // 初始化（自动加载配置文件）
        IDaaSCredentialProviderFactory.init();

        // 创建 PAM Client
        IDaaSPamClient pamClient = new IDaaSPamClient();

        // 获取 OAuth 认证令牌（2LO / M2M）
        OAuthAuthenticationTokenResponse response = pamClient.fetchOAuthAuthenticationTokenV2(
                "your-credential-provider-identifier", OAuthAuthorizationFlow.M2M);
        // 带可选参数
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

### OAuth 3LO 授权（端到端模式，推荐）

`pollOAuthAuthenticationToken` 封装了完整的 3LO 流程：发起授权 → 通过回调通知授权 URL → 轮询等待用户授权 → 获取令牌。适合 Agent / CLI 场景。

> 3LO 的会话接口要求用户身份（user-auth）令牌，因此下面通过**令牌交换**构建 PAM 客户端。

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
        // 初始化（自动加载配置文件）
        IDaaSCredentialProviderFactory.init();

        // 通过令牌交换获取用户身份凭据
        IDaaSTokenExchangeCredentialProvider tokenExchangeProvider = IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider();
        IDaaSCredential credential = tokenExchangeProvider.getCredential("your-subject-token", OAuth2Constants.ACCESS_TOKEN_TYPE, OAuth2Constants.ACCESS_TOKEN_TYPE);
        IDaaSCredentialProvider credentialProvider = StaticIDaaSCredentialProvider.builder()
                .setCredential(credential)
                .build();
        IDaaSPamClient pamClient = IDaaSPamClient.builder()
                .credentialProvider(credentialProvider)
                .build();

        // 端到端获取 OAuth 认证令牌（内部自动轮询等待授权完成）
        OAuthAuthenticationTokenResponse response = pamClient.pollOAuthAuthenticationToken(
                "your-oauth-3lo-credential-provider-identifier",
                // 授权 URL 回调：由调用方决定如何展示给终端用户
                authorizationUrl -> System.out.println("请在浏览器中打开以下 URL 完成授权：\n" + authorizationUrl));
        // 带可选参数
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

### OAuth 3LO 授权（原子模式）

调用方自行编排轮询逻辑，适合需要自定义 UI 交互或轮询策略的场景。

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
        // 初始化（自动加载配置文件）
        IDaaSCredentialProviderFactory.init();

        // 通过令牌交换获取用户身份凭据
        IDaaSTokenExchangeCredentialProvider tokenExchangeProvider = IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider();
        IDaaSCredential credential = tokenExchangeProvider.getCredential("your-subject-token", OAuth2Constants.ACCESS_TOKEN_TYPE, OAuth2Constants.ACCESS_TOKEN_TYPE);
        IDaaSCredentialProvider credentialProvider = StaticIDaaSCredentialProvider.builder()
                .setCredential(credential)
                .build();
        IDaaSPamClient pamClient = IDaaSPamClient.builder()
                .credentialProvider(credentialProvider)
                .build();

        String credentialProviderIdentifier = "your-oauth-3lo-credential-provider-identifier";

        // 1. 发起授权（user_federation 流程）
        OAuthAuthenticationTokenResponse response = pamClient.fetchOAuthAuthenticationTokenV2(
                credentialProviderIdentifier, OAuthAuthorizationFlow.USER_FEDERATION);

        if (response.hasOAuthAccessTokenContent()) {
            // 2. 令牌已可用，直接使用
            System.out.println("Access Token: " + response.getOauthAccessTokenContent().getAccessTokenValue());
        } else {
            // 3. 需要用户授权：展示授权 URL 并轮询会话状态
            OAuthAuthorizationSession session = response.getOauthAuthorizationSession();
            System.out.println("请在浏览器中打开以下 URL 完成授权：\n" + session.getAuthorizationUrl());

            while (true) {
                OAuthAuthorizationSessionResponse sessionResponse = pamClient.getOAuthAuthorizationSession(session.getSessionUri());
                String status = sessionResponse.getSessionStatus();
                System.out.println("授权会话状态: " + status);
                if (PamClientConstants.SESSION_STATUS_COMPLETED.equals(status)) {
                    break;
                }
                if (PamClientConstants.SESSION_STATUS_FAILED.equals(status)
                        || PamClientConstants.SESSION_STATUS_EXPIRED.equals(status)) {
                    throw new RuntimeException("授权未完成: " + status);
                }
                Thread.sleep(PamClientConstants.POLLING_INTERVAL_MILLIS);
            }

            // 4. 授权完成，再次获取令牌
            OAuthAuthenticationTokenResponse finalResponse = pamClient.fetchOAuthAuthenticationTokenV2(
                    credentialProviderIdentifier, OAuthAuthorizationFlow.USER_FEDERATION);
            System.out.println("Access Token: " + finalResponse.getOauthAccessTokenContent().getAccessTokenValue());
        }
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
