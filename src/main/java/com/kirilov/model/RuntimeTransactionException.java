package com.kirilov.model;

public class RuntimeTransactionException extends RuntimeException {

    public RuntimeTransactionException() {
        super();
    }

    public RuntimeTransactionException(String message) {
        super(message);
    }

    public RuntimeTransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeTransactionException(Throwable cause) {
        super(cause);
    }

    protected RuntimeTransactionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
