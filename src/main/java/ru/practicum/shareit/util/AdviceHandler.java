package ru.practicum.shareit.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class AdviceHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> validationException(ValidationException exp) {
        log.warn(exp.getMessage());
        return new ResponseEntity<>(exp.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<String> storageException(StorageException exp) {
        log.warn(exp.getMessage());
        return new ResponseEntity<>(exp.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<String> storageException(ConflictException exp) {
        log.warn(exp.getMessage());
        return new ResponseEntity<>(exp.getMessage(), HttpStatus.CONFLICT);
    }
}
