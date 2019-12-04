package com.github.banjowaza.snot;

import com.github.banjowaza.snot.client.SlackClient;
import com.github.banjowaza.snot.config.SnotProperties;
import com.github.banjowaza.snot.dto.SnotShot;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.collections4.CollectionUtils;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * Snot Service to interact with Slack Client
 *
 */
@Service
public class SnotService {
    private static final String SNOT_FOOTER_MESSAGE = "-- from your friendly neighborhood snot app --";
    private static final Logger LOGGER = LoggerFactory.getLogger(SnotService.class);

    @Resource
    private SlackClient slackClient;

    @Resource
    private SnotProperties snotProperties;

    @Resource
    private SnotTemplates templates;

    /**
     * Color applied to SnotTemplates @see SnotTemplates and Tissue @see Tissue
     *
     */
    public enum COLOR {
        GREEN("good"),
        YELLOW("warning"),
        RED("danger"),
        NONE("none");

        private String color;

        COLOR(String color) {
            this.color = color;
        }

        public String getColor() {
            return this.color;
        }
    }

    /**
     * sends a SnotShot message to SlackClient
     * 
     * @param message
     * @param status
     *            the Slack status @see SnotService#COLOR
     * @param targets
     *            optional list of target channel or users to send messages to. If not provided, default targets are
     *            used @see SnotProperties#slackDefaultTargets
     * @return true if messages are sent successfully to all targets, false if not
     * @throws SnotException
     *             if no targets are provided or defined in defaults @see SnotProperties#slackDefaultTargets
     */
    public boolean sneeze(SnotShot message, COLOR status, List<String> targets, Tissue tissue) {
        List<String> targetList = getTargets(targets);
        SnotRocket request = createSlackRequest(message, status, tissue);
        boolean sentStatus = true;
        for (String target : targetList) {
            request.setTarget(target);
            LOGGER.debug("posting Slack message {} to channel {}", message.getText(), status, target);
            sentStatus &= slackClient.postSlackMessage(request);
        }
        return sentStatus;
    }


    /**
     * uses the provided targets, or default targets if not provided.
     * 
     * @param targets
     * @return the list of targets to send the message to
     * @throws SnotException
     *             if no targets are provided or defined in defaults @see SnotProperties#slackDefaultTargets
     */
    List<String> getTargets(List<String> targets) {
        if (!CollectionUtils.emptyIfNull(targets)
                .isEmpty()) {
            return targets;
        } else if (!CollectionUtils.emptyIfNull(snotProperties.getSlackDefaultTargets())
                .isEmpty()) {
            return snotProperties.getSlackDefaultTargets();
        } else {
            throw new SnotException("No targets defined on sneeze");
        }
    }

    /**
     * creates a SnotRocket from the given message, status and tissue
     * @param message
     * @param status
     * @param tissue
     * @return
     */
    SnotRocket createSlackRequest(SnotShot message, COLOR status, Tissue tissue) {
        LOGGER.debug("creating slack request");

        final String template = templates.getTemplate(tissue);
        SnotRocket slackRequest = new SnotRocket(template);
        // to support custom templates
        Iterator<Entry<String, Object>> entries = message.getVariables()
                .entrySet()
                .iterator();
        while (entries.hasNext()) {
            Entry<String, Object> entry = entries.next();
            slackRequest.addVariable(entry.getKey(), entry.getValue()
                    .toString());
        }

        slackRequest.getAttachmentsJson();

        slackRequest.setColor(status.getColor());
        slackRequest.setPretext(getPretextLine(message));
        slackRequest.setAuthorLink(snotProperties.getAppLink());
        slackRequest.setAuthorName(snotProperties.getAppName());
        slackRequest.setFallback(message.getFallback());
        slackRequest.setText(message.getText());
        slackRequest.setFooter(SNOT_FOOTER_MESSAGE);

        return slackRequest;
    }

    /**
     * returns the pretext line with applied environment, pretext and application name when available.
     * @param message
     * @return
     */
    String getPretextLine(SnotShot message) {
        StringBuilder builder = new StringBuilder();
        List<String> tokens = Lists.newArrayList();
        if (StringUtils.isNotBlank(getEnvironment())) {
            builder.append("*env:* _%s_ |");
            tokens.add(getEnvironment());
        }

        if (StringUtils.isNotBlank(snotProperties.getAppName())) {
            builder.append(" *app:* _%s_ |");
            tokens.add(snotProperties.getAppName());
        }

        if (StringUtils.isNotBlank(message.getPretext())) {
            builder.append(" _%s_");
            tokens.add(message.getPretext());
        }

        if (tokens.size() == 0) {
            return null;
        } else {
            if (builder.charAt(builder.length() - 1) == '|') {
                builder.deleteCharAt(builder.length() - 1);
            }
            return String.format(builder.toString()
                    .trim(), tokens.toArray());
        }
    }

    /**
     * returns the specified environment from properties if not null. Otherwise retrieves the default environment
     * properties which is loaded from spring active profiles.
     * 
     * @return
     */
    private String getEnvironment() {
        return snotProperties.getEnvironment() != null ? snotProperties.getEnvironment()
                : snotProperties.getDefaultEnv();
    }

}
