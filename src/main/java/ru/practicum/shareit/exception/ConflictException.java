package ru.practicum.shareit.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
@NoArgsConstructor
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}

