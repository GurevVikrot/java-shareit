package ru.practicum.shareit.requests.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;

public interface RequestsRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequester_IdIs(long userId, Sort sort);

    List<ItemRequest> findAllByRequester_IdNot(long userId, Pageable pageable);
}
