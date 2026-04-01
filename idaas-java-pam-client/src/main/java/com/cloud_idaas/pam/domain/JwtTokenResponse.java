package com.cloud_idaas.pam.domain;

import java.io.Serializable;

public class JwtTokenResponse implements Serializable {

    private static final long serialVersionUID = 7382916405837261948L;

    /**
     * Authentication token ID
     */
    private String authenticationTokenId;
    /**
     * Authentication token consumer type, optional values: application, custom
     */
    private String consumerType;

    /**
     * Authentication token consumer ID
     */
    private String consumerId;

    /**
     * JWT type authentication token content
     */
    private JwtContent jwtContent;

    public JwtTokenResponse() {
    }

    public String getAuthenticationTokenId() {
        return authenticationTokenId;
    }

    public void setAuthenticationTokenId(String authenticationTokenId) {
        this.authenticationTokenId = authenticationTokenId;
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

    public JwtContent getJwtContent() {
        return jwtContent;
    }

    public void setJwtContent(JwtContent jwtContent) {
        this.jwtContent = jwtContent;
    }
}
