package com.cloud_idaas.pam.domain;

public interface PamClientConstants {

    /**
     * IDaaS PAM Service scope value.
     * The ".all" grants the machine-to-machine client full authorization
     * to access all PAM resource servers within the specified scope.
     */
    String SCOPE = "urn:cloud:idaas:pam|.all";

    int STATUS_CODE_200 = 200;
}
