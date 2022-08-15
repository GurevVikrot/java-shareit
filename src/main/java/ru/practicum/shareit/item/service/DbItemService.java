package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exeption.StorageException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBookings;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.requests.storage.RequestsRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.util.OptionalTaker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
public class DbItemService implements ItemService {
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final RequestsRepository requestsRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Autowired
    public DbItemService(ItemMapper itemMapper,
                         ItemRepository itemRepository,
                         RequestsRepository requestsRepository,
                         UserRepository userRepository,
                         BookingRepository bookingRepository,
                         BookingMapper bookingMapper,
                         CommentRepository commentRepository,
                         CommentMapper commentMapper) {
        this.itemMapper = itemMapper;
        this.itemRepository = itemRepository;
        this.requestsRepository = requestsRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        if (!checkId(itemDto)) {
            throw new StorageException("Невозможно создать вещь, неверный формат id");
        }

        // Если вещь добавляется по запросу другого пользователя, проверяем существует ли он
        if (itemDto.getRequest() != null) {
            if (!requestsRepository.existsById(itemDto.getRequest().getRequestId())) {
                throw new StorageException("Запроса на вещь не существует, попробуйте создание без привязки к запросу");
            }
        }

        Item item = itemMapper.toItem(itemDto);
        item.setOwner(getUserFromDb(userId));

        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long id, long userId) {
        itemDto.setId(id);

        if (checkId(itemDto)) {
            throw new StorageException("Невозможно обновить вещь, ее не существует");
        }

        Item itemToUpdate = itemMapper.toItem(itemDto);
        Item itemFromBd = OptionalTaker.getItem(itemRepository.findById(id));

        if (itemFromBd.getOwner().getId() != userId) {
            throw new StorageException("У пользователя не найдено обновляемой вещи");
        }

        itemToUpdate.setOwner(getUserFromDb(userId));

        if (itemToUpdate.getName() == null) {
            itemToUpdate.setName(itemFromBd.getName());
        }

        if (itemToUpdate.getDescription() == null) {
            itemToUpdate.setDescription(itemFromBd.getDescription());
        }

        if (itemToUpdate.getAvailable() == null) {
            itemToUpdate.setAvailable(itemFromBd.getAvailable());
        }

        return itemMapper.toItemDto(itemRepository.save(itemToUpdate));
    }

    @Override
    public ItemDtoBookings getItem(long id, long userId) {
        Item item = OptionalTaker.getItem(itemRepository.findById(id));
        ItemDtoBookings itemBookings = itemMapper.toItemBookingDto(item);

        if (item.getOwner().getId() == userId) {
            itemBookings.setLastBooking(getLastBooking(id));
            itemBookings.setNextBooking(getNextBooking(id));
        }

        itemBookings.setComments(getCommentsDtoList(id));

        return itemBookings;
    }

    @Override
    public List<ItemDtoBookings> getAllUserItems(long userId) {
        return itemRepository.findAllByOwner_Id(userId).stream()
                .map(itemMapper::toItemBookingDto)
                .peek(item -> item.setLastBooking(getLastBooking(item.getId())))
                .peek(item -> item.setNextBooking(getNextBooking(item.getId())))
                .peek(item -> item.setComments(getCommentsDtoList(item.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String name) {
        if (name.isEmpty()) {
            return new ArrayList<>();
        }

        return itemRepository.find(name.trim().toLowerCase()).stream()
                .filter(Item::getAvailable)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(long itemId, long userId, String text) {
        if (text == null || text.isEmpty()) {
            throw new ValidationException("Текст отзыва не должен быть пустым");
        } else if (!itemRepository.existsById(itemId)) {
            throw new StorageException("Вещи не существует");
        } else if (!userRepository.existsById(userId)) {
            throw new StorageException("Пользователя не существует");
        }

        Booking booking = OptionalTaker.getBooking(bookingRepository.findLastBooker_IdAndItem_Id(userId, itemId));

        if (booking.getEnd().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Можно оставить комментарий только к завершенному бронированию");
        }

        Comment comment = new Comment(0, text.trim(), booking.getBooker(), booking.getItem(), LocalDateTime.now());

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    private boolean checkId(ItemDto itemDto) {
        return itemDto.getId() == 0 && !itemRepository.existsById(itemDto.getId());
    }

    private User getUserFromDb(long id) {
        if (!userRepository.existsById(id)) {
            throw new StorageException("Пользователя не существует, невозможно добавить вещь");
        }
        return userRepository.findById(id).orElseThrow(
                () -> new StorageException("Ошибка получения пользователя из БД"));
    }

    private BookingDto getLastBooking(long itemId) {
        Booking booking = bookingRepository.getLastBookingForItem(itemId);

        if (booking == null) {
            return null;
        }

        return bookingMapper.toItemBooking(booking);
    }

    private BookingDto getNextBooking(long itemId) {
        Booking booking = bookingRepository.getNextBookingForItem(itemId);

        if (booking == null) {
            return null;
        }

        return bookingMapper.toItemBooking(booking);
    }

    private List<CommentDto> getCommentsDtoList(long itemId) {
        List<Comment> comments = commentRepository.findAllByItem_Id(itemId);

        if (comments != null && !comments.isEmpty()) {
            return comments.stream()
                    .map(commentMapper::toCommentDto)
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}