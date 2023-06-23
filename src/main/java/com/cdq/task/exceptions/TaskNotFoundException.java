package com.cdq.task.exceptions;

public class TaskNotFoundException extends AbstractTaskException {

    private static final String MESSAGE = "Task %d not found";

    public TaskNotFoundException(int id) {
        super(String.format(MESSAGE, id));
    }
}
