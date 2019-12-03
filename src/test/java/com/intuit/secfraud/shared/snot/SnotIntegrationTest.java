package com.intuit.secfraud.shared.snot;

import static junit.framework.TestCase.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Lists;
import com.intuit.secfraud.shared.snot.SnotService.COLOR;
import com.intuit.secfraud.shared.snot.client.SlackClient;
import com.intuit.secfraud.shared.snot.config.SnotConfiguration;
import com.intuit.secfraud.shared.snot.config.SnotProperties;
import com.intuit.secfraud.shared.snot.dto.LongShot;
import com.intuit.secfraud.shared.snot.dto.ShortShot;
import com.intuit.secfraud.shared.snot.dto.SnotShot;

@SpringBootTest(classes = { SlackClient.class, SnotConfiguration.class, SnotService.class, SnotTemplates.class })
@EnableConfigurationProperties()
@TestPropertySource(locations = {
        "classpath:application.properties",
        "classpath:slack/general.json",
        "classpath:slack/exception.json"})
public class SnotIntegrationTest extends BaseUnitTest {

    @Resource
    private SlackClient slackClient;

    @Resource
    private SnotProperties snotProperties;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private SnotTemplates snotTemplates;

    @Resource
    private SnotService snotSlackService;

    @Test
    public void testSnotConfiguration() {
        assertNotNull(snotProperties);
        assertNotNull(restTemplate);
        assertNotNull(slackClient);
        assertNotNull(snotTemplates);
        assertEquals("https://slack.com/api/chat.postMessage", snotProperties.getSlackAppUrl());
        assertEquals("xoxp-86879456164-365856799362-820573244391-76b96d09dcc44398c9d772ebb3e82400",
                snotProperties.getSlackAppOauthToken());
        assertEquals("https://sherlock-be-blue.qa.api.intuit.com/v2/api-docs", snotProperties.getAppLink());
        assertEquals("dev", snotProperties.getEnvironment());
        assertEquals("NARC", snotProperties.getAppName());
        assertEquals(Lists.newArrayList("demo-snot", "@gfuller1"), snotProperties.getSlackDefaultTargets());
    }

    @Test
    public void testEmptySlackAppUrl() {
        injectInto(snotProperties, "slackAppUrl", "test");
        assertEquals("test", snotProperties.getSlackAppUrl());

        injectInto(snotProperties, "slackAppUrl", "");
        assertEquals(SnotProperties.DEFAULT_SLACK_APP_URL, snotProperties.getSlackAppUrl());

        injectInto(snotProperties, "slackAppUrl", null);
        assertEquals(SnotProperties.DEFAULT_SLACK_APP_URL, snotProperties.getSlackAppUrl());
    }

    @ParameterizedTest
    @EnumSource(value = Tissue.class, names = {"GENERAL", "BLOW"})
    public void testSendMessage(Tissue tissue) {
        snotSlackService.sneeze(createSimpleMessage("test message from snot. all Green!"), COLOR.GREEN,
                Lists.newArrayList("oifp-dev-snot"), tissue);
    }

    @Test
    public void testSendMessage_warning() {
        snotSlackService.sneeze(createSimpleMessage("test message from snot. all Yellow"), COLOR.YELLOW,
                Lists.newArrayList("oifp-dev-snot"), Tissue.GENERAL);
    }

    @Test
    public void testSendMessage_error() {
        snotSlackService.sneeze(createSimpleMessage("test message from snot. all Red"), COLOR.RED,
                Lists.newArrayList("oifp-dev-snot"), Tissue.BLOW);
    }

    @Test
    public void testSendDetailedMessage_withPreText() {
        snotSlackService.sneeze(
                createDetailedMessage("main body of message", "ATTN: this is important!", "fallback text"),
                COLOR.GREEN, Lists.newArrayList("oifp-dev-snot"), Tissue.GENERAL);

    }

    @Test
    public void testSendDetailedMessage_withoutPreText() {
        snotSlackService.sneeze(createDetailedMessage("main body of message", null, "fallback text"),
                COLOR.GREEN, Lists.newArrayList("oifp-dev-snot", "demo-snot", "@jingram1"), Tissue.GENERAL);
    }

    @Test
    public void testSendDetailedMessage_withoutPreTextAndApp() {
        injectInto(snotProperties, "appName", null);

        snotSlackService.sneeze(createDetailedMessage("main body of message", null, "fallback text"),
                COLOR.GREEN, Lists.newArrayList("oifp-dev-snot"), Tissue.GENERAL);
    }

    @Test
    public void testSendDetailedMessage_withoutPreTextAppAndEnv() {
        injectInto(snotProperties, "appName", null);
        injectInto(snotProperties, "environment", null);
        snotSlackService.sneeze(createDetailedMessage("main body of message", null, "fallback text"),
                COLOR.GREEN, Lists.newArrayList("oifp-dev-snot"), Tissue.GENERAL);

    }

    private SnotShot createSimpleMessage(String text) {
        ShortShot simpleMessage = new ShortShot(text);
        return simpleMessage;
    }

    private SnotShot createDetailedMessage(String text, String pretext, String fallback) {
        LongShot message = new LongShot(text);
        message.setPretext(pretext);
        message.setFallback(fallback);

        return message;
    }

}
