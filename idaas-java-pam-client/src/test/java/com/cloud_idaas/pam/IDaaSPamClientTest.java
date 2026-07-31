package com.cloud_idaas.pam;

import com.aliyun.eiam_developerapi20220225.Client;
import com.aliyun.eiam_developerapi20220225.models.FetchOAuthAuthenticationTokenRequest;
import com.aliyun.eiam_developerapi20220225.models.FetchOAuthAuthenticationTokenResponse;
import com.aliyun.eiam_developerapi20220225.models.FetchOAuthAuthenticationTokenResponseBody;
import com.aliyun.eiam_developerapi20220225.models.GetOAuthAuthorizationSessionResponse;
import com.aliyun.eiam_developerapi20220225.models.GetOAuthAuthorizationSessionResponseBody;
import com.aliyun.tea.TeaException;
import com.cloud_idaas.core.exception.ClientException;
import com.cloud_idaas.core.provider.IDaaSCredentialProvider;
import com.cloud_idaas.pam.domain.OAuthAuthenticationTokenResponse;
import com.cloud_idaas.pam.domain.OAuthAuthorizationFlow;
import com.cloud_idaas.pam.domain.OAuthAuthorizationSessionResponse;
import com.cloud_idaas.pam.domain.PamClientConstants;
import com.cloud_idaas.pam.option.FetchOAuthAuthenticationOptions;
import com.cloud_idaas.pam.option.PollOAuthAuthenticationTokenOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the 3LO capabilities of {@link IDaaSPamClient}:
 * {@code fetchOAuthAuthenticationTokenV2}, {@code getOAuthAuthorizationSession}
 * and {@code pollOAuthAuthenticationToken}.
 */
public class IDaaSPamClientTest {

    private static final String INSTANCE_ID = "instance-id";
    private static final String PROVIDER = "google-provider";

    private Client mockClient;
    private IDaaSPamClient pamClient;

    @BeforeEach
    void setUp() throws Exception {
        IDaaSCredentialProvider provider = Mockito.mock(IDaaSCredentialProvider.class);
        when(provider.getBearerToken()).thenReturn("valid_token");
        IDaaSPamClient real = new IDaaSPamClient("api.example.com", INSTANCE_ID, provider);
        pamClient = Mockito.spy(real);
        mockClient = Mockito.mock(Client.class);
        injectClient(pamClient, mockClient);
    }

    private void injectClient(IDaaSPamClient target, Client client) throws Exception {
        Field field = IDaaSPamClient.class.getDeclaredField("client");
        field.setAccessible(true);
        field.set(target, client);
    }

    // --- response builders ---

    private FetchOAuthAuthenticationTokenResponse fetchResponseWithToken(String tokenValue) {
        FetchOAuthAuthenticationTokenResponseBody body = new FetchOAuthAuthenticationTokenResponseBody()
                .setOauthAccessTokenContent(
                        new FetchOAuthAuthenticationTokenResponseBody.FetchOAuthAuthenticationTokenResponseBodyOauthAccessTokenContent()
                                .setAccessTokenValue(tokenValue)
                                .setTokenType("Bearer")
                                .setScope("read"));
        return new FetchOAuthAuthenticationTokenResponse().setStatusCode(200).setBody(body);
    }

    private FetchOAuthAuthenticationTokenResponse fetchResponseWithSession(String sessionUri, String authUrl) {
        FetchOAuthAuthenticationTokenResponseBody body = new FetchOAuthAuthenticationTokenResponseBody()
                .setOauthAuthorizationSession(
                        new FetchOAuthAuthenticationTokenResponseBody.FetchOAuthAuthenticationTokenResponseBodyOauthAuthorizationSession()
                                .setSessionId("sid")
                                .setSessionUri(sessionUri)
                                .setAuthorizationUrl(authUrl)
                                .setSessionStatus(PamClientConstants.SESSION_STATUS_PENDING));
        return new FetchOAuthAuthenticationTokenResponse().setStatusCode(200).setBody(body);
    }

    private GetOAuthAuthorizationSessionResponse sessionResponse(String status, String errorCode, String errorDescription) {
        GetOAuthAuthorizationSessionResponseBody body = new GetOAuthAuthorizationSessionResponseBody()
                .setSessionStatus(status)
                .setSessionUri("session-uri")
                .setAuthorizationUrl("https://auth.example.com")
                .setAuthenticationTokenId("token-id")
                .setErrorCode(errorCode)
                .setErrorDescription(errorDescription);
        return new GetOAuthAuthorizationSessionResponse().setStatusCode(200).setBody(body);
    }

