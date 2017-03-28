package com.kirilov.model;

public class LevelpTransactionException extends RuntimeException {

    public LevelpTransactionException() {
        super();
    }

    public LevelpTransactionException(String message) {
        super(message);
    }

    public LevelpTransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public LevelpTransactionException(Throwable cause) {
        super(cause);
    }

    protected LevelpTransactionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
