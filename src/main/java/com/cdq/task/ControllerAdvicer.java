package com.cdq.task;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.cdq.task.exceptions.AbstractTaskException;
import com.cdq.task.exceptions.WrongLengthsException;

@RestControllerAdvice
public class ControllerAdvicer {

    private static final String ERROR = "error";

    @ExceptionHandler(WrongLengthsException.class)
    public ResponseEntity<Map<String, String>> handleWrongLengthsException(WrongLengthsException exception) {
        return getExceptionBody(exception);
    }

    @ExceptionHandler(AbstractTaskException.class)
    public ResponseEntity<Map<String, String>> handleCustomException(AbstractTaskException exception) {
        return getExceptionBody(exception);
    }

    private ResponseEntity<Map<String, String>> getExceptionBody(Throwable throwable) {
        Map<String, String> body = new HashMap<>();
        body.put(ERROR, throwable.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

}
