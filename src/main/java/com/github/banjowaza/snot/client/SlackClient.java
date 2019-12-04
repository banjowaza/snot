package com.github.banjowaza.snot.client;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.github.banjowaza.snot.SnotRocket;
import com.github.banjowaza.snot.config.SnotProperties;

/**
 * The Slack Client
 *
 */
@Service
public class SlackClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlackClient.class); //NOSONAR

    @Resource
    SnotProperties snotProperties;

    @Resource
    private RestTemplate restTemplate;

    /**
     * sends the request to Slack
     * @param request
     * @return
     */
    public boolean postSlackMessage(SnotRocket request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("token", snotProperties.getSlackAppOauthToken());
        params.add("channel", request.getTarget());
        params.add("attachments", request.getAttachmentsJson());
        params.add("as_user", "false");
        HttpEntity<MultiValueMap<String, Object>> httpRequest = new HttpEntity<>(params, headers);
        try {
            restTemplate.postForLocation(snotProperties.getSlackAppUrl(), httpRequest);
            LOGGER.debug("Slack Message Sent Success");
            return true;
        } catch (RuntimeException e) {
            LOGGER.error("Failed to send Slack message", e);
            return false;
        }
    }
}
