package ru.practicum.shareit.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class AdviceHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> errorHandler() {
        Map<String, String> resp = new HashMap<>();
        resp.put("error", "Unknown state: UNSUPPORTED_STATUS");
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }
}
