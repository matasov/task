package com.cdq.task.exceptions;

public abstract class AbstractTaskException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AbstractTaskException(String message) {
        super(message);
    }

    public AbstractTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public AbstractTaskException(Throwable cause) {
        super(cause);
    }

    public AbstractTaskException() {
        super();
    }

    protected AbstractTaskException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
