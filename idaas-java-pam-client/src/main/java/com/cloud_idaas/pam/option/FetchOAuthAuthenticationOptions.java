package com.cloud_idaas.pam.option;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Optional parameters for fetching an OAuth authentication token.
 * Use {@link #builder()} to create an instance.
 */
public class FetchOAuthAuthenticationOptions implements Serializable {

    private static final long serialVersionUID = 9065908490696423097L;

    private String scope;

    /**
     * Whether to force re-authorization, ignoring any existing valid token.
     */
    private Boolean forceAuthentication;

    /**
     * Custom parameters appended to the OAuth authorization URL query parameters.
     */
    private Map<String, String> customParameters;

    public FetchOAuthAuthenticationOptions() {
    }

    private FetchOAuthAuthenticationOptions(Builder builder) {
        this.scope = builder.scope;
        this.forceAuthentication = builder.forceAuthentication;
        this.customParameters = builder.customParameters != null ? new HashMap<>(builder.customParameters) : null;
    }

    /**
     * Returns the OAuth scope.
     *
     * @return the scope, or null if not set
     */
    public String getScope() {
        return scope;
    }

    /**
     * Sets the OAuth scope.
     *
     * @param scope the OAuth scope
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Returns whether to force re-authorization.
     *
     * @return the forceAuthentication flag, or null if not set
     */
    public Boolean getForceAuthentication() {
        return forceAuthentication;
    }

    /**
     * Sets whether to force re-authorization.
     *
     * @param forceAuthentication the forceAuthentication flag
     */
    public void setForceAuthentication(Boolean forceAuthentication) {
        this.forceAuthentication = forceAuthentication;
    }

    /**
     * Returns the custom parameters.
     *
     * @return the custom parameters, or null if not set
     */
    public Map<String, String> getCustomParameters() {
        return customParameters;
    }

    /**
     * Sets the custom parameters.
     *
     * @param customParameters the custom parameters
     */
    public void setCustomParameters(Map<String, String> customParameters) {
        this.customParameters = customParameters != null ? new HashMap<>(customParameters) : null;
    }

    /**
     * Creates a new builder for FetchOAuthAuthenticationOptions.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for FetchOAuthAuthenticationOptions.
     */
    public static final class Builder {
        private String scope;
        private Boolean forceAuthentication;
        private Map<String, String> customParameters;

        private Builder() {
        }

        /**
         * Sets the OAuth scope.
         *
         * @param scope the OAuth scope
         * @return this builder
         */
        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        /**
         * Sets whether to force re-authorization.
         *
         * @param forceAuthentication the forceAuthentication flag
         * @return this builder
         */
        public Builder forceAuthentication(Boolean forceAuthentication) {
            this.forceAuthentication = forceAuthentication;
            return this;
        }

        /**
         * Sets the custom parameters.
         *
         * @param customParameters the custom parameters
         * @return this builder
         */
        public Builder customParameters(Map<String, String> customParameters) {
            this.customParameters = customParameters;
            return this;
        }

        /**
         * Builds the FetchOAuthAuthenticationOptions instance.
         *
         * @return a new FetchOAuthAuthenticationOptions
         */
        public FetchOAuthAuthenticationOptions build() {
            return new FetchOAuthAuthenticationOptions(this);
        }
    }
}
