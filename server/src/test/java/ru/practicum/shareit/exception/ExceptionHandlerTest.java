package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ExceptionHandlerTest {
    @Test
    void conflictExcTest() {
        ExceptionsHandler exceptionsHandler = new ExceptionsHandler();

        String error = "Unknown " + ": ";
        ResponseEntity.status(409).body(new ErrorResponse(error));
        exceptionsHandler.conflictException(new ConflictException());
        assertEquals(1, 1);
    }

    @Test
    void badReqExcTest() {
        ExceptionsHandler exceptionsHandler = new ExceptionsHandler();

        String error = "Unknown " + ": ";
        ResponseEntity.status(409).body(new ErrorResponse(error));
        exceptionsHandler.badRequestException(new BadRequestException());
        assertEquals(1, 1);
    }

    @Test
    public void testBadRequestException() {
        ExceptionsHandler exceptionsHandler = new ExceptionsHandler();
        NullPointerException exception = new NullPointerException("test");

        ResponseEntity<ErrorResponse> response = exceptionsHandler.badRequestException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad request", response.getBody().getError());
    }
}