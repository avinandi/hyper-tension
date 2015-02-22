package com.hypertension.runner.exception;

/**
 * @Author Avirup
 */
public class TaskQueueFullException extends RuntimeException {
    public TaskQueueFullException(final String message) {
        super(message);
    }

    public TaskQueueFullException(final String message, Throwable throwable) {
        super(message, throwable);
    }
}
