package ru.practicum.shareit.util;

public class ValidationException extends IllegalArgumentException {
    public ValidationException(String massage) {
        super(massage);
    }
}
