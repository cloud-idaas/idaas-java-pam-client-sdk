package com.cloud_idaas.pam.domain;

public interface PamClientConstants {

    /**
     * IDaaS PAM Service scope value.
     * The ".all" grants the machine-to-machine client full authorization
     * to access all PAM resource servers within the specified scope.
     */
    String SCOPE = "urn:cloud:idaas:pam|.all";

    int STATUS_CODE_200 = 200;

    /**
     * OAuth authorization session statuses.
     */
    String SESSION_STATUS_PENDING = "pending";
    String SESSION_STATUS_CALLBACK_RECEIVED = "callback_received";
    String SESSION_STATUS_COMPLETED = "completed";
    String SESSION_STATUS_FAILED = "failed";
    String SESSION_STATUS_EXPIRED = "expired";

    /**
     * Polling settings for the end-to-end {@code pollOAuthAuthenticationToken} method.
     * The polling interval is fixed at 3 seconds. The default max retries is 60.
     * The total polling duration is hard-limited to 180 seconds, which always takes
     * precedence over {@code maxPollingRetries}.
     */
    long POLLING_INTERVAL_MILLIS = 3000L;
    int DEFAULT_MAX_POLLING_RETRIES = 60;
    long MAX_POLLING_DURATION_MILLIS = 180000L;
}
