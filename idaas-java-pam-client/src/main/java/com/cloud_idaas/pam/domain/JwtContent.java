package com.cloud_idaas.pam.domain;

import java.io.Serializable;

public class JwtContent implements Serializable {

    private static final long serialVersionUID = 7502717293092133059L;

    private String jwtValue;

    private String derivedShortToken;

    public JwtContent() {
    }

    public JwtContent(String jwtValue, String derivedShortToken) {
        this.jwtValue = jwtValue;
        this.derivedShortToken = derivedShortToken;
    }

    public String getJwtValue() {
        return jwtValue;
    }

    public void setJwtValue(String jwtValue) {
        this.jwtValue = jwtValue;
    }

    public String getDerivedShortToken() {
        return derivedShortToken;
    }

    public void setDerivedShortToken(String derivedShortToken) {
        this.derivedShortToken = derivedShortToken;
    }

}
