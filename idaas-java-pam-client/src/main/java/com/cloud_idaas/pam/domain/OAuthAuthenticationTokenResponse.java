package com.cloud_idaas.pam.domain;

import java.io.Serializable;

/**
 * Response model for {@code fetchOAuthAuthenticationTokenV2} and
 * {@code pollOAuthAuthenticationToken}.
 *
 * <p>Contains either {@code oauthAccessTokenContent} (token available) or
 * {@code oauthAuthorizationSession} (user authorization needed), but never both.
 */
public class OAuthAuthenticationTokenResponse implements Serializable {

    private static final long serialVersionUID = 4926718350194873261L;

    private String authenticationTokenId;
    private String authenticationTokenType;
    private String consumerId;
    private String consumerType;
    private Long createTime;
    private String creatorId;
    private String creatorType;
    private String credentialProviderId;
    private Long expirationTime;
    private String instanceId;
    private Boolean revoked;
    private Long updateTime;

    /**
     * OAuth Access Token content. Present when a valid token is available.
     */
    private OAuthAccessTokenContent oauthAccessTokenContent;

    /**
     * OAuth Authorization Session info. Present when user authorization is required.
     */
    private OAuthAuthorizationSession oauthAuthorizationSession;

    public OAuthAuthenticationTokenResponse() {
    }

    /**
     * Returns {@code true} if this response contains an available access token.
     */
    public boolean hasOAuthAccessTokenContent() {
        return oauthAccessTokenContent != null;
    }

    /**
     * Returns {@code true} if this response indicates that user authorization is needed.
     */
    public boolean hasOAuthAuthorizationSession() {
        return oauthAuthorizationSession != null;
    }

    // --- getters and setters ---

    public String getAuthenticationTokenId() {
        return authenticationTokenId;
    }

    public void setAuthenticationTokenId(String authenticationTokenId) {
        this.authenticationTokenId = authenticationTokenId;
    }

    public String getAuthenticationTokenType() {
        return authenticationTokenType;
    }

    public void setAuthenticationTokenType(String authenticationTokenType) {
        this.authenticationTokenType = authenticationTokenType;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getConsumerType() {
        return consumerType;
    }

    public void setConsumerType(String consumerType) {
        this.consumerType = consumerType;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorType() {
        return creatorType;
    }

    public void setCreatorType(String creatorType) {
        this.creatorType = creatorType;
    }

    public String getCredentialProviderId() {
        return credentialProviderId;
    }

    public void setCredentialProviderId(String credentialProviderId) {
        this.credentialProviderId = credentialProviderId;
    }

    public Long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Boolean getRevoked() {
        return revoked;
    }

    public void setRevoked(Boolean revoked) {
        this.revoked = revoked;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public OAuthAccessTokenContent getOauthAccessTokenContent() {
        return oauthAccessTokenContent;
    }

    public void setOauthAccessTokenContent(OAuthAccessTokenContent oauthAccessTokenContent) {
        this.oauthAccessTokenContent = oauthAccessTokenContent;
    }

    public OAuthAuthorizationSession getOauthAuthorizationSession() {
        return oauthAuthorizationSession;
    }

    public void setOauthAuthorizationSession(OAuthAuthorizationSession oauthAuthorizationSession) {
        this.oauthAuthorizationSession = oauthAuthorizationSession;
    }
}
