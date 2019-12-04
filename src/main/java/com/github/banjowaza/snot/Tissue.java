package com.github.banjowaza.snot;

/**
 * Predefined Slack Attachment templates
 *
 */
public enum Tissue {

    GENERAL("general.json"),
    BLOW("exception.json");

    private String template;

    Tissue(final String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

}
