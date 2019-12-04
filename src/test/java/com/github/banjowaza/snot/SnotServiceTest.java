package com.github.banjowaza.snot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.github.banjowaza.snot.SnotRocket;
import com.github.banjowaza.snot.SnotService;
import com.github.banjowaza.snot.SnotTemplates;
import com.github.banjowaza.snot.Tissue;
import com.github.banjowaza.snot.SnotService.COLOR;
import com.github.banjowaza.snot.client.SlackClient;
import com.github.banjowaza.snot.config.SnotProperties;
import com.github.banjowaza.snot.dto.LongShot;
import com.github.banjowaza.snot.dto.ShortShot;
import com.google.common.io.Resources;

public class SnotServiceTest extends BaseUnitTest {

    @Mock
    private SnotProperties mockSnotProperties;

    @Mock
    private SlackClient mockSlackClient;

    @Mock
    private SnotTemplates snotTemplates;

    @InjectMocks
    private SnotService snotSlackService;

    @BeforeEach
    public void beforeEach() throws IOException, URISyntaxException {
        URL templateFile = Resources.getResource("slack/general.json");
        String slackTemplate = IOUtils.toString(templateFile.toURI(), StandardCharsets.UTF_8);
        when(snotTemplates.getTemplate(any(Tissue.class))).thenReturn(slackTemplate);
    }

    @Test
    public void sendSlackMessage() {
        when(mockSlackClient.postSlackMessage(any())).thenReturn(true);
        LongShot snotShot = createDetailedMessage("body of message", "my pretext goes here", "fallback text");

        assertTrue(snotSlackService.sneeze(snotShot, COLOR.GREEN, Lists.newArrayList("channel"), Tissue.GENERAL));
        verify(mockSlackClient).postSlackMessage(any());
    }

    @Test
    public void sendSlackMessage_noTargetsWithDefaults() {
        when(mockSlackClient.postSlackMessage(any())).thenReturn(true);
        when(mockSnotProperties.getSlackDefaultTargets()).thenReturn(Lists.newArrayList("channel"));
        LongShot snotShot = createDetailedMessage("body of message", "my pretext goes here", "fallback text");

        assertTrue(snotSlackService.sneeze(snotShot, COLOR.GREEN, null, Tissue.GENERAL));
        verify(mockSlackClient).postSlackMessage(any());
    }

    @Test
    public void sendSlackMessage_noTargetsWithEmptyDefaults() {
        when(mockSlackClient.postSlackMessage(any())).thenReturn(true);
        when(mockSnotProperties.getSlackDefaultTargets()).thenReturn(Lists.newArrayList());
        LongShot snotShot = createDetailedMessage("body of message", "my pretext goes here", "fallback text");

        assertThrows(RuntimeException.class, () -> snotSlackService.sneeze(snotShot, COLOR.GREEN, null, Tissue.GENERAL));
    }

    @Test
    public void sendSlackMessage_nullTargetsDefined() {
        when(mockSlackClient.postSlackMessage(any())).thenReturn(true);
        LongShot snotShot = createDetailedMessage("body of message", "my pretext goes here", "fallback text");
        assertThrows(RuntimeException.class, () -> snotSlackService.sneeze(snotShot, COLOR.GREEN, null, Tissue.GENERAL));
    }

    @Test
    public void sendSlackMessage_emptyTargetsDefined() {
        when(mockSlackClient.postSlackMessage(any())).thenReturn(true);
        LongShot snotShot = createDetailedMessage("body of message", "my pretext goes here", "fallback text");
        assertThrows(RuntimeException.class,
                () -> snotSlackService.sneeze(snotShot, COLOR.GREEN, Lists.newArrayList(), Tissue.GENERAL));
    }

    @Test
    public void sendSlackMessage_error() {
        when(mockSlackClient.postSlackMessage(any())).thenReturn(false);
        LongShot snotShot = createDetailedMessage("body of message", "my pretext goes here", "fallback text");

        assertFalse(snotSlackService.sneeze(snotShot, COLOR.GREEN, Lists.newArrayList("channel"), Tissue.GENERAL));
        verify(mockSlackClient).postSlackMessage(any());
    }

    @Test
    public void createSlackRequest() {
        when(mockSnotProperties.getAppLink()).thenReturn("https://localhost.com");
        when(mockSnotProperties.getAppName()).thenReturn("NARC");
        LongShot snotShot = createDetailedMessage("body of message", "my pretext goes here", "fallback text");
        SnotRocket request = snotSlackService.createSlackRequest(snotShot, COLOR.GREEN, Tissue.GENERAL);
        assertNotNull(request);
        assertEquals(
                "[{\"author_name\":\"NARC\",\"author_link\":\"https://localhost.com\",\"color\":\"good\",\"footer\":\"-- from your friendly neighborhood snot app --\",\"mrkdwn_in\":[\"text\",\"pretext\"],\"pretext\":\"*app:* _NARC_ | _my pretext goes here_\",\"text\":\"body of message\",\"fallback\":\"fallback text\"}]",
                request.getAttachmentsJson());

    }

    @Test
    public void getPretextLine_noApp_noEnv_noPretext() {
        assertNull(snotSlackService.getPretextLine(new ShortShot("body of message")));
    }

    @Test
    public void getPretextLine_noEnv_noPretext() {
        when(mockSnotProperties.getAppName()).thenReturn("app name");
        assertEquals("*app:* _app name_", snotSlackService.getPretextLine(new ShortShot("body of message")));
    }

    @Test
    public void getPretextLine_noApp_noPretext() {
        when(mockSnotProperties.getEnvironment()).thenReturn("dev");
        assertEquals("*env:* _dev_", snotSlackService.getPretextLine(new ShortShot("body of message")));
    }

    @Test
    public void getPretextLine_noPretext() {
        when(mockSnotProperties.getEnvironment()).thenReturn("dev");
        when(mockSnotProperties.getAppName()).thenReturn("app name");

        assertEquals("*env:* _dev_ | *app:* _app name_", snotSlackService.getPretextLine(new ShortShot("body of message")));
    }

    @Test
    public void getPretextLine_withAppEnvPretext() {
        when(mockSnotProperties.getEnvironment()).thenReturn("dev");
        when(mockSnotProperties.getAppName()).thenReturn("app name");
        LongShot snotShot = createDetailedMessage("body of message", "my pretext goes here", null);

        assertEquals("*env:* _dev_ | *app:* _app name_ | _my pretext goes here_", snotSlackService.getPretextLine(snotShot));
    }

    private LongShot createDetailedMessage(String text, String pretext, String fallback) {
        LongShot snotShot = new LongShot(text);
        snotShot.setPretext(pretext);
        snotShot.setFallback(fallback);
        return snotShot;
    }
}
