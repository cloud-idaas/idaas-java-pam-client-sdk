package com.cloud_idaas.pam.option;

import java.io.Serializable;

/**
 * Optional parameters for fetching an OAuth authentication token.
 * Use {@link #builder()} to create an instance.
 */
public class FetchOAuthAuthenticationOptions implements Serializable {

    private static final long serialVersionUID = 9065908490696423097L;

    private final String scope;

    private FetchOAuthAuthenticationOptions(Builder builder) {
        this.scope = builder.scope;
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
     * Creates a new builder for FetchOAuthTokenOptions.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for FetchOAuthTokenOptions.
     */
    public static final class Builder {
        private String scope;

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
         * Builds the FetchOAuthTokenOptions instance.
         *
         * @return a new FetchOAuthTokenOptions
         */
        public FetchOAuthAuthenticationOptions build() {
            return new FetchOAuthAuthenticationOptions(this);
        }
    }
}
