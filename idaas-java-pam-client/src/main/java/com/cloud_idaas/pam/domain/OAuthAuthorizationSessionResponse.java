package com.cloud_idaas.pam.domain;

import java.io.Serializable;

/**
 * Response model for {@code getOAuthAuthorizationSession}.
 */
public class OAuthAuthorizationSessionResponse implements Serializable {

    private static final long serialVersionUID = 3157842096471538820L;

    /**
     * The IDaaS instance ID.
     */
    private String instanceId;

    /**
     * The authorization session ID.
     */
    private String sessionId;

    /**
     * The authorization session URI.
     */
    private String sessionUri;

    /**
     * The session status: pending / callback_received / completed / failed / expired.
     */
    private String sessionStatus;

    /**
     * The credential provider identifier.
     */
    private String credentialProviderIdentifier;

    /**
     * The consumer type.
     */
    private String consumerType;

    /**
     * The consumer ID.
     */
    private String consumerId;

    /**
     * The creator type.
     */
    private String creatorType;

    /**
     * The creator ID.
     */
    private String creatorId;

    /**
     * The authorization URL. Returned when status is pending.
     */
    private String authorizationUrl;

    /**
     * The session expiration time, as a Unix timestamp in milliseconds.
     */
    private Long expirationTime;

    /**
     * The associated authentication token ID. Returned when status is completed.
     */
    private String authenticationTokenId;

    /**
     * The error code. Returned when status is failed.
     */
    private String errorCode;

    /**
     * The error description. Returned when status is failed.
     */
    private String errorDescription;

    public OAuthAuthorizationSessionResponse() {
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionUri() {
        return sessionUri;
    }

    public void setSessionUri(String sessionUri) {
        this.sessionUri = sessionUri;
    }

    public String getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(String sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public String getCredentialProviderIdentifier() {
        return credentialProviderIdentifier;
    }

    public void setCredentialProviderIdentifier(String credentialProviderIdentifier) {
        this.credentialProviderIdentifier = credentialProviderIdentifier;
    }

    public String getConsumerType() {
        return consumerType;
    }

    public void setConsumerType(String consumerType) {
        this.consumerType = consumerType;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getCreatorType() {
        return creatorType;
    }

    public void setCreatorType(String creatorType) {
        this.creatorType = creatorType;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getAuthorizationUrl() {
        return authorizationUrl;
    }

    public void setAuthorizationUrl(String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }

    public Long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getAuthenticationTokenId() {
        return authenticationTokenId;
    }

    public void setAuthenticationTokenId(String authenticationTokenId) {
        this.authenticationTokenId = authenticationTokenId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
