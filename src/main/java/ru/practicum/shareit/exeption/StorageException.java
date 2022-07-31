package ru.practicum.shareit.exeption;

public class StorageException extends RuntimeException {
    public StorageException(String massage) {
        super(massage);
    }
}
