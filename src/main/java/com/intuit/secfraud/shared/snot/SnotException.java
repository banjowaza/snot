package com.intuit.secfraud.shared.snot;

/**
 * Generalized Snot Runtime Exception
 *
 */
public class SnotException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SnotException() {
        super();
    }

    public SnotException(String message, Throwable cause) {
        super(message, cause);
    }

    public SnotException(String message) {
        super(message);
    }

    public SnotException(Throwable cause) {
        super(cause);
    }

}
