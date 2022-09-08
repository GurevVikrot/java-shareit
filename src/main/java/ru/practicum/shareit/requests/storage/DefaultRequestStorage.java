package ru.practicum.shareit.requests.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.HashMap;
import java.util.Map;

@Repository
public class DefaultRequestStorage implements RequestStorage {
    private final Map<Long, ItemRequest> storage = new HashMap<>();

    @Override
    public boolean requestExist(long id) {
        return storage.containsKey(id);
    }
}
