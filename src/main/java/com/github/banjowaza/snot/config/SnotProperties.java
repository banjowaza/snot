package com.github.banjowaza.snot.config;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

public class SnotProperties {

    @Value("${spring.profiles.active:}")
    private String defaultEnv;

    private String slackAppOauthToken;

    private String slackAppUrl;

    private String appName;

    private String environment;

    private String appLink;
    
    private boolean debug;
    
    private boolean blowOnException;

    private List<String> slackDefaultTargets;

    public final static String DEFAULT_SLACK_APP_URL = "https://slack.com/api/chat.postMessage";

    public String getSlackAppOauthToken() {
        return slackAppOauthToken;
    }

    public void setSlackAppOauthToken(String slackAppOauthToken) {
        this.slackAppOauthToken = slackAppOauthToken;
    }

    public String getSlackAppUrl() {
        return StringUtils.isNotBlank(slackAppUrl) ? slackAppUrl : DEFAULT_SLACK_APP_URL;
    }

    public void setSlackAppUrl(String slackAppUrl) {
        this.slackAppUrl = slackAppUrl;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getDefaultEnv() {
        return defaultEnv;
    }

    public void setDefaultEnv(String defaultEnv) {
        this.defaultEnv = defaultEnv;
    }

    public String getAppLink() {
        return appLink;
    }

    public void setAppLink(String appLink) {
        this.appLink = appLink;
    }

    public List<String> getSlackDefaultTargets() {
        return slackDefaultTargets;
    }

    public void setSlackDefaultTargets(List<String> slackDefaultTargets) {
        this.slackDefaultTargets = slackDefaultTargets;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public static String getDefaultSlackAppUrl() {
        return DEFAULT_SLACK_APP_URL;
    }

    public boolean isBlowOnException() {
        return blowOnException;
    }

    public void setBlowOnException(boolean blowOnException) {
        this.blowOnException = blowOnException;
    }

}
