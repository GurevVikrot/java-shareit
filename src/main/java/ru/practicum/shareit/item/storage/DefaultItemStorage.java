package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeption.StorageException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class DefaultItemStorage implements ItemStorage {
    private final Map<Long, Item> storage = new HashMap<>();
    private long counterId = 1;

    private long getCounterId() {
        return counterId++;
    }

    @Override
    public Optional<Item> save(Item item) {
        item.setId(getCounterId());
        storage.put(item.getId(), item);
        return Optional.of(item);
    }

    @Override
    public Optional<Item> update(Item item) {
        if (!Objects.equals(storage.get(item.getId()).getOwner().getId(), item.getOwner().getId())) {
            throw new StorageException("Невозможно обновить вещь другого пользователя");
        }

        Item itemToUpdate = storage.get(item.getId());

        if (itemToUpdate != null) {
            if (item.getName() != null) {
                itemToUpdate.setName(item.getName());
            }
            if (item.getDescription() != null) {
                itemToUpdate.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                itemToUpdate.setAvailable(item.getAvailable());
            }
        }

        return Optional.ofNullable(storage.get(item.getId()));
    }

    @Override
    public Optional<Item> get(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Item> find(String name) {
        List<Item> searchResult = new ArrayList<>();

        for (Item item : storage.values()) {
            if (item.getName().toLowerCase().contains(name)
                    || item.getDescription().toLowerCase().contains(name)) {
                searchResult.add(item);
            }
        }

        return searchResult;
    }

    @Override
    public boolean delete(long id) {
        return storage.remove(id) != null;
    }

    @Override
    public boolean itemExist(long id) {
        return storage.containsKey(id);
    }
}
