package com.cloud_idaas.pam.option;

import java.io.Serializable;

/**
 * Optional parameters for reinstating an authentication token.
 * Use {@link #builder()} to create an instance.
 */
public class ReinstateAuthenticationTokenOptions implements Serializable {

    private static final long serialVersionUID = 9181705101063767274L;

    private final String tokenTypeHint;

    private ReinstateAuthenticationTokenOptions(Builder builder) {
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
     * Creates a new builder for ReinstateAuthTokenOptions.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for ReinstateAuthTokenOptions.
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
         * Builds the ReinstateAuthTokenOptions instance.
         *
         * @return a new ReinstateAuthTokenOptions
         */
        public ReinstateAuthenticationTokenOptions build() {
            return new ReinstateAuthenticationTokenOptions(this);
        }
    }
}
