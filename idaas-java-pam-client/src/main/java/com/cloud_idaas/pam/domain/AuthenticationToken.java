package com.cloud_idaas.pam.domain;

import java.io.Serializable;

public class AuthenticationToken implements Serializable {

    private static final long serialVersionUID = 3901557682423792027L;

    /**
     * The IDaaS instance ID.
     */
    private String instanceId;

    /**
     * The unique identifier of the authentication token.
     */
    private String authenticationTokenId;

    /**
     * The unique identifier of the credential provider.
     */
    private String credentialProviderId;

    /**
     * The creation time of the authentication token, as a Unix timestamp in milliseconds.
     */
    private Long createTime;

    /**
     * The last update time of the authentication token, as a Unix timestamp in milliseconds.
     */
    private Long updateTime;

    /**
     * The type of the authentication token.
     * Enum value: jwt (JWT token)
     */
    private String authenticationTokenType;

    /**
     * Indicates whether the authentication token has been revoked.
     */
    private Boolean revoked;

    /**
     * The type of the entity that created this token.
     * Enum value: application
     */
    private String creatorType;

    /**
     * The identifier of the entity that created this token.
     */
    private String creatorId;

    /**
     * The type of the consumer that uses this token.
     * Enum value: custom
     */
    private String consumerType;

    /**
     * The identifier of the consumer that uses this token.
     */
    private String consumerId;

    /**
     * The expiration time of the authentication token, as a Unix timestamp in seconds.
     */
    private Long expirationTime;


    public AuthenticationToken() {
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getAuthenticationTokenId() {
        return authenticationTokenId;
    }

    public void setAuthenticationTokenId(String authenticationTokenId) {
        this.authenticationTokenId = authenticationTokenId;
    }

    public String getCredentialProviderId() {
        return credentialProviderId;
    }

    public void setCredentialProviderId(String credentialProviderId) {
        this.credentialProviderId = credentialProviderId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getAuthenticationTokenType() {
        return authenticationTokenType;
    }

    public void setAuthenticationTokenType(String authenticationTokenType) {
        this.authenticationTokenType = authenticationTokenType;
    }

    public Boolean getRevoked() {
        return revoked;
    }

    public void setRevoked(Boolean revoked) {
        this.revoked = revoked;
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

    public Long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Long expirationTime) {
        this.expirationTime = expirationTime;
    }
}
