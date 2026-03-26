
package com.cloud_idaas.pam.option;

import java.io.Serializable;

/**
 * Optional parameters for validating an authentication token.
 * Use {@link #builder()} to create an instance.
 */
public class ValidateAuthenticationTokenOptions implements Serializable {

    private static final long serialVersionUID = 2428615710709901367L;

    private final String tokenTypeHint;

    private ValidateAuthenticationTokenOptions(Builder builder) {
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
     * Creates a new builder for ValidateAuthTokenOptions.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for ValidateAuthTokenOptions.
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
         * Builds the ValidateAuthTokenOptions instance.
         *
         * @return a new ValidateAuthTokenOptions
         */
        public ValidateAuthenticationTokenOptions build() {
            return new ValidateAuthenticationTokenOptions(this);
        }
    }
}
