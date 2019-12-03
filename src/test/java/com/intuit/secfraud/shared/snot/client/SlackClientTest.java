package com.intuit.secfraud.shared.snot.client;

import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.intuit.secfraud.shared.snot.BaseUnitTest;
import com.intuit.secfraud.shared.snot.SnotRocket;
import com.intuit.secfraud.shared.snot.config.SnotProperties;

public class SlackClientTest extends BaseUnitTest {

    private static final String MOCK_OAUTH_TOKEN = "mockOauthToken";

    private static final String MOCK_SLACK_APP_URL = "http://mock.com";

    private SnotRocket slackRequestObject;
    private HttpEntity<MultiValueMap<String, Object>> expectedRequest;

    @Mock
    private RestTemplate mockRestTemplate;

    @Mock
    private SnotProperties mockSnotProperties;

    @InjectMocks
    private SlackClient slackClient;

    @BeforeEach
    public void before() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File templateFile = new File(classLoader.getResource("slack/general.json")
                .getFile());
        String templateJson = FileUtils.readFileToString(templateFile, StandardCharsets.UTF_8);
        slackRequestObject = new SnotRocket(templateJson);
        slackRequestObject.addVariable("customKey", "custom Value");
        expectedRequest = createExpectedHttpRequest(slackRequestObject);
    }

    @Test
    public void postSlackMessage() {
        when(mockSnotProperties.getSlackAppOauthToken()).thenReturn(MOCK_OAUTH_TOKEN);
        when(mockSnotProperties.getSlackAppUrl()).thenReturn(MOCK_SLACK_APP_URL);

        when(mockRestTemplate.postForLocation(MOCK_SLACK_APP_URL, expectedRequest)).thenReturn(null);
        assertTrue(slackClient.postSlackMessage(slackRequestObject));
    }

    @Test
    public void postSlackMessage_RuntimeException() throws Exception {
        when(mockSnotProperties.getSlackAppOauthToken()).thenReturn(MOCK_OAUTH_TOKEN);
        when(mockSnotProperties.getSlackAppUrl()).thenReturn(MOCK_SLACK_APP_URL);

        when(mockRestTemplate.postForLocation(MOCK_SLACK_APP_URL, expectedRequest)).thenThrow(new RuntimeException());
        assertFalse(slackClient.postSlackMessage(slackRequestObject));
        verify(mockRestTemplate).postForLocation(MOCK_SLACK_APP_URL, expectedRequest);
    }

    private HttpEntity<MultiValueMap<String, Object>> createExpectedHttpRequest(SnotRocket request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("token", MOCK_OAUTH_TOKEN);
        params.add("channel", request.getTarget());
        params.add("attachments", request.getAttachmentsJson());
        params.add("as_user", "false");
        return new HttpEntity<>(params, headers);
    }

}
