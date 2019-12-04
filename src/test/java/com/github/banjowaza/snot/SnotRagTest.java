package com.github.banjowaza.snot;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;

import com.github.banjowaza.snot.SnotRag;

/** Generic handler for the SlackClient that reads the response body to check for errors */
public class SnotRagTest extends BaseUnitTest {

    private static final String TEMPORARY_HEADER = "X-internal-payload";

    @InjectMocks
    private SnotRag handler;

    @Mock
    private ClientHttpResponse mockClientHttpResponse;

    private HttpHeaders mockHeaders = new HttpHeaders();

    @Mock
    private Set<String> mockSet;

    @Test
    public void hasError_false() throws Exception {
        String json = "{\"ok\":true}";
        when(mockClientHttpResponse.getBody())
                .thenReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));

        assertFalse(handler.hasError(mockClientHttpResponse));
    }

    @Test
    public void hasError_true() throws Exception {
        String json = "{\"ok\":false}";
        when(mockClientHttpResponse.getBody())
                .thenReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
        mockHeaders.add(TEMPORARY_HEADER, json);
        when(mockClientHttpResponse.getHeaders()).thenReturn(mockHeaders);

        assertTrue(handler.hasError(mockClientHttpResponse));
    }

    @Test
    public void hasError_badResponse1() throws Exception {
        String json = "{\"ok\":\"maybe\"}";
        when(mockClientHttpResponse.getBody())
                .thenReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
        when(mockClientHttpResponse.getHeaders()).thenReturn(mockHeaders);
        mockHeaders.add(TEMPORARY_HEADER, json);

        assertTrue(handler.hasError(mockClientHttpResponse));
    }

    @Test
    public void hasError_badResponse2() throws Exception {
        String json = "{\"error\":false}";
        when(mockClientHttpResponse.getBody())
                .thenReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
        when(mockClientHttpResponse.getHeaders()).thenReturn(mockHeaders);
        mockHeaders.add(TEMPORARY_HEADER, json);

        assertTrue(handler.hasError(mockClientHttpResponse));
    }

    @Test
    public void hasError_invalid() throws Exception {
        String json = "{invalid}";
        when(mockClientHttpResponse.getBody())
                .thenReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
        when(mockClientHttpResponse.getHeaders()).thenReturn(mockHeaders);
        mockHeaders.add(TEMPORARY_HEADER, json);

        assertTrue(handler.hasError(mockClientHttpResponse));
    }

    @Test()
    public void handleError() throws Exception {
        when(mockClientHttpResponse.getHeaders()).thenReturn(mockHeaders);
        mockHeaders.add(TEMPORARY_HEADER, "errorJson");

        assertThrows(RuntimeException.class, () -> handler.handleError(mockClientHttpResponse));

    }
}
