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
import java.util.function.Consumer;

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

    /**
     * Fetches an OAuth authentication token (2LO / client_credentials flow).
     *
     * @deprecated Use {@link #fetchOAuthAuthenticationTokenV2(String, String)} instead,
     * which returns a rich {@link OAuthAuthenticationTokenResponse} and covers both the
     * 2LO and 3LO flows. This method is kept for backward compatibility and returns only
     * the access token value string.
     */
    @Deprecated
    public String fetchOAuthAuthenticationToken(String credentialIdentifier){
        return fetchOAuthAuthenticationToken(credentialIdentifier, null);
    }

    /**
     * Fetches an OAuth authentication token (2LO / client_credentials flow).
     *
     * @deprecated Use {@link #fetchOAuthAuthenticationTokenV2(String, String, FetchOAuthAuthenticationOptions)}
     * instead, which returns a rich {@link OAuthAuthenticationTokenResponse} and covers both
     * the 2LO and 3LO flows. This method is kept for backward compatibility and returns only
     * the access token value string.
     */
    @Deprecated
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

    /**
     * Fetches an OAuth authentication token, covering both the 2LO (m2m) and 3LO
     * (user_federation) flows. Returns a rich {@link OAuthAuthenticationTokenResponse}
     * that contains either an available access token or an authorization session that
     * requires user interaction.
     *
     * @param credentialProviderIdentifier the credential provider identifier
     * @param authorizationFlow             the authorization flow, one of
     *                                      {@link OAuthAuthorizationFlow#M2M} or
     *                                      {@link OAuthAuthorizationFlow#USER_FEDERATION};
     *                                      used only for SDK-side validation and NOT sent to the server
     * @return the OAuth authentication token response
     */
    public OAuthAuthenticationTokenResponse fetchOAuthAuthenticationTokenV2(String credentialProviderIdentifier, String authorizationFlow) {
        return fetchOAuthAuthenticationTokenV2(credentialProviderIdentifier, authorizationFlow, null);
    }

    /**
     * Fetches an OAuth authentication token with optional parameters, covering both the
     * 2LO (m2m) and 3LO (user_federation) flows.
     *
     * @param credentialProviderIdentifier the credential provider identifier
     * @param authorizationFlow             the authorization flow, one of
     *                                      {@link OAuthAuthorizationFlow#M2M} or
     *                                      {@link OAuthAuthorizationFlow#USER_FEDERATION}
     * @param options                       optional parameters (scope / forceAuthentication / customParameters)
     * @return the OAuth authentication token response
     */
    public OAuthAuthenticationTokenResponse fetchOAuthAuthenticationTokenV2(String credentialProviderIdentifier, String authorizationFlow,
                                                                            FetchOAuthAuthenticationOptions options) {
        if (!OAuthAuthorizationFlow.M2M.equals(authorizationFlow)
                && !OAuthAuthorizationFlow.USER_FEDERATION.equals(authorizationFlow)) {
            throw new ClientException("invalid_authorization_flow",
                    "authorizationFlow must be either 'm2m' or 'user_federation'");
        }
        try {
            FetchOAuthAuthenticationTokenRequest request = new FetchOAuthAuthenticationTokenRequest()
                    .setCredentialProviderIdentifier(credentialProviderIdentifier);
            if (options != null) {
                if (options.getScope() != null) {
                    request.setScope(options.getScope());
                }
                if (options.getForceAuthentication() != null) {
                    request.setForceAuthentication(options.getForceAuthentication());
                }
                if (options.getCustomParameters() != null) {
                    request.setCustomParameters(options.getCustomParameters());
                }
            }
            FetchOAuthAuthenticationTokenResponse response = this.client.fetchOAuthAuthenticationToken(this.idaasInstanceId, request);
            if (response.getStatusCode() == PamClientConstants.STATUS_CODE_200) {
                OAuthAuthenticationTokenResponse result = convertOAuthAuthenticationTokenResponse(response.getBody());
                if (OAuthAuthorizationFlow.M2M.equals(authorizationFlow) && result.hasOAuthAuthorizationSession()) {
                    throw new ClientException("authorization_flow_mismatch",
                            "authorizationFlow 'm2m' does not match the CredentialProvider configuration, "
                                    + "which requires user authorization (user_federation)");
                }
                LOGGER.info("Fetched OAuth authentication token, hasAccessToken={}, hasAuthorizationSession={}",
                        result.hasOAuthAccessTokenContent(), result.hasOAuthAuthorizationSession());
                return result;
            }
            throw new IDaaSUnexpectedException("Failed to fetch OAuth authentication token, status code: " + response.getStatusCode());
        } catch (TeaException e) {
            throw handleTeaException(e);
        } catch (ClientException | IDaaSUnexpectedException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching OAuth authentication token: {}", e.getMessage());
            throw new IDaaSUnexpectedException(e.getMessage(), e);
        }
    }

    /**
     * Queries an OAuth authorization session by its session URI.
     *
     * @param sessionUri the authorization session URI
     * @return the authorization session response
     */
    public OAuthAuthorizationSessionResponse getOAuthAuthorizationSession(String sessionUri) {
        try {
            GetOAuthAuthorizationSessionRequest request = new GetOAuthAuthorizationSessionRequest()
                    .setSessionUri(sessionUri);
            GetOAuthAuthorizationSessionResponse response = this.client.getOAuthAuthorizationSession(this.idaasInstanceId, request);
            if (response.getStatusCode() == PamClientConstants.STATUS_CODE_200) {
                OAuthAuthorizationSessionResponse result = convertOAuthAuthorizationSessionResponse(response.getBody());
                LOGGER.info("Queried OAuth authorization session, sessionStatus={}", result.getSessionStatus());
                return result;
            }
            throw new IDaaSUnexpectedException("Failed to get OAuth authorization session, status code: " + response.getStatusCode());
        } catch (TeaException e) {
            throw handleTeaException(e);
        } catch (IDaaSUnexpectedException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while getting OAuth authorization session: {}", e.getMessage());
            throw new IDaaSUnexpectedException(e.getMessage(), e);
        }
    }

    /**
     * End-to-end 3LO helper: initiates authorization, notifies the caller of the
     * authorization URL, polls the session until completion, and finally returns a
     * response containing an available access token. This method blocks the calling
     * thread for up to 180 seconds.
     *
     * @param credentialProviderIdentifier the credential provider identifier
     * @param onAuthorizationUrl           callback invoked with the authorization URL when user
     *                                     authorization is required; exceptions thrown inside the
     *                                     callback are propagated as-is
     * @return a response that always contains {@code oauthAccessTokenContent}
     */
    public OAuthAuthenticationTokenResponse pollOAuthAuthenticationToken(String credentialProviderIdentifier, Consumer<String> onAuthorizationUrl) {
        return pollOAuthAuthenticationToken(credentialProviderIdentifier, onAuthorizationUrl, null);
    }

    /**
     * End-to-end 3LO helper with optional polling parameters.
     *
     * @param credentialProviderIdentifier the credential provider identifier
     * @param onAuthorizationUrl           callback invoked with the authorization URL when user
     *                                     authorization is required
     * @param options                      optional parameters (scope / forceAuthentication /
     *                                     customParameters / maxPollingRetries)
     * @return a response that always contains {@code oauthAccessTokenContent}
     */
    public OAuthAuthenticationTokenResponse pollOAuthAuthenticationToken(String credentialProviderIdentifier, Consumer<String> onAuthorizationUrl,
                                                                         PollOAuthAuthenticationTokenOptions options) {
        FetchOAuthAuthenticationOptions.Builder fetchOptionsBuilder = FetchOAuthAuthenticationOptions.builder();
        int maxPollingRetries = PamClientConstants.DEFAULT_MAX_POLLING_RETRIES;
        if (options != null) {
            if (options.getScope() != null) {
                fetchOptionsBuilder.scope(options.getScope());
            }
            if (options.getForceAuthentication() != null) {
                fetchOptionsBuilder.forceAuthentication(options.getForceAuthentication());
            }
            if (options.getCustomParameters() != null) {
                fetchOptionsBuilder.customParameters(options.getCustomParameters());
            }
            if (options.getMaxPollingRetries() != null) {
                maxPollingRetries = options.getMaxPollingRetries();
            }
        }

        OAuthAuthenticationTokenResponse initial = fetchOAuthAuthenticationTokenV2(
                credentialProviderIdentifier, OAuthAuthorizationFlow.USER_FEDERATION, fetchOptionsBuilder.build());
        if (initial.hasOAuthAccessTokenContent()) {
            return initial;
        }

        OAuthAuthorizationSession session = initial.getOauthAuthorizationSession();
        if (session == null || session.getSessionUri() == null) {
            throw new IDaaSUnexpectedException("Authorization session is missing from the fetch response");
        }
        onAuthorizationUrl.accept(session.getAuthorizationUrl());

        String sessionUri = session.getSessionUri();
        long deadline = currentTimeMillis() + PamClientConstants.MAX_POLLING_DURATION_MILLIS;
        for (int attempt = 0; attempt < maxPollingRetries; attempt++) {
            if (currentTimeMillis() + PamClientConstants.POLLING_INTERVAL_MILLIS > deadline) {
                throw new ClientException("polling_timeout",
                        "Polling for OAuth authorization timed out after "
                                + (PamClientConstants.MAX_POLLING_DURATION_MILLIS / 1000) + " seconds");
            }
            try {
                sleepBetweenPolls(PamClientConstants.POLLING_INTERVAL_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IDaaSUnexpectedException("Polling for OAuth authorization was interrupted", e);
            }
            OAuthAuthorizationSessionResponse sessionResponse = getOAuthAuthorizationSession(sessionUri);
            String status = sessionResponse.getSessionStatus();
            if (PamClientConstants.SESSION_STATUS_COMPLETED.equals(status)) {
                FetchOAuthAuthenticationOptions fetchOptions = new FetchOAuthAuthenticationOptions();
                if (options != null) {
                    if (options.getScope() != null) {
                        fetchOptions.setScope(options.getScope());
                    }
                    if (options.getCustomParameters() != null) {
                        fetchOptions.setCustomParameters(options.getCustomParameters());
                    }
                }
                OAuthAuthenticationTokenResponse finalResponse = fetchOAuthAuthenticationTokenV2(
                        credentialProviderIdentifier, OAuthAuthorizationFlow.USER_FEDERATION, fetchOptions);
                if (!finalResponse.hasOAuthAccessTokenContent()) {
                    throw new IDaaSUnexpectedException("Authorization session completed but no access token was returned");
                }
                return finalResponse;
            } else if (PamClientConstants.SESSION_STATUS_FAILED.equals(status)) {
                throw new ClientException(
                        sessionResponse.getErrorCode() != null ? sessionResponse.getErrorCode() : "authorization_failed",
                        sessionResponse.getErrorDescription() != null ? sessionResponse.getErrorDescription()
                                : "OAuth authorization failed");
            } else if (PamClientConstants.SESSION_STATUS_EXPIRED.equals(status)) {
                throw new ClientException("authorization_session_expired", "The OAuth authorization session has expired");
            }
            // pending / callback_received -> continue polling
        }
        throw new ClientException("polling_timeout",
                "Polling for OAuth authorization timed out after " + maxPollingRetries + " retries");
    }

    private OAuthAuthenticationTokenResponse convertOAuthAuthenticationTokenResponse(FetchOAuthAuthenticationTokenResponseBody body) {
        OAuthAuthenticationTokenResponse result = new OAuthAuthenticationTokenResponse();
        result.setAuthenticationTokenId(body.getAuthenticationTokenId());
        result.setAuthenticationTokenType(body.getAuthenticationTokenType());
        result.setConsumerId(body.getConsumerId());
        result.setConsumerType(body.getConsumerType());
        result.setCreateTime(body.getCreateTime());
        result.setCreatorId(body.getCreatorId());
        result.setCreatorType(body.getCreatorType());
        result.setCredentialProviderId(body.getCredentialProviderId());
        result.setExpirationTime(body.getExpirationTime());
        result.setInstanceId(body.getInstanceId());
        result.setRevoked(body.getRevoked());
        result.setUpdateTime(body.getUpdateTime());
        FetchOAuthAuthenticationTokenResponseBody.FetchOAuthAuthenticationTokenResponseBodyOauthAccessTokenContent accessTokenContent
                = body.getOauthAccessTokenContent();
        if (accessTokenContent != null) {
            OAuthAccessTokenContent content = new OAuthAccessTokenContent();
            content.setAccessTokenValue(accessTokenContent.getAccessTokenValue());
            content.setTokenType(accessTokenContent.getTokenType());
            content.setScope(accessTokenContent.getScope());
            result.setOauthAccessTokenContent(content);
        }
        FetchOAuthAuthenticationTokenResponseBody.FetchOAuthAuthenticationTokenResponseBodyOauthAuthorizationSession authorizationSession
                = body.getOauthAuthorizationSession();
        if (authorizationSession != null) {
            OAuthAuthorizationSession session = new OAuthAuthorizationSession();
            session.setSessionId(authorizationSession.getSessionId());
            session.setSessionUri(authorizationSession.getSessionUri());
            session.setAuthorizationUrl(authorizationSession.getAuthorizationUrl());
            session.setSessionStatus(authorizationSession.getSessionStatus());
            result.setOauthAuthorizationSession(session);
        }
        return result;
    }

    private OAuthAuthorizationSessionResponse convertOAuthAuthorizationSessionResponse(GetOAuthAuthorizationSessionResponseBody body) {
        OAuthAuthorizationSessionResponse result = new OAuthAuthorizationSessionResponse();
        result.setInstanceId(body.getInstanceId());
        result.setSessionId(body.getSessionId());
        result.setSessionUri(body.getSessionUri());
        result.setSessionStatus(body.getSessionStatus());
        result.setCredentialProviderIdentifier(body.getCredentialProviderIdentifier());
        result.setConsumerType(body.getConsumerType());
        result.setConsumerId(body.getConsumerId());
        result.setCreatorType(body.getCreatorType());
        result.setCreatorId(body.getCreatorId());
        result.setAuthorizationUrl(body.getAuthorizationUrl());
        result.setExpirationTime(body.getExpirationTime());
        result.setAuthenticationTokenId(body.getAuthenticationTokenId());
        result.setErrorCode(body.getErrorCode());
        result.setErrorDescription(body.getErrorDescription());
        return result;
    }

    public JwtTokenResponse generateJwtAuthenticationToken(String credentialProviderIdentifier, String subject, List<String> audiences) {
        return generateJwtAuthenticationToken(credentialProviderIdentifier, subject, audiences, null);
    }

    public JwtTokenResponse generateJwtAuthenticationToken(String credentialProviderIdentifier, String subject, List<String> audiences,
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
                JwtContent jwtContent = new JwtContent(jwtAuthenticationTokenContent.getJwtValue(), jwtAuthenticationTokenContent.getDerivedShortToken());
                JwtTokenResponse jwtTokenResponse = new JwtTokenResponse();
                jwtTokenResponse.setAuthenticationTokenId(responseBody.getAuthenticationTokenId());
                jwtTokenResponse.setConsumerType(responseBody.getConsumerType());
                jwtTokenResponse.setConsumerId(responseBody.getConsumerId());
                jwtTokenResponse.setJwtContent(jwtContent);
                return jwtTokenResponse;
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

    public JwtContent obtainJwtAuthenticationToken(String consumerId, String authenticationTokenId){
        try {
            ObtainJwtAuthenticationTokenRequest request = new ObtainJwtAuthenticationTokenRequest()
                    .setConsumerId(consumerId)
                    .setAuthenticationTokenId(authenticationTokenId);
            ObtainJwtAuthenticationTokenResponse response = this.client.obtainJwtAuthenticationToken(this.idaasInstanceId, request);
            if (response.getStatusCode() == PamClientConstants.STATUS_CODE_200) {
                ObtainJwtAuthenticationTokenResponseBody responseBody = response.getBody();
                ObtainJwtAuthenticationTokenResponseBody.ObtainJwtAuthenticationTokenResponseBodyJwtContent jwtContent =
                        responseBody.getJwtContent();
                return new JwtContent(jwtContent.getJwtValue(), jwtContent.getDerivedShortToken());
            }
            throw new IDaaSUnexpectedException("Failed to obtain JWT authentication token, status code: " + response.getStatusCode());
        } catch (TeaException e){
            throw handleTeaException(e);
        } catch (IDaaSUnexpectedException e) {
            throw e;
        } catch (Exception e){
            LOGGER.error("Error occurred while obtaining JWT authentication token: {}", e.getMessage());
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

    /**
     * Returns the current time in milliseconds. Extracted for testability.
     */
    protected long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * Sleeps for the given number of milliseconds between polls. Extracted for testability.
     */
    protected void sleepBetweenPolls(long millis) throws InterruptedException {
        Thread.sleep(millis);
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
