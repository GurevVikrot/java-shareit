package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Optional<Item> save(Item item);

    Optional<Item> update(Item item);

    Optional<Item> get(long id);

    List<Item> getAll();

    List<Item> find(String name);

    boolean delete(long id);

    boolean itemExist(long id);
}