    private TeaException teaException(int statusCode, String code, String message) {
        Map<String, Object> data = new HashMap<>();
        data.put("statusCode", statusCode);
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("message", message);
        map.put("data", data);
        return new TeaException(map);
    }

    // --- 5.1 fetchOAuthAuthenticationTokenV2 ---

    @Test
    void fetchV2_m2m_ReturnsToken() throws Exception {
        when(mockClient.fetchOAuthAuthenticationToken(eq(INSTANCE_ID), any())).thenReturn(fetchResponseWithToken("abc"));

        OAuthAuthenticationTokenResponse response = pamClient.fetchOAuthAuthenticationTokenV2(PROVIDER, OAuthAuthorizationFlow.M2M);

        assertTrue(response.hasOAuthAccessTokenContent());
        assertFalse(response.hasOAuthAuthorizationSession());
        assertEquals("abc", response.getOauthAccessTokenContent().getAccessTokenValue());
    }

    @Test
    void fetchV2_userFederation_NeedsAuthorization() throws Exception {
        when(mockClient.fetchOAuthAuthenticationToken(eq(INSTANCE_ID), any()))
                .thenReturn(fetchResponseWithSession("session-uri", "https://auth.example.com"));

        OAuthAuthenticationTokenResponse response = pamClient.fetchOAuthAuthenticationTokenV2(PROVIDER, OAuthAuthorizationFlow.USER_FEDERATION);

        assertTrue(response.hasOAuthAuthorizationSession());
        assertFalse(response.hasOAuthAccessTokenContent());
        assertEquals("https://auth.example.com", response.getOauthAuthorizationSession().getAuthorizationUrl());
    }

    @Test
    void fetchV2_userFederation_TokenAlreadyAvailable() throws Exception {
        when(mockClient.fetchOAuthAuthenticationToken(eq(INSTANCE_ID), any())).thenReturn(fetchResponseWithToken("xyz"));

        OAuthAuthenticationTokenResponse response = pamClient.fetchOAuthAuthenticationTokenV2(PROVIDER, OAuthAuthorizationFlow.USER_FEDERATION);

        assertTrue(response.hasOAuthAccessTokenContent());
        assertEquals("xyz", response.getOauthAccessTokenContent().getAccessTokenValue());
    }

    @Test
    void fetchV2_m2m_ButSessionReturned_ThrowsClientException() throws Exception {
        when(mockClient.fetchOAuthAuthenticationToken(eq(INSTANCE_ID), any()))
                .thenReturn(fetchResponseWithSession("session-uri", "https://auth.example.com"));

        ClientException ex = assertThrows(ClientException.class,
                () -> pamClient.fetchOAuthAuthenticationTokenV2(PROVIDER, OAuthAuthorizationFlow.M2M));
        assertEquals("authorization_flow_mismatch", ex.getErrorCode());
    }

    @Test
    void fetchV2_invalidFlow_ThrowsClientException() {
        assertThrows(ClientException.class,
                () -> pamClient.fetchOAuthAuthenticationTokenV2(PROVIDER, "invalid_flow"));
    }

    @Test
    void fetchV2_forceAuthentication_PassedToServer() throws Exception {
        when(mockClient.fetchOAuthAuthenticationToken(eq(INSTANCE_ID), any())).thenReturn(fetchResponseWithToken("abc"));
        FetchOAuthAuthenticationOptions options = FetchOAuthAuthenticationOptions.builder()
                .forceAuthentication(true).build();

        pamClient.fetchOAuthAuthenticationTokenV2(PROVIDER, OAuthAuthorizationFlow.USER_FEDERATION, options);

        ArgumentCaptor<FetchOAuthAuthenticationTokenRequest> captor = ArgumentCaptor.forClass(FetchOAuthAuthenticationTokenRequest.class);
        verify(mockClient).fetchOAuthAuthenticationToken(eq(INSTANCE_ID), captor.capture());
        assertEquals(Boolean.TRUE, captor.getValue().getForceAuthentication());
    }

