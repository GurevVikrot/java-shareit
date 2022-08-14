package ru.practicum.shareit.exeption.handling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.exeption.ConflictException;
import ru.practicum.shareit.exeption.StorageException;
import ru.practicum.shareit.exeption.ValidationException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class AdviceHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> validationException(ValidationException exp) {
        log.error(exp.getMessage());
        return new ResponseEntity<>(exp.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<String> storageException(StorageException exp) {
        log.error(exp.getMessage());
        return new ResponseEntity<>(exp.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<String> conflictException(ConflictException exp) {
        log.error(exp.getMessage());
        return new ResponseEntity<>(exp.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String,String>> errorHandler(){
        Map<String,String> resp = new HashMap<>();
        resp.put("error", "Unknown state: UNSUPPORTED_STATUS");
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }
}
