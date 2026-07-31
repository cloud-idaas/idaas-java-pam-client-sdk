package com.cloud_idaas.pam.domain;

import java.io.Serializable;

/**
 * OAuth Access Token content returned when a token is available.
 */
public class OAuthAccessTokenContent implements Serializable {

    private static final long serialVersionUID = 6152438977265401183L;

    /**
     * The access_token value.
     */
    private String accessTokenValue;

    /**
     * The token_type, usually "Bearer".
     */
    private String tokenType;

    /**
     * The authorized scope.
     */
    private String scope;

    public OAuthAccessTokenContent() {
    }

    public String getAccessTokenValue() {
        return accessTokenValue;
    }

    public void setAccessTokenValue(String accessTokenValue) {
        this.accessTokenValue = accessTokenValue;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
