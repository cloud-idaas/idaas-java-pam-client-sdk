package com.cloud_idaas.pam.option;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Optional parameters for generating a JWT authentication token.
 * Use {@link #builder()} to create an instance.
 */
public class GenerateJwtAuthenticationOptions implements Serializable {

    private static final long serialVersionUID = 2166506472654749246L;

    private final String issuer;
    private final Map<String, Object> customClaims;
    private final Integer expiration;
    private final Boolean includeDerivedShortToken;

    private GenerateJwtAuthenticationOptions(Builder builder) {
        this.issuer = builder.issuer;
        this.customClaims = builder.customClaims != null ? new HashMap<>(builder.customClaims) : null;
        this.expiration = builder.expiration;
        this.includeDerivedShortToken = builder.includeDerivedShortToken;
    }

    public String getIssuer() {
        return issuer;
    }

    public Map<String, Object> getCustomClaims() {
        return customClaims;
    }

    public Integer getExpiration() {
        return expiration;
    }

    public Boolean getIncludeDerivedShortToken() {
        return includeDerivedShortToken;
    }

    /**
     * Creates a new builder for GenerateJwtTokenOptions.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for GenerateJwtTokenOptions.
     */
    public static final class Builder {
        private String issuer;
        private Map<String, Object> customClaims;
        private Integer expiration;
        private Boolean includeDerivedShortToken;

        private Builder() {
        }

        public Builder issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        public Builder customClaims(Map<String, Object> customClaims) {
            this.customClaims = customClaims;
            return this;
        }

        public Builder expiration(Integer expiration) {
            this.expiration = expiration;
            return this;
        }

        public Builder includeDerivedShortToken(Boolean includeDerivedShortToken) {
            this.includeDerivedShortToken = includeDerivedShortToken;
            return this;
        }

        /**
         * Builds the GenerateJwtTokenOptions instance.
         *
         * @return a new GenerateJwtTokenOptions
         */
        public GenerateJwtAuthenticationOptions build() {
            return new GenerateJwtAuthenticationOptions(this);
        }
    }
}
