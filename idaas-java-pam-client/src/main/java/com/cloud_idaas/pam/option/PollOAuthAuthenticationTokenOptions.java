package com.cloud_idaas.pam.option;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Optional parameters for the end-to-end {@code pollOAuthAuthenticationToken} method.
 * Use {@link #builder()} to create an instance.
 */
public class PollOAuthAuthenticationTokenOptions implements Serializable {

    private static final long serialVersionUID = 5183047926384517290L;

    private String scope;
    private Boolean forceAuthentication;
    private Map<String, String> customParameters;

    /**
     * Maximum number of polling retries. Default is 60.
     * The hard time limit of 180 seconds always takes precedence.
     */
    private Integer maxPollingRetries;

    public PollOAuthAuthenticationTokenOptions() {
    }

    private PollOAuthAuthenticationTokenOptions(Builder builder) {
        this.scope = builder.scope;
        this.forceAuthentication = builder.forceAuthentication;
        this.customParameters = builder.customParameters != null ? new HashMap<>(builder.customParameters) : null;
        this.maxPollingRetries = builder.maxPollingRetries;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Boolean getForceAuthentication() {
        return forceAuthentication;
    }

    public void setForceAuthentication(Boolean forceAuthentication) {
        this.forceAuthentication = forceAuthentication;
    }

    public Map<String, String> getCustomParameters() {
        return customParameters;
    }

    public void setCustomParameters(Map<String, String> customParameters) {
        this.customParameters = customParameters != null ? new HashMap<>(customParameters) : null;
    }

    public Integer getMaxPollingRetries() {
        return maxPollingRetries;
    }

    public void setMaxPollingRetries(Integer maxPollingRetries) {
        this.maxPollingRetries = maxPollingRetries;
    }

    /**
     * Creates a new builder.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for PollOAuthAuthenticationTokenOptions.
     */
    public static final class Builder {
        private String scope;
        private Boolean forceAuthentication;
        private Map<String, String> customParameters;
        private Integer maxPollingRetries;

        private Builder() {
        }

        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public Builder forceAuthentication(Boolean forceAuthentication) {
            this.forceAuthentication = forceAuthentication;
            return this;
        }

        public Builder customParameters(Map<String, String> customParameters) {
            this.customParameters = customParameters;
            return this;
        }

        public Builder maxPollingRetries(Integer maxPollingRetries) {
            this.maxPollingRetries = maxPollingRetries;
            return this;
        }

        /**
         * Builds the PollOAuthAuthenticationTokenOptions instance.
         *
         * @return a new PollOAuthAuthenticationTokenOptions
         */
        public PollOAuthAuthenticationTokenOptions build() {
            return new PollOAuthAuthenticationTokenOptions(this);
        }
    }
}
