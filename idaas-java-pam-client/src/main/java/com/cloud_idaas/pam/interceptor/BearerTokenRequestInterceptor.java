package com.cloud_idaas.pam.interceptor;

import com.aliyun.tea.TeaRequest;
import com.aliyun.tea.interceptor.InterceptorContext;
import com.aliyun.tea.interceptor.RequestInterceptor;
import com.aliyun.tea.utils.AttributeMap;
import com.cloud_idaas.core.domain.constants.HttpConstants;
import com.cloud_idaas.core.provider.IDaaSCredentialProvider;
import com.cloud_idaas.core.util.StringUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * A request interceptor that adds Bearer Token authentication to HTTP requests.
 * This class implements the {@link RequestInterceptor} interface to inject
 * the Authorization header into outgoing requests.
 */
public class BearerTokenRequestInterceptor implements RequestInterceptor {

    private final Set<String> ignoreMethods = new HashSet<>();
    {
        ignoreMethods.add("validate");
    }

    /**
     * The credential provider used to retrieve the Bearer Token.
     */
    private final IDaaSCredentialProvider credentialProvider;

    /**
     * Constructs a new instance with the specified credential provider.
     *
     * @param credentialProvider the provider for retrieving Bearer Tokens,
     *                           must not be null and must contain a valid token
     * @throws IllegalArgumentException if the provider is null or its token is empty
     */
    public BearerTokenRequestInterceptor(IDaaSCredentialProvider credentialProvider) {
        if (credentialProvider == null) {
            throw new IllegalArgumentException("credentialProvider cannot be null.");
        }
        if (StringUtil.isEmpty(credentialProvider.getBearerToken())) {
            throw new IllegalArgumentException("credentialProvider.getBearerToken() cannot be empty.");
        }
        this.credentialProvider = credentialProvider;
    }

    /**
     * Retrieves the currently configured credential provider.
     *
     * @return the credential provider instance
     */
    public IDaaSCredentialProvider getCredentialProvider() {
        return credentialProvider;
    }

    /**
     * Modifies the outgoing request by adding or updating the Authorization header.
     *
     * @param context    the interceptor context containing the original request
     * @param attributes additional configuration parameters (unused in this implementation)
     * @return null, indicating that the original request object is not replaced
     */
    @Override
    public TeaRequest modifyRequest(InterceptorContext context, AttributeMap attributes) {
        TeaRequest request = context.teaRequest();
        String pathName = request.pathname;
        String methodName = pathName.substring(pathName.lastIndexOf("/") + 1);
        if (this.ignoreMethods.contains(methodName)) {
            return request;
        }
        // Initialize headers if not already set
        if (request.headers == null) {
            request.headers = new java.util.HashMap<String, String>();
        }
        // Add or update the Authorization header
        if (!request.headers.containsKey(HttpConstants.AUTHORIZATION_HEADER)) {
            request.headers.put(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.BEARER + HttpConstants.SPACE + this.credentialProvider.getBearerToken());
        }
        return request;
    }
}
