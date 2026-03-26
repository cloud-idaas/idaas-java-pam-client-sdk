package com.cloud_idaas.pam;

import com.aliyun.eiam_developerapi20220225.Client;
import com.aliyun.eiam_developerapi20220225.models.*;
import com.aliyun.tea.TeaException;
import com.aliyun.tea.interceptor.RequestInterceptor;
import com.aliyun.teaopenapi.models.Config;
import com.cloud_idaas.core.exception.ClientException;
import com.cloud_idaas.core.exception.ConfigException;
import com.cloud_idaas.core.exception.IDaaSUnexpectedException;
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.core.provider.IDaaSCredentialProvider;
import com.cloud_idaas.pam.domain.*;
import com.cloud_idaas.pam.interceptor.BearerTokenRequestInterceptor;
import com.cloud_idaas.pam.option.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class IDaaSPamClient {

    /**
     * IDaaS Developer API endpoint for developers.
     */
    private final String developerApiEndpoint;

    /**
     * IDaaS instance ID.
     */
    private final String idaasInstanceId;

    private IDaaSCredentialProvider credentialProvider;

    private final transient Client client;

    private final transient RequestInterceptor requestInterceptor;

    private static final Logger LOGGER = LoggerFactory.getLogger(IDaaSPamClient.class);

    public IDaaSPamClient() {
        this(IDaaSCredentialProviderFactory.getDeveloperApiEndpoint(),
                IDaaSCredentialProviderFactory.getIDaasInstanceId(),
                IDaaSCredentialProviderFactory.getIDaaSCredentialProvider(PamClientConstants.SCOPE));
    }

    public IDaaSPamClient(String developerApiEndpoint, String idaasInstanceId, IDaaSCredentialProvider credentialProvider) {
        this.developerApiEndpoint = getDeveloperApiEndpoint(developerApiEndpoint);
        if (this.developerApiEndpoint == null) {
            throw new ConfigException("DeveloperApiEndpoint can not be empty");
        }
        this.idaasInstanceId = idaasInstanceId != null ? idaasInstanceId : IDaaSCredentialProviderFactory.getIDaasInstanceId();
        if (this.idaasInstanceId == null) {
            throw new ConfigException("IDaasInstanceId can not be empty");
        }
        this.credentialProvider = credentialProvider != null ? credentialProvider : IDaaSCredentialProviderFactory.getIDaaSCredentialProvider(
                PamClientConstants.SCOPE);
        if (this.credentialProvider == null) {
            throw new ConfigException("CredentialProvider can not be empty");
        }
        try {
            this.client = new Client(new Config()
                    .setEndpoint(this.developerApiEndpoint));
            this.requestInterceptor = new BearerTokenRequestInterceptor(this.credentialProvider);
            this.client.addRequestInterceptor(this.requestInterceptor);
        } catch (Exception e) {
            LOGGER.error("Error occurred while creating IDaaSPamClient: {}", e.getMessage());
            throw new ConfigException(e.getMessage());
        }
    }

    private String getDeveloperApiEndpoint(String developerApiEndpoint) {
        String realEndpoint = (developerApiEndpoint != null ? developerApiEndpoint : IDaaSCredentialProviderFactory.getDeveloperApiEndpoint());

        if (realEndpoint == null || realEndpoint.trim().isEmpty()) {
            return realEndpoint;
        }

        if (realEndpoint.startsWith("https://")) {
            realEndpoint = realEndpoint.substring(8);
        } else if (realEndpoint.startsWith("http://")) {
            realEndpoint = realEndpoint.substring(7);
        }

        return realEndpoint;
    }

    public String getApiKey(String credentialIdentifier) {
        try {
            ObtainCredentialRequest request = new ObtainCredentialRequest()
                    .setCredentialIdentifier(credentialIdentifier);
            ObtainCredentialResponse response = this.client.obtainCredential(this.idaasInstanceId, request);
            if (response.getStatusCode() == PamClientConstants.STATUS_CODE_200) {
                ObtainCredentialResponseBody responseBody = response.getBody();
                ObtainCredentialResponseBody.ObtainCredentialResponseBodyCredentialContent credentialContent = responseBody.getCredentialContent();
                if (credentialContent != null) {
                    ObtainCredentialResponseBody.ObtainCredentialResponseBodyCredentialContentApiKeyContent apiKeyContent
                            = credentialContent.getApiKeyContent();
                    if (apiKeyContent != null) {
                        return apiKeyContent.getApiKey();
                    } else {
                        LOGGER.info("The credential retrieval operation using the CredentialIdentifier was successful; however, "
                                + "the ApiContent field returned null, suggesting that an incorrect API method may have been invoked.");
                    }
                }
                return null;
            }
            throw new IDaaSUnexpectedException("Failed to obtain credential, status code: " + response.getStatusCode());
        } catch (TeaException e){
            throw handleTeaException(e);
        } catch (IDaaSUnexpectedException e) {
            throw e;
        } catch (Exception e){
            LOGGER.error("Error occurred while obtaining credential: {}", e.getMessage());
            throw new IDaaSUnexpectedException(e.getMessage(), e);
        }
    }

    public String fetchOAuthAuthenticationToken(String credentialIdentifier){
        return fetchOAuthAuthenticationToken(credentialIdentifier, null);
    }

    public String fetchOAuthAuthenticationToken(String credentialIdentifier, FetchOAuthAuthenticationOptions options) {
        try {
            FetchOAuthAuthenticationTokenRequest request = new FetchOAuthAuthenticationTokenRequest()
                    .setCredentialProviderIdentifier(credentialIdentifier);
            if (options != null){
                if (options.getScope() != null){
                    request.setScope(options.getScope());
                }
            }
            FetchOAuthAuthenticationTokenResponse response = this.client.fetchOAuthAuthenticationToken(this.idaasInstanceId, request);
            if (response.getStatusCode() == PamClientConstants.STATUS_CODE_200) {
                FetchOAuthAuthenticationTokenResponseBody responseBody = response.getBody();
                FetchOAuthAuthenticationTokenResponseBody.FetchOAuthAuthenticationTokenResponseBodyOauthAccessTokenContent oauthAccessTokenContent
                        = responseBody.getOauthAccessTokenContent();
                return oauthAccessTokenContent.getAccessTokenValue();
            }
            throw new IDaaSUnexpectedException("Failed to fetch OAuth authentication token, status code: " + response.getStatusCode());
        } catch (TeaException e){
            throw handleTeaException(e);
        } catch (IDaaSUnexpectedException e) {
            throw e;
        } catch (Exception e){
            LOGGER.error("Error occurred while obtaining credential: {}", e.getMessage());
            throw new IDaaSUnexpectedException(e.getMessage(), e);
        }
    }

    public JwtContent generateJwtAuthenticationToken(String credentialProviderIdentifier, String subject, List<String> audiences) {
        return generateJwtAuthenticationToken(credentialProviderIdentifier, subject, audiences, null);
    }

    public JwtContent generateJwtAuthenticationToken(String credentialProviderIdentifier, String subject, List<String> audiences,
                                                     GenerateJwtAuthenticationOptions options) {
        try {
            GenerateJwtAuthenticationTokenRequest request = new GenerateJwtAuthenticationTokenRequest()
                    .setCredentialProviderIdentifier(credentialProviderIdentifier)
                    .setSubject(subject)
                    .setAudiences(new ArrayList<>(audiences));
            if (options != null){
                if (options.getIssuer() != null){
                    request.setIssuer(options.getIssuer());
                }
                if (options.getCustomClaims() != null){
                    request.setCustomClaims(options.getCustomClaims());
                }
                if (options.getExpiration() != null){
                    request.setExpiration(options.getExpiration());
                }
                if (options.getIncludeDerivedShortToken() != null){
                    request.setIncludeDerivedShortToken(options.getIncludeDerivedShortToken());
                }
            }
            GenerateJwtAuthenticationTokenResponse response = this.client.generateJwtAuthenticationToken(this.idaasInstanceId, request);
            if (response.getStatusCode() == PamClientConstants.STATUS_CODE_200) {
                GenerateJwtAuthenticationTokenResponseBody responseBody = response.getBody();
                GenerateJwtAuthenticationTokenResponseBody.GenerateJwtAuthenticationTokenResponseBodyJwtContent jwtAuthenticationTokenContent
                        = responseBody.getJwtContent();
                return new JwtContent(jwtAuthenticationTokenContent.getJwtValue(), jwtAuthenticationTokenContent.getDerivedShortToken());
            }
            throw new IDaaSUnexpectedException("Failed to generate JWT authentication token, status code: " + response.getStatusCode());
        } catch (TeaException e){
            throw handleTeaException(e);
        } catch (IDaaSUnexpectedException e) {
            throw e;
        } catch (Exception e){
            LOGGER.error("Error occurred while generating JWT authentication token: {}", e.getMessage());
            throw new IDaaSUnexpectedException(e.getMessage(), e);
        }
    }

    public JwtContent obtainJwtAuthenticationTokenByDerivedShortToken(String derivedShortToken){
        try {
            ObtainJwtAuthenticationTokenByDerivedShortTokenRequest request = new ObtainJwtAuthenticationTokenByDerivedShortTokenRequest()
                    .setDerivedShortToken(derivedShortToken);
            ObtainJwtAuthenticationTokenByDerivedShortTokenResponse response =
                    this.client.obtainJwtAuthenticationTokenByDerivedShortToken(this.idaasInstanceId, request);
            if (response.getStatusCode() == PamClientConstants.STATUS_CODE_200) {
                ObtainJwtAuthenticationTokenByDerivedShortTokenResponseBody responseBody = response.getBody();
                ObtainJwtAuthenticationTokenByDerivedShortTokenResponseBody.ObtainJwtAuthenticationTokenByDerivedShortTokenResponseBodyJwtContent jwtContent =
                        responseBody.getJwtContent();
                return new JwtContent(jwtContent.getJwtValue(), jwtContent.getDerivedShortToken());
            }
            throw new IDaaSUnexpectedException("Failed to obtain JWT authentication token by derived short token, status code: " + response.getStatusCode());
        } catch (TeaException e){
            throw handleTeaException(e);
        } catch (IDaaSUnexpectedException e) {
            throw e;
        } catch (Exception e){
            LOGGER.error("Error occurred while obtaining JWT authentication token by derived short token: {}", e.getMessage());
            throw new IDaaSUnexpectedException(e.getMessage(), e);
        }
    }

    public NextTokenPageableResponse<AuthenticationToken> listAuthenticationTokens(String consumerId, String credentialProviderIdentifier){
        return listAuthenticationTokens(consumerId, credentialProviderIdentifier, null);
    }

    public NextTokenPageableResponse<AuthenticationToken> listAuthenticationTokens(String consumerId, String credentialProviderIdentifier,
                                                                                   ListAuthenticationTokensOptions options){
        try {
            ListAuthenticationTokensRequest request = new ListAuthenticationTokensRequest()
                    .setConsumerId(consumerId)
                    .setCredentialProviderIdentifier(credentialProviderIdentifier);
            if (options != null){
                if (options.getNextToken() != null){
                    request.setNextToken(options.getNextToken());
                }
                if (options.getMaxResults() != null){
                    request.setMaxResults(options.getMaxResults());
                }
                if (options.getRevoked() != null){
                    request.setRevoked(options.getRevoked());
                }
                if (options.getExpired() != null){
                    request.setExpired(options.getExpired());
                }
            }
            ListAuthenticationTokensResponse response = this.client.listAuthenticationTokens(this.idaasInstanceId, request);
            if (response.getStatusCode() == PamClientConstants.STATUS_CODE_200) {
                ListAuthenticationTokensResponseBody responseBody = response.getBody();
                    NextTokenPageableResponse<AuthenticationToken> listAuthenticationTokens = new NextTokenPageableResponse<>();
                    listAuthenticationTokens.setTotalCount(responseBody.getTotalCount());
                    listAuthenticationTokens.setNextToken(responseBody.getNextToken());
                    listAuthenticationTokens.setMaxResults(responseBody.getMaxResults());
                    List<AuthenticationToken> authenticationTokenList = convertAuthenticationTokenList(responseBody.getEntities());
                    listAuthenticationTokens.setEntities(authenticationTokenList);
                    return listAuthenticationTokens;
            }
            throw new IDaaSUnexpectedException("Failed to list authentication tokens, status code: " + response.getStatusCode());
        } catch (TeaException e){
            throw handleTeaException(e);
        } catch (IDaaSUnexpectedException e) {
            throw e;
        } catch (Exception e){
            LOGGER.error("Error occurred while listing authentication tokens: {}", e.getMessage());
            throw new IDaaSUnexpectedException(e.getMessage(), e);
        }
    }

    public Boolean validateAuthenticationToken(String token){
        return validateAuthenticationToken(token, null);
    }

    public Boolean validateAuthenticationToken(String token, ValidateAuthenticationTokenOptions options){
        try {
            ValidateAuthenticationTokenRequest request = new ValidateAuthenticationTokenRequest()
                    .setToken(token);
            if (options != null){
                if (options.getTokenTypeHint() != null){
                    request.setTokenTypeHint(options.getTokenTypeHint());
                }
            }
            ValidateAuthenticationTokenResponse response = this.client.validateAuthenticationToken(this.idaasInstanceId, request);
            if (response.getStatusCode() == PamClientConstants.STATUS_CODE_200) {
                ValidateAuthenticationTokenResponseBody responseBody = response.getBody();
                return responseBody.getActive();
            }
            throw new IDaaSUnexpectedException("Failed to validate authentication token, status code: " + response.getStatusCode());
        } catch (TeaException e){
            throw handleTeaException(e);
        } catch (IDaaSUnexpectedException e) {
            throw e;
        } catch (Exception e){
            LOGGER.error("Error occurred while validating authentication token: {}", e.getMessage());
            throw new IDaaSUnexpectedException(e.getMessage(), e);
        }
    }

    public void reinstateAuthenticationToken(String token){
        reinstateAuthenticationToken(token, null);
    }

    public void reinstateAuthenticationToken(String token, ReinstateAuthenticationTokenOptions options){
        try {
            ReinstateAuthenticationTokenRequest request = new ReinstateAuthenticationTokenRequest()
                    .setToken(token);
            if (options != null){
                if (options.getTokenTypeHint() != null){
                    request.setTokenTypeHint(options.getTokenTypeHint());
                }
            }
            ReinstateAuthenticationTokenResponse response = this.client.reinstateAuthenticationToken(this.idaasInstanceId, request);
            if (response.getStatusCode() == PamClientConstants.STATUS_CODE_200) {
                return;
            }
            throw new IDaaSUnexpectedException("Failed to reinstate authentication token, status code: " + response.getStatusCode());
        } catch (TeaException e){
            throw handleTeaException(e);
        } catch (IDaaSUnexpectedException e) {
            throw e;
        } catch (Exception e){
            LOGGER.error("Error occurred while reinstating authentication token: {}", e.getMessage());
            throw new IDaaSUnexpectedException(e.getMessage(), e);
        }
    }

    public void reinstateAuthenticationTokenByConsumer(String consumerId, String credentialProviderIdentifier){
        try {
            ReinstateAuthenticationTokenByConsumerRequest request = new ReinstateAuthenticationTokenByConsumerRequest()
                    .setConsumerId(consumerId)
                    .setCredentialProviderIdentifier(credentialProviderIdentifier);
            ReinstateAuthenticationTokenByConsumerResponse response = this.client.reinstateAuthenticationTokenByConsumer(this.idaasInstanceId, request);
            if (response.getStatusCode() == PamClientConstants.STATUS_CODE_200) {
                return;
            }
            throw new IDaaSUnexpectedException("Failed to reinstate authentication token by consumer, status code: " + response.getStatusCode());
        } catch (TeaException e){
            throw handleTeaException(e);
        } catch (IDaaSUnexpectedException e) {
            throw e;
        } catch (Exception e){
            LOGGER.error("Error occurred while reinstating authentication token by consumer: {}", e.getMessage());
            throw new IDaaSUnexpectedException(e.getMessage(), e);
        }
    }

    public void revokeAuthenticationToken(String token){
        revokeAuthenticationToken(token, null);
    }

    public void revokeAuthenticationToken(String token, RevokeAuthenticationTokenOptions options){
        try {
            RevokeAuthenticationTokenRequest request = new RevokeAuthenticationTokenRequest()
                    .setToken(token);
            if (options != null){
                if (options.getTokenTypeHint() != null){
                    request.setTokenTypeHint(options.getTokenTypeHint());
                }
            }
            RevokeAuthenticationTokenResponse response = this.client.revokeAuthenticationToken(this.idaasInstanceId, request);
            if (response.getStatusCode() == PamClientConstants.STATUS_CODE_200) {
                return;
            }
            throw new IDaaSUnexpectedException("Failed to revoke authentication token, status code: " + response.getStatusCode());
        } catch (TeaException e){
            throw handleTeaException(e);
        } catch (IDaaSUnexpectedException e) {
            throw e;
        } catch (Exception e){
            LOGGER.error("Error occurred while revoking authentication token: {}", e.getMessage());
            throw new IDaaSUnexpectedException(e.getMessage(), e);
        }
    }

    public void revokeAuthenticationTokenByConsumer(String consumerId, String credentialProviderIdentifier){
        try {
            RevokeAuthenticationTokenByConsumerRequest request = new RevokeAuthenticationTokenByConsumerRequest()
                    .setConsumerId(consumerId)
                    .setCredentialProviderIdentifier(credentialProviderIdentifier);
            RevokeAuthenticationTokenByConsumerResponse response = this.client.revokeAuthenticationTokenByConsumer(this.idaasInstanceId, request);
            if (response.getStatusCode() == PamClientConstants.STATUS_CODE_200) {
                return;
            }
            throw new IDaaSUnexpectedException("Failed to revoke authentication token by consumer, status code: " + response.getStatusCode());
        } catch (TeaException e){
            throw handleTeaException(e);
        } catch (IDaaSUnexpectedException e) {
            throw e;
        } catch (Exception e){
            LOGGER.error("Error occurred while revoking authentication token by consumer: {}", e.getMessage());
            throw new IDaaSUnexpectedException(e.getMessage(), e);
        }
    }

    private RuntimeException handleTeaException(TeaException e) {
        int statusCode = e.getStatusCode();
        if (statusCode >= 400 && statusCode < 500){
            String code = e.getCode();
            String message = e.getMessage();
            LOGGER.error("Client Error: {}", code);
            LOGGER.error("Client Error Message: {}", message);
            return new ClientException(code, message, e);
        } else if (statusCode >= 500) {
            LOGGER.error("Server Error Message: {}", e.getMessage());
            return e;
        } else {
            LOGGER.error("Error occurred while obtaining credential: {}", e.getMessage());
            return e;
        }
    }

    private List<AuthenticationToken> convertAuthenticationTokenList(List<ListAuthenticationTokensResponseBody.ListAuthenticationTokensResponseBodyEntities> entities) {
        List<AuthenticationToken> authenticationTokenList = new ArrayList<>();
        if (entities == null) {
            return authenticationTokenList;
        }
        for (ListAuthenticationTokensResponseBody.ListAuthenticationTokensResponseBodyEntities entity : entities) {
            AuthenticationToken authenticationToken = new AuthenticationToken();
            authenticationToken.setInstanceId(entity.getInstanceId());
            authenticationToken.setAuthenticationTokenId(entity.getAuthenticationTokenId());
            authenticationToken.setCredentialProviderId(entity.getCredentialProviderId());
            authenticationToken.setCreateTime(entity.getCreateTime());
            authenticationToken.setUpdateTime(entity.getUpdateTime());
            authenticationToken.setAuthenticationTokenType(entity.getAuthenticationTokenType());
            authenticationToken.setRevoked(entity.getRevoked());
            authenticationToken.setCreatorType(entity.getCreatorType());
            authenticationToken.setCreatorId(entity.getCreatorId());
            authenticationToken.setConsumerType(entity.getConsumerType());
            authenticationToken.setConsumerId(entity.getConsumerId());
            authenticationToken.setExpirationTime(entity.getExpirationTime());
            authenticationTokenList.add(authenticationToken);
        }
        return authenticationTokenList;
    }

    public static IDaaSPamClientBuilder builder() {
        return new IDaaSPamClientBuilder();
    }

    public static final class IDaaSPamClientBuilder {
        private String developerApiEndpoint;
        private String idaasInstanceId;
        private IDaaSCredentialProvider credentialProvider;

        private IDaaSPamClientBuilder() {
        }

        public IDaaSPamClientBuilder developerApiEndpoint(String developerApiEndpoint) {
            this.developerApiEndpoint = developerApiEndpoint;
            return this;
        }

        public IDaaSPamClientBuilder idaasInstanceId(String idaasInstanceId) {
            this.idaasInstanceId = idaasInstanceId;
            return this;
        }

        public IDaaSPamClientBuilder credentialProvider(IDaaSCredentialProvider credentialProvider) {
            this.credentialProvider = credentialProvider;
            return this;
        }

        public IDaaSPamClient build() {
            return new IDaaSPamClient(this.developerApiEndpoint, this.idaasInstanceId, this.credentialProvider);
        }
    }

}