    @Test
    void fetchV2_customParameters_PassedToServer() throws Exception {
        when(mockClient.fetchOAuthAuthenticationToken(eq(INSTANCE_ID), any())).thenReturn(fetchResponseWithToken("abc"));
        Map<String, String> custom = new HashMap<>();
        custom.put("prompt", "consent");
        FetchOAuthAuthenticationOptions options = FetchOAuthAuthenticationOptions.builder()
                .customParameters(custom).build();

        pamClient.fetchOAuthAuthenticationTokenV2(PROVIDER, OAuthAuthorizationFlow.USER_FEDERATION, options);

        ArgumentCaptor<FetchOAuthAuthenticationTokenRequest> captor = ArgumentCaptor.forClass(FetchOAuthAuthenticationTokenRequest.class);
        verify(mockClient).fetchOAuthAuthenticationToken(eq(INSTANCE_ID), captor.capture());
        assertEquals("consent", captor.getValue().getCustomParameters().get("prompt"));
    }

    // --- 5.2 getOAuthAuthorizationSession ---

    @Test
    void getSession_Pending_MapsFields() throws Exception {
        when(mockClient.getOAuthAuthorizationSession(eq(INSTANCE_ID), any()))
                .thenReturn(sessionResponse(PamClientConstants.SESSION_STATUS_PENDING, null, null));

        OAuthAuthorizationSessionResponse response = pamClient.getOAuthAuthorizationSession("session-uri");

        assertEquals(PamClientConstants.SESSION_STATUS_PENDING, response.getSessionStatus());
        assertEquals("https://auth.example.com", response.getAuthorizationUrl());
    }

    @Test
    void getSession_Completed_MapsTokenId() throws Exception {
        when(mockClient.getOAuthAuthorizationSession(eq(INSTANCE_ID), any()))
                .thenReturn(sessionResponse(PamClientConstants.SESSION_STATUS_COMPLETED, null, null));

        OAuthAuthorizationSessionResponse response = pamClient.getOAuthAuthorizationSession("session-uri");

        assertEquals(PamClientConstants.SESSION_STATUS_COMPLETED, response.getSessionStatus());
        assertEquals("token-id", response.getAuthenticationTokenId());
    }

    @Test
    void getSession_Failed_MapsError() throws Exception {
        when(mockClient.getOAuthAuthorizationSession(eq(INSTANCE_ID), any()))
                .thenReturn(sessionResponse(PamClientConstants.SESSION_STATUS_FAILED, "access_denied", "user denied"));

        OAuthAuthorizationSessionResponse response = pamClient.getOAuthAuthorizationSession("session-uri");

        assertEquals(PamClientConstants.SESSION_STATUS_FAILED, response.getSessionStatus());
        assertEquals("access_denied", response.getErrorCode());
        assertEquals("user denied", response.getErrorDescription());
    }

    @Test
    void getSession_NotFound_ThrowsClientException() throws Exception {
        when(mockClient.getOAuthAuthorizationSession(eq(INSTANCE_ID), any()))
                .thenThrow(teaException(404, "oauth_session_not_found", "session not found"));

        assertThrows(ClientException.class, () -> pamClient.getOAuthAuthorizationSession("missing-uri"));
    }

    // --- 5.3 pollOAuthAuthenticationToken ---

    @Test
    void poll_TokenAvailable_ReturnsDirectly() throws Exception {
        when(mockClient.fetchOAuthAuthenticationToken(eq(INSTANCE_ID), any())).thenReturn(fetchResponseWithToken("abc"));
        AtomicInteger callbackCount = new AtomicInteger(0);

        OAuthAuthenticationTokenResponse response = pamClient.pollOAuthAuthenticationToken(PROVIDER, url -> callbackCount.incrementAndGet());

        assertTrue(response.hasOAuthAccessTokenContent());
        assertEquals(0, callbackCount.get());
        verify(mockClient, never()).getOAuthAuthorizationSession(any(), any());
    }

    @Test
    void poll_NeedsAuth_PollsUntilCompleted() throws Exception {
        when(mockClient.fetchOAuthAuthenticationToken(eq(INSTANCE_ID), any()))
                .thenReturn(fetchResponseWithSession("session-uri", "https://auth.example.com"))
                .thenReturn(fetchResponseWithToken("final-token"));
        when(mockClient.getOAuthAuthorizationSession(eq(INSTANCE_ID), any()))
                .thenReturn(sessionResponse(PamClientConstants.SESSION_STATUS_COMPLETED, null, null));
        doNothing().when(pamClient).sleepBetweenPolls(anyLong());
        AtomicReference<String> capturedUrl = new AtomicReference<>();

        OAuthAuthenticationTokenResponse response = pamClient.pollOAuthAuthenticationToken(PROVIDER, capturedUrl::set);

        assertTrue(response.hasOAuthAccessTokenContent());
        assertEquals("final-token", response.getOauthAccessTokenContent().getAccessTokenValue());
        assertEquals("https://auth.example.com", capturedUrl.get());
    }

