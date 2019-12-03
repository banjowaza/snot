package com.intuit.secfraud.shared.snot.dto;

/**
 * A Simple SnotShot
 *
 */
public class ShortShot implements SnotShot {

    private String text;

    public ShortShot(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return this.text;
    }

}
