package com.github.banjowaza.snot;

import org.json.JSONObject;

/**
 * A wrapper class to hold JSONObject and target of Slack Message
 *
 */
public class SnotRocket {
    private String target; // eg. "#channel", "@user"
    private JSONObject attachment;

    public SnotRocket(String slackTemplate) {
        target = "";
        attachment = new JSONObject(slackTemplate);
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTarget() {
        return target;
    }

    public void setFallback(String previewText) {
        attachment.put("fallback", previewText);
    }

    public void setColor(String color) {
        attachment.put("color", color);
    }

    public void setPretext(String summary) {
        attachment.put("pretext", summary);
    }

    public void setText(String data) {
        attachment.put("text", data);
    }

    public void setFooter(String footerData) {
        attachment.put("footer", footerData);
    }

    public void setAuthorLink(String link) {
        attachment.put("author_link", link);
    }

    public void setAuthorName(String name) {
        attachment.put("author_name", name);
    }

    public String getAttachmentsJson() {
        return "[" + this.attachment.toString() + "]";  //return as array
    }

    public void addVariable(String key, String value) {
        attachment.put(key, value);
    }

}
