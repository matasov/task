package com.cdq.task.exceptions;

public class WrongLengthsException extends AbstractTaskException {

    public WrongLengthsException(String message) {
        super(message);
    }

    public WrongLengthsException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongLengthsException(Throwable cause) {
        super(cause);
    }

    public WrongLengthsException() {
        super();
    }

    protected WrongLengthsException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
