package com.github.banjowaza.snot.dto;

/**
 * A Larger SnotShot
 *
 */
public class LongShot extends ShortShot {

    private String pretext;
    private String fallback;

    public LongShot(String text, String pretext, String fallback) {
        this(text);
        this.pretext = pretext;
        this.fallback = fallback;
    }

    public LongShot(String message) {
        super(message);
    }

    public String getPretext() {
        return pretext;
    }

    public void setPretext(String pretext) {
        this.pretext = pretext;
    }

    public String getFallback() {
        return fallback;
    }

    public void setFallback(String fallback) {
        this.fallback = fallback;
    }

}
