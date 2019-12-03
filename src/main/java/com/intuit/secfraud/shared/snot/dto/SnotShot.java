package com.intuit.secfraud.shared.snot.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Embodies message data for SlackClient body and template
 *
 */
public interface SnotShot {

    String getText();

    default String getPretext() {
        return null;
    }

    default String getFallback() {
        return null;
    }

    default Map<String, Object> getVariables() {
        return new HashMap<String, Object>();
    }
}
