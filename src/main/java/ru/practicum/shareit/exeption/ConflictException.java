package ru.practicum.shareit.exeption;

public class ConflictException extends StorageException {
    public ConflictException(String massage) {
        super(massage);
    }
}
