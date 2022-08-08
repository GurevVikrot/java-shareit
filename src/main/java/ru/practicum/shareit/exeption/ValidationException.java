package ru.practicum.shareit.exeption;

public class ValidationException extends IllegalArgumentException {
    public ValidationException(String massage) {
        super(massage);
    }
}
