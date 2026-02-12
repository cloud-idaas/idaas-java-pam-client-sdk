package com.cloud_idaas.pam;

import com.aliyun.eiam_developerapi20220225.Client;
import com.aliyun.eiam_developerapi20220225.models.ObtainCredentialRequest;
import com.aliyun.eiam_developerapi20220225.models.ObtainCredentialResponse;
import com.aliyun.eiam_developerapi20220225.models.ObtainCredentialResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.tea.interceptor.RequestInterceptor;
import com.aliyun.teaopenapi.models.Config;
import com.cloud_idaas.core.exception.ClientException;
import com.cloud_idaas.core.exception.ConfigException;
import com.cloud_idaas.core.exception.CredentialException;
import com.cloud_idaas.core.exception.ServerException;
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.core.provider.IDaaSCredentialProvider;
import com.cloud_idaas.pam.domain.PamClientConstants;
import com.cloud_idaas.pam.interceptor.BearerTokenRequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class IDaaSPamClient {

    /**
     * IDaaS Developer API endpoint for developers.
     */
    private final String developerApiEndpoint;

    /**
     * IDaaS instance ID.
     */
    private final String idaasInstanceId;

    private IDaaSCredentialProvider credentialProvider;

    private final transient Client client;

    private final transient RequestInterceptor requestInterceptor;

    private static final Logger LOGGER = LoggerFactory.getLogger(IDaaSPamClient.class);

    public IDaaSPamClient() {
        this(IDaaSCredentialProviderFactory.getDeveloperApiEndpoint(),
                IDaaSCredentialProviderFactory.getIDaasInstanceId(),
                IDaaSCredentialProviderFactory.getIDaaSCredentialProvider(PamClientConstants.SCOPE));
    }

    public IDaaSPamClient(String developerApiEndpoint, String idaasInstanceId, IDaaSCredentialProvider credentialProvider) {
        this.developerApiEndpoint = getDeveloperApiEndpoint(developerApiEndpoint);
        if (this.developerApiEndpoint == null) {
            throw new ConfigException("DeveloperApiEndpoint can not be empty");
        }
        this.idaasInstanceId = idaasInstanceId != null ? idaasInstanceId : IDaaSCredentialProviderFactory.getIDaasInstanceId();
        if (this.idaasInstanceId == null) {
            throw new ConfigException("IDaasInstanceId can not be empty");
        }
        this.credentialProvider = credentialProvider != null ? credentialProvider : IDaaSCredentialProviderFactory.getIDaaSCredentialProvider(
                PamClientConstants.SCOPE);
        if (this.credentialProvider == null) {
            throw new ConfigException("CredentialProvider can not be empty");
        }
        try {
            this.client = new Client(new Config()
                    .setEndpoint(this.developerApiEndpoint));
            this.requestInterceptor = new BearerTokenRequestInterceptor(this.credentialProvider);
            this.client.addRequestInterceptor(this.requestInterceptor);
        } catch (Exception e) {
            LOGGER.error("Error occurred while creating IDaaSPamClient: {}", e.getMessage());
            throw new ConfigException(e.getMessage());
        }
    }

    private String getDeveloperApiEndpoint(String developerApiEndpoint) {
        String realEndpoint = (developerApiEndpoint != null ? developerApiEndpoint : IDaaSCredentialProviderFactory.getDeveloperApiEndpoint());

        if (realEndpoint == null || realEndpoint.trim().isEmpty()) {
            return realEndpoint;
        }

        if (realEndpoint.startsWith("https://")) {
            realEndpoint = realEndpoint.substring(8);
        } else if (realEndpoint.startsWith("http://")) {
            realEndpoint = realEndpoint.substring(7);
        }

        return realEndpoint;
    }

    public String getApiKey(String credentialIdentifier) {
        try {
            ObtainCredentialRequest request = new ObtainCredentialRequest()
                    .setCredentialIdentifier(credentialIdentifier);
            ObtainCredentialResponse response = this.client.obtainCredential(this.idaasInstanceId, request);
            if (response.getStatusCode() == PamClientConstants.STATUS_CODE_200) {
                ObtainCredentialResponseBody responseBody = response.getBody();
                ObtainCredentialResponseBody.ObtainCredentialResponseBodyCredentialContent credentialContent = responseBody.getCredentialContent();
                if (credentialContent != null) {
                    ObtainCredentialResponseBody.ObtainCredentialResponseBodyCredentialContentApiKeyContent apiKeyContent
                            = credentialContent.getApiKeyContent();
                    if (apiKeyContent != null) {
                        return apiKeyContent.getApiKey();
                    } else {
                        LOGGER.info("The credential retrieval operation using the CredentialIdentifier was successful; however, "
                                + "the ApiContent field returned null, suggesting that an incorrect API method may have been invoked.");
                    }
                }
            }
            return null;
        } catch (TeaException e){
            int statusCode = e.getStatusCode();
            if (statusCode >= 400 && statusCode < 500){
                String code = Optional.ofNullable(e.getData())
                        .map(data -> (String) data.get("error"))
                        .orElse(null);
                String errorDescription = Optional.ofNullable(e.getData())
                        .map(data -> (String) data.get("error_description"))
                        .orElse(null);
                String requestId = Optional.ofNullable(e.getData())
                        .map(data -> (String) data.get("request_id"))
                        .orElse(null);
                String message = String.format("code: %s, %s request_id: %s", code, errorDescription, requestId);
                LOGGER.error("Client Error: {}", code);
                LOGGER.error("Client Error Message: {}", message);
                LOGGER.error("Client Error RequestId: {}", requestId);
                throw new ClientException(code, message, requestId);
            } else if (statusCode >= 500) {
                LOGGER.error("Server Error Message: {}", e.getMessage());
                throw e;
            } else {
                LOGGER.error("Error occurred while obtaining credential: {}", e.getMessage());
                throw e;
            }
        } catch (Exception e){
            LOGGER.error("Error occurred while obtaining credential: {}", e.getMessage());
            throw new CredentialException(e.getMessage(), e);
        }
    }

    public static IDaaSPamClientBuilder builder() {
        return new IDaaSPamClientBuilder();
    }

    public static final class IDaaSPamClientBuilder {
        private String developerApiEndpoint;
        private String idaasInstanceId;
        private IDaaSCredentialProvider credentialProvider;

        private IDaaSPamClientBuilder() {
        }

        public IDaaSPamClientBuilder developerApiEndpoint(String developerApiEndpoint) {
            this.developerApiEndpoint = developerApiEndpoint;
            return this;
        }

        public IDaaSPamClientBuilder idaasInstanceId(String idaasInstanceId) {
            this.idaasInstanceId = idaasInstanceId;
            return this;
        }

        public IDaaSPamClientBuilder credentialProvider(IDaaSCredentialProvider credentialProvider) {
            this.credentialProvider = credentialProvider;
            return this;
        }

        public IDaaSPamClient build() {
            return new IDaaSPamClient(this.developerApiEndpoint, this.idaasInstanceId, this.credentialProvider);
        }
    }

}
