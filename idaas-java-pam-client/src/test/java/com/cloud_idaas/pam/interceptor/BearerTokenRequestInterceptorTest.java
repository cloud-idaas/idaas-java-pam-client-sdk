package com.cloud_idaas.pam.interceptor;

import com.aliyun.tea.TeaRequest;
import com.aliyun.tea.interceptor.InterceptorContext;
import com.aliyun.tea.utils.AttributeMap;
import com.cloud_idaas.core.provider.IDaaSCredentialProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BearerTokenRequestInterceptor.
 */
public class BearerTokenRequestInterceptorTest {

    private IDaaSCredentialProvider mockCredentialProvider;
    private InterceptorContext mockContext;
    private TeaRequest mockRequest;

    @BeforeEach
    void setUp() {
        // Mock dependencies
        mockCredentialProvider = Mockito.mock(IDaaSCredentialProvider.class);
        mockContext = Mockito.mock(InterceptorContext.class);
        mockRequest = Mockito.mock(TeaRequest.class);

        // Default behavior for mockRequest.teaRequest()
        when(mockContext.teaRequest()).thenReturn(mockRequest);
    }

    /**
     * Test constructor with null credential provider.
     */
    @Test
    void testConstructorWithNullProvider() {
        assertThrows(IllegalArgumentException.class, () -> new BearerTokenRequestInterceptor(null));
    }

    /**
     * Test constructor with empty bearer token.
     */
    @Test
    void testConstructorWithEmptyBearerToken() {
        when(mockCredentialProvider.getBearerToken()).thenReturn("");
        assertThrows(IllegalArgumentException.class, () -> new BearerTokenRequestInterceptor(mockCredentialProvider));
    }

    /**
     * Test constructor with valid credential provider.
     */
    @Test
    void testConstructorWithValidProvider() {
        when(mockCredentialProvider.getBearerToken()).thenReturn("valid_token");
        BearerTokenRequestInterceptor interceptor = new BearerTokenRequestInterceptor(mockCredentialProvider);
        assertEquals(mockCredentialProvider, interceptor.getCredentialProvider());
    }

    /**
     * Test modifyRequest when headers are null.
     */
    @Test
    void testModifyRequestWithNullHeaders() {
        when(mockCredentialProvider.getBearerToken()).thenReturn("test_token");
        mockRequest.headers = null;

        BearerTokenRequestInterceptor interceptor = new BearerTokenRequestInterceptor(mockCredentialProvider);
        interceptor.modifyRequest(mockContext, AttributeMap.empty());

        // Verify headers were initialized and Authorization header was added
        assertNotNull(mockRequest.headers);
        assertTrue(mockRequest.headers.containsKey("Authorization"));
        assertEquals("Bearer test_token", mockRequest.headers.get("Authorization"));
    }

    /**
     * Test modifyRequest when Authorization header does not exist.
     */
    @Test
    void testModifyRequestWithoutAuthorizationHeader() {
        Map<String, String> headers = new HashMap<>();
        when(mockCredentialProvider.getBearerToken()).thenReturn("test_token");
        mockRequest.headers = headers;

        BearerTokenRequestInterceptor interceptor = new BearerTokenRequestInterceptor(mockCredentialProvider);
        interceptor.modifyRequest(mockContext, AttributeMap.empty());

        // Verify Authorization header was added
        assertTrue(headers.containsKey("Authorization"));
        assertEquals("Bearer test_token", headers.get("Authorization"));
    }

    /**
     * Test modifyRequest when Authorization header already exists.
     */
    @Test
    void testModifyRequestWithExistingAuthorizationHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer existing_token");
        when(mockCredentialProvider.getBearerToken()).thenReturn("test_token");
        mockRequest.headers = headers;

        BearerTokenRequestInterceptor interceptor = new BearerTokenRequestInterceptor(mockCredentialProvider);
        interceptor.modifyRequest(mockContext, AttributeMap.empty());

        // Verify Authorization header was not modified
        assertEquals("Bearer existing_token", headers.get("Authorization"));
    }
}
