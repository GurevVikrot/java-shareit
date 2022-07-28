package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.ConflictException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class DefaultUserStorage implements UserStorage{
    private final Map<Long, User> storage = new HashMap<>();
    private Set<String> emails = new HashSet<>();
    private long counterId = 1;

    private long getCounterId() {
        return counterId++;
    }

    @Override
    public Optional<User> save(User user) {
        if (emailExist(user)) {
            throw new ConflictException("email занят другим пользователем");
        }
        user.setId(getCounterId());
        storage.put(user.getId(), user);
        emails.add(user.getEmail());
        return Optional.of(user);
    }

    @Override
    public Optional<User> update(User user) {
        if (emailExist(user)) {
            throw new ConflictException("email занят другим пользователем");
        }

        User userToUpdate = storage.get(user.getId());

        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }

        if (user.getEmail() != null) {
            emails.remove(storage.get(user.getId()).getEmail());
            userToUpdate.setEmail(user.getEmail());
        }

        return Optional.of(userToUpdate);
    }

    @Override
    public Optional<User> get(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public boolean delete(long id) {
        if (userExist(id)) {
            emails.remove(storage.get(id).getEmail());
            storage.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean userExist(long id) {
        return storage.containsKey(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(storage.values());
    }

    private boolean emailExist(User user) {
        return emails.contains(user.getEmail());
    }
}
