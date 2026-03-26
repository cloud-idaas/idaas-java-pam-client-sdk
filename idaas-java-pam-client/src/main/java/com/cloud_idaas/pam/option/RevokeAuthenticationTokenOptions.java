package com.cloud_idaas.pam.option;

import java.io.Serializable;

/**
 * Optional parameters for revoking an authentication token.
 * Use {@link #builder()} to create an instance.
 */
public class RevokeAuthenticationTokenOptions implements Serializable {

    private static final long serialVersionUID = 2428615710709901395L;

    private final String tokenTypeHint;

    private RevokeAuthenticationTokenOptions(Builder builder) {
        this.tokenTypeHint = builder.tokenTypeHint;
    }

    /**
     * Returns the token type hint.
     *
     * @return the token type hint, or null if not set
     */
    public String getTokenTypeHint() {
        return tokenTypeHint;
    }

    /**
     * Creates a new builder for RevokeAuthTokenOptions.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for RevokeAuthTokenOptions.
     */
    public static final class Builder {
        private String tokenTypeHint;

        private Builder() {
        }

        /**
         * Sets the token type hint.
         *
         * @param tokenTypeHint the token type hint
         * @return this builder
         */
        public Builder tokenTypeHint(String tokenTypeHint) {
            this.tokenTypeHint = tokenTypeHint;
            return this;
        }

        /**
         * Builds the RevokeAuthTokenOptions instance.
         *
         * @return a new RevokeAuthTokenOptions
         */
        public RevokeAuthenticationTokenOptions build() {
            return new RevokeAuthenticationTokenOptions(this);
        }
    }
}
