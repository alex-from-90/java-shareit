package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
@SuppressWarnings("unused")
public class ExceptionsHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> constraintException(final MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException ", e);
        String error = "Unknown " + e.getName() + ": " + e.getValue();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(error));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> badRequestException(final Exception e) {
        log.error("badRequestException ", e);
        String error = "Bad request";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(error));
    }
}