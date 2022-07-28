package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<User> save(User user);

    Optional<User> update(User user);

    Optional<User> get(long id);

    boolean delete(long id);

    boolean userExist(long id);

    List<User> getAll();
}