    @Test
    void poll_SessionFailed_ThrowsClientException() throws Exception {
        when(mockClient.fetchOAuthAuthenticationToken(eq(INSTANCE_ID), any()))
                .thenReturn(fetchResponseWithSession("session-uri", "https://auth.example.com"));
        when(mockClient.getOAuthAuthorizationSession(eq(INSTANCE_ID), any()))
                .thenReturn(sessionResponse(PamClientConstants.SESSION_STATUS_FAILED, "access_denied", "user denied"));
        doNothing().when(pamClient).sleepBetweenPolls(anyLong());

        ClientException ex = assertThrows(ClientException.class,
                () -> pamClient.pollOAuthAuthenticationToken(PROVIDER, url -> {
                }));
        assertEquals("access_denied", ex.getErrorCode());
    }

    @Test
    void poll_SessionExpired_ThrowsClientException() throws Exception {
        when(mockClient.fetchOAuthAuthenticationToken(eq(INSTANCE_ID), any()))
                .thenReturn(fetchResponseWithSession("session-uri", "https://auth.example.com"));
        when(mockClient.getOAuthAuthorizationSession(eq(INSTANCE_ID), any()))
                .thenReturn(sessionResponse(PamClientConstants.SESSION_STATUS_EXPIRED, null, null));
        doNothing().when(pamClient).sleepBetweenPolls(anyLong());

        ClientException ex = assertThrows(ClientException.class,
                () -> pamClient.pollOAuthAuthenticationToken(PROVIDER, url -> {
                }));
        assertEquals("authorization_session_expired", ex.getErrorCode());
    }

    @Test
    void poll_CallbackThrows_PropagatesException() throws Exception {
        when(mockClient.fetchOAuthAuthenticationToken(eq(INSTANCE_ID), any()))
                .thenReturn(fetchResponseWithSession("session-uri", "https://auth.example.com"));

        Consumer<String> throwingCallback = url -> {
            throw new IllegalStateException("callback boom");
        };

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> pamClient.pollOAuthAuthenticationToken(PROVIDER, throwingCallback));
        assertEquals("callback boom", ex.getMessage());
    }

    // --- 5.4 polling timeout ---

    @Test
    void poll_HardLimit180s_ThrowsTimeout() throws Exception {
        when(mockClient.fetchOAuthAuthenticationToken(eq(INSTANCE_ID), any()))
                .thenReturn(fetchResponseWithSession("session-uri", "https://auth.example.com"));
        // deadline call returns 0 -> deadline = 180000; first check returns 178000 -> 178000 + 3000 > 180000 => timeout
        when(pamClient.currentTimeMillis()).thenReturn(0L, 178_000L);
        PollOAuthAuthenticationTokenOptions options = PollOAuthAuthenticationTokenOptions.builder()
                .maxPollingRetries(100).build();

        ClientException ex = assertThrows(ClientException.class,
                () -> pamClient.pollOAuthAuthenticationToken(PROVIDER, url -> {
                }, options));
        assertEquals("polling_timeout", ex.getErrorCode());
        verify(mockClient, never()).getOAuthAuthorizationSession(any(), any());
    }

    @Test
    void poll_RetriesExhausted_ThrowsTimeout() throws Exception {
        when(mockClient.fetchOAuthAuthenticationToken(eq(INSTANCE_ID), any()))
                .thenReturn(fetchResponseWithSession("session-uri", "https://auth.example.com"));
        when(mockClient.getOAuthAuthorizationSession(eq(INSTANCE_ID), any()))
                .thenReturn(sessionResponse(PamClientConstants.SESSION_STATUS_PENDING, null, null));
        doNothing().when(pamClient).sleepBetweenPolls(anyLong());
        PollOAuthAuthenticationTokenOptions options = PollOAuthAuthenticationTokenOptions.builder()
                .maxPollingRetries(2).build();

        ClientException ex = assertThrows(ClientException.class,
                () -> pamClient.pollOAuthAuthenticationToken(PROVIDER, url -> {
                }, options));
        assertEquals("polling_timeout", ex.getErrorCode());
        verify(mockClient, times(2)).getOAuthAuthorizationSession(eq(INSTANCE_ID), any());
    }
}
