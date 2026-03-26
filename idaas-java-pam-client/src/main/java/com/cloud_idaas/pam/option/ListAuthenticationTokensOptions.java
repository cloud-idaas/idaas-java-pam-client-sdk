package com.cloud_idaas.pam.option;

import java.io.Serializable;

/**
 * Optional parameters for listing authentication tokens.
 * Use {@link #builder()} to create an instance.
 */
public class ListAuthenticationTokensOptions implements Serializable {

    private static final long serialVersionUID = 2490097827129125349L;

    private final String nextToken;
    private final Long maxResults;
    private final Boolean revoked;
    private final Boolean expired;

    private ListAuthenticationTokensOptions(Builder builder) {
        this.nextToken = builder.nextToken;
        this.maxResults = builder.maxResults;
        this.revoked = builder.revoked;
        this.expired = builder.expired;
    }

    public String getNextToken() {
        return nextToken;
    }

    public Long getMaxResults() {
        return maxResults;
    }

    public Boolean getRevoked() {
        return revoked;
    }

    public Boolean getExpired() {
        return expired;
    }

    /**
     * Creates a new builder for ListAuthTokensOptions.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for ListAuthTokensOptions.
     */
    public static final class Builder {
        private String nextToken;
        private Long maxResults;
        private Boolean revoked;
        private Boolean expired;

        private Builder() {
        }

        public Builder nextToken(String nextToken) {
            this.nextToken = nextToken;
            return this;
        }

        public Builder maxResults(Long maxResults) {
            this.maxResults = maxResults;
            return this;
        }

        public Builder revoked(Boolean revoked) {
            this.revoked = revoked;
            return this;
        }

        public Builder expired(Boolean expired) {
            this.expired = expired;
            return this;
        }

        /**
         * Builds the ListAuthTokensOptions instance.
         *
         * @return a new ListAuthTokensOptions
         */
        public ListAuthenticationTokensOptions build() {
            return new ListAuthenticationTokensOptions(this);
        }
    }
}
