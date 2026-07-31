package com.cloud_idaas.pam.domain;

import java.io.Serializable;

/**
 * OAuth authorization session information returned when user authorization
 * is required (3LO flow).
 */
public class OAuthAuthorizationSession implements Serializable {

    private static final long serialVersionUID = 8471290365512847199L;

    /**
     * The authorization session ID.
     */
    private String sessionId;

    /**
     * The authorization session URI, in the format
     * {@code urn:ietf:params:oauth:request_uri:{sessionId}}.
     */
    private String sessionUri;

    /**
     * The URL that the end user must open in a browser to complete authorization.
     */
    private String authorizationUrl;

    /**
     * The authorization session status, e.g. "pending".
     */
    private String sessionStatus;

    public OAuthAuthorizationSession() {
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

    public String getAuthorizationUrl() {
        return authorizationUrl;
    }

    public void setAuthorizationUrl(String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }

    public String getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(String sessionStatus) {
        this.sessionStatus = sessionStatus;
    }
}
