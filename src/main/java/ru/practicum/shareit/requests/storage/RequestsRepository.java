package ru.practicum.shareit.requests.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.requests.ItemRequest;

public interface RequestsRepository extends JpaRepository<ItemRequest, Long> {
}
