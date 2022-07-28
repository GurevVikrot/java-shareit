package ru.practicum.shareit.util;

public class ConflictException extends StorageException {
    public ConflictException(String massage) {
        super(massage);
    }
}
