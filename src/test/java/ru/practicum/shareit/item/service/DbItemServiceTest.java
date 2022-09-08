package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;
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
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.storage.RequestsRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DbItemServiceTest {
    private final BookingDto lastBookingDto = new BookingDto(1,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1), 1L, BookingStatus.APPROVED);
    private final BookingDto nextBookingDto = new BookingDto(2,
            LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(3), 2L, BookingStatus.APPROVED);
    private final CommentDto comment = new CommentDto(1, "Балдеж", "Vova", lastBookingDto.getEnd());
    @InjectMocks
    private DbItemService itemService;
    @Mock
    private ItemMapper mokItemMapper;
    @Mock
    private ItemRepository mokItemRepository;
    @Mock
    private RequestsRepository mokRequestsRepository;
    @Mock
    private UserRepository mokUserRepository;
    @Mock
    private BookingRepository mokBookingRepository;
    @Mock
    private BookingMapper mokBookingMapper;
    @Mock
    private CommentRepository mokCommentRepository;
    @Mock
    private CommentMapper mokCommentMapper;
    private ItemDto itemDto;
    private ItemDto itemDto1;
    private ItemDtoBookings itemDtoBookings;
    private Item item;
    private Item item1;
    private final User user = new User(1L, "Vitya", "vitya@mail.ru");
    private final ItemRequest itemRequest = new ItemRequest(1L, "Хочется", null, null, null);

    @BeforeEach
    void beforeEach() {
        item = new Item(0L, "Вещь", "Супер", true, null, null);
        itemDto = new ItemDto(0L, "Вещь", "Супер", true, null);
        item1 = new Item(1L, "Вещь", "Супер", true, user, itemRequest);
        itemDto1 = new ItemDto(item1.getId(),
                item1.getName(),
                item1.getDescription(),
                item1.getAvailable(),
                1L);
        itemDtoBookings = new ItemDtoBookings(item1.getId(),
                item1.getName(),
                item1.getDescription(),
                item1.getAvailable(),
                item1.getRequest(), null, null, List.of());
    }

    @Test
    void createItemTest() {
        Mockito
                .when(mokItemRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);
        Mockito
                .when(mokUserRepository.existsById(user.getId()))
                .thenReturn(true);
        Mockito
                .when(mokUserRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mokRequestsRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.of(itemRequest));

        Mockito
                .when(mokItemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item1);
        Mockito
                .when(mokItemMapper.toItem(itemDto))
                .thenReturn(item);

        Mockito
                .when(mokItemMapper.toItemDto(item1))
                .thenReturn(itemDto1);

        item.setRequest(itemRequest);
        itemDto.setRequestId(1L);

        assertEquals(itemDto1, itemService.createItem(itemDto, 1L));
        Mockito.verify(mokItemMapper, Mockito.times(1))
                .toItem(itemDto);
        Mockito.verify(mokItemMapper, Mockito.times(1))
                .toItemDto(item1);
        Mockito.verify(mokUserRepository, Mockito.times(1))
                .findById(1L);
        Mockito.verify(mokRequestsRepository, Mockito.times(1))
                .findById(1L);
        Mockito.verify(mokItemRepository, Mockito.times(1))
                .save(Mockito.any(Item.class));
    }

    @Test
    void itemCreateWithIncorrectId() {
        itemDto.setId(1L);
        assertThrows(StorageException.class, () -> itemService.createItem(itemDto, 1L));

        Mockito
                .when(mokItemRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        itemDto.setId(0L);
        assertThrows(StorageException.class, () -> itemService.createItem(itemDto, 1L));
    }

    @Test
    void itemCreateWithNonexistentUser() {
        Mockito
                .when(mokUserRepository.existsById(user.getId()))
                .thenReturn(false);
        assertThrows(StorageException.class, () -> itemService.createItem(itemDto, 1L));
    }

    @Test
    void itemCreateWhenRequestNull() {
        Mockito
                .when(mokItemRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);
        Mockito
                .when(mokUserRepository.existsById(user.getId()))
                .thenReturn(true);
        Mockito
                .when(mokUserRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mokItemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item1);
        Mockito
                .when(mokItemMapper.toItem(itemDto))
                .thenReturn(item);

        Mockito
                .when(mokItemMapper.toItemDto(item1))
                .thenReturn(itemDto1);

        itemDto1.setRequestId(null);

        assertEquals(itemDto1, itemService.createItem(itemDto, 1L));
        Mockito.verify(mokItemMapper, Mockito.times(1))
                .toItem(itemDto);
        Mockito.verify(mokItemMapper, Mockito.times(1))
                .toItemDto(item1);
        Mockito.verify(mokUserRepository, Mockito.times(1))
                .findById(1L);
        Mockito.verify(mokRequestsRepository, Mockito.never())
                .findById(Mockito.anyLong());
        Mockito.verify(mokItemRepository, Mockito.times(1))
                .save(Mockito.any(Item.class));
    }

    @Test
    void itemCreateWithNonexistentRequest() {
        Mockito
                .when(mokItemRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        Mockito
                .when(mokUserRepository.existsById(user.getId()))
                .thenReturn(true);
        Mockito
                .when(mokUserRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(mokItemMapper.toItem(itemDto))
                .thenReturn(item);

        Mockito
                .when(mokRequestsRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        itemDto.setRequestId(2L);
        assertThrows(StorageException.class, () -> itemService.createItem(itemDto, 1L));
        Mockito.verify(mokRequestsRepository, Mockito.times(1))
                .findById(2L);
    }

    @Test
    void updateItemTest() {
        Mockito
                .when(mokItemRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokItemMapper.toItem(Mockito.any(ItemDto.class)))
                .thenReturn(item);

        Mockito
                .when(mokItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));

        Mockito
                .when(mokUserRepository.existsById(user.getId()))
                .thenReturn(true);

        Mockito
                .when(mokUserRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        Mockito.when(mokItemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item1);

        Mockito
                .when(mokItemMapper.toItemDto(Mockito.any(Item.class)))
                .thenReturn(itemDto1);

        assertEquals(itemDto1, itemService.updateItem(itemDto, 1L, user.getId()));
        Mockito.verify(mokItemMapper, Mockito.times(1))
                .toItem(itemDto);
        Mockito.verify(mokItemMapper, Mockito.times(1))
                .toItemDto(item1);
        Mockito.verify(mokUserRepository, Mockito.times(1))
                .findById(1L);
        Mockito.verify(mokRequestsRepository, Mockito.never())
                .findById(Mockito.anyLong());
        Mockito.verify(mokItemRepository, Mockito.times(1))
                .save(Mockito.any(Item.class));
    }

    @Test
    void updateWhenItemNonexistent() {
        Mockito
                .when(mokItemRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        assertThrows(StorageException.class, () -> itemService.updateItem(itemDto, 1L, 1L));

        Mockito
                .when(mokItemRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokItemRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(StorageException.class, () -> itemService.updateItem(itemDto, 1L, 1L));
    }

    @Test
    void updateWhenNotOwner() {
        Mockito
                .when(mokItemRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));

        assertThrows(StorageException.class, () -> itemService.updateItem(itemDto, 1L, 2L));
    }

    @Test
    void updateWithNullFields() {
        itemDto.setName(null);
        itemDto.setDescription(null);
        itemDto.setAvailable(null);
        item.setName(null);
        item.setDescription(null);
        item.setAvailable(null);

        Mockito
                .when(mokItemRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokItemMapper.toItem(Mockito.any(ItemDto.class)))
                .thenReturn(item);

        Mockito
                .when(mokItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));

        Mockito
                .when(mokUserRepository.existsById(user.getId()))
                .thenReturn(true);

        Mockito
                .when(mokUserRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        Mockito.when(mokItemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item1);

        Mockito
                .when(mokItemMapper.toItemDto(Mockito.any(Item.class)))
                .thenReturn(itemDto1);

        assertEquals(itemDto1, itemService.updateItem(itemDto, 1L, user.getId()));
        Mockito.verify(mokItemMapper, Mockito.times(1))
                .toItem(itemDto);
        Mockito.verify(mokItemMapper, Mockito.times(1))
                .toItemDto(item1);
        Mockito.verify(mokUserRepository, Mockito.times(1))
                .findById(1L);
        Mockito.verify(mokRequestsRepository, Mockito.never())
                .findById(Mockito.anyLong());
        Mockito.verify(mokItemRepository, Mockito.times(1))
                .save(Mockito.any(Item.class));
    }

    @Test
    void getOwnerItemTest() {
        Mockito
                .when(mokItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));

        Mockito
                .when(mokItemMapper.toItemBookingDto(Mockito.any(Item.class)))
                .thenReturn(itemDtoBookings);

        Booking bookingLast = new Booking(1L, null, null, null, null, null);
        Mockito
                .when(mokBookingRepository.getLastBookingForItem(1L))
                .thenReturn(bookingLast);

        Mockito.when(mokBookingMapper.toItemBooking(bookingLast))
                .thenReturn(lastBookingDto);

        Booking bookingNext = new Booking(2L, null, null, null, null, null);
        Mockito
                .when(mokBookingRepository.getNextBookingForItem(1L))
                .thenReturn(bookingNext);

        Mockito.when(mokBookingMapper.toItemBooking(bookingNext))
                .thenReturn(nextBookingDto);

        Mockito
                .when(mokCommentRepository.findAllByItem_Id(1L))
                .thenReturn(List.of(new Comment(), new Comment()));
        Mockito
                .when(mokCommentMapper.toCommentDto(Mockito.any(Comment.class)))
                .thenReturn(comment);

        ItemDtoBookings itemDtoBookingsFromService = itemService.getItem(1L, 1L);
        itemDtoBookings.setLastBooking(lastBookingDto);
        itemDtoBookings.setNextBooking(nextBookingDto);
        itemDtoBookings.setComments(List.of(comment, comment));

        assertEquals(itemDtoBookings, itemDtoBookingsFromService);
        Mockito.verify(mokItemRepository, Mockito.times(1))
                .findById(1L);
        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .getLastBookingForItem(1L);
        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .getNextBookingForItem(1L);
        Mockito.verify(mokCommentRepository, Mockito.times(1))
                .findAllByItem_Id(1L);
        Mockito.verify(mokCommentMapper, Mockito.times(2))
                .toCommentDto(Mockito.any(Comment.class));
    }

    @Test
    void getOwnerItemWithoutBookingsAndCommentsAndRequest() {
        item1.setRequest(null);
        itemDtoBookings.setRequest(null);

        Mockito
                .when(mokItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));

        Mockito
                .when(mokItemMapper.toItemBookingDto(Mockito.any(Item.class)))
                .thenReturn(itemDtoBookings);

        Mockito
                .when(mokBookingRepository.getLastBookingForItem(1L))
                .thenReturn(null);

        Mockito
                .when(mokBookingRepository.getNextBookingForItem(1L))
                .thenReturn(null);

        Mockito
                .when(mokCommentRepository.findAllByItem_Id(1L))
                .thenReturn(List.of());

        ItemDtoBookings itemDtoBookingsFromService = itemService.getItem(1L, 1L);
        itemDtoBookings.setComments(List.of());

        assertEquals(itemDtoBookings, itemDtoBookingsFromService);
        Mockito.verify(mokItemRepository, Mockito.times(1))
                .findById(1L);
        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .getLastBookingForItem(1L);
        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .getNextBookingForItem(1L);
        Mockito.verify(mokCommentRepository, Mockito.times(1))
                .findAllByItem_Id(1L);
        Mockito.verify(mokCommentMapper, Mockito.never())
                .toCommentDto(Mockito.any(Comment.class));
    }

    @Test
    void itemGetWhenNotOwner() {
        Mockito
                .when(mokItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));

        Mockito
                .when(mokItemMapper.toItemBookingDto(Mockito.any(Item.class)))
                .thenReturn(itemDtoBookings);

        Mockito
                .when(mokCommentRepository.findAllByItem_Id(1L))
                .thenReturn(List.of(new Comment(), new Comment()));
        Mockito
                .when(mokCommentMapper.toCommentDto(Mockito.any(Comment.class)))
                .thenReturn(comment);

        ItemDtoBookings itemDtoBookingsFromService = itemService.getItem(1L, 2L);
        itemDtoBookings.setComments(List.of(comment, comment));

        assertEquals(itemDtoBookings, itemDtoBookingsFromService);
        Mockito.verify(mokItemRepository, Mockito.times(1))
                .findById(1L);
        Mockito.verify(mokBookingRepository, Mockito.never())
                .getLastBookingForItem(Mockito.anyLong());
        Mockito.verify(mokBookingRepository, Mockito.never())
                .getNextBookingForItem(Mockito.anyLong());
        Mockito.verify(mokCommentRepository, Mockito.times(1))
                .findAllByItem_Id(1L);
        Mockito.verify(mokCommentMapper, Mockito.times(2))
                .toCommentDto(Mockito.any(Comment.class));
    }

    @Test
    void getAllUserItemsTest() {
        Item item1 = new Item(1L, "1Вещь", "1Супер", true, user, itemRequest);
        Item item2 = new Item(2L, "2Вещь", "2Супер", true, user, null);
        Item item3 = new Item(3L, "3Вещь", "3Супер", true, user, null);
        Item item4 = new Item(4L, "4Вещь", "4Супер", true, user, null);
        ItemDtoBookings itemDtoBookings1 = new ItemDtoBookings(item1.getId(),
                item1.getName(),
                item1.getDescription(),
                item1.getAvailable(),
                itemRequest,
                lastBookingDto,
                nextBookingDto,
                List.of(comment));
        ItemDtoBookings itemDtoBookings2 = new ItemDtoBookings(item2.getId(),
                item2.getName(),
                item2.getDescription(),
                item2.getAvailable(),
                null,
                null,
                null,
                List.of());
        ItemDtoBookings itemDtoBookings3 = new ItemDtoBookings(item3.getId(),
                item3.getName(),
                item3.getDescription(),
                item3.getAvailable(),
                null,
                null,
                null,
                List.of());
        ItemDtoBookings itemDtoBookings4 = new ItemDtoBookings(item4.getId(),
                item4.getName(),
                item4.getDescription(),
                item4.getAvailable(),
                null,
                null,
                null,
                List.of());

        Mockito
                .when(mokItemRepository.findAllByOwner_IdOrderById(user.getId(), PageRequest.of(2 / 10, 10)))
                .thenReturn(List.of(item1, item2, item3, item4));

        Mockito
                .when(mokItemMapper.toItemBookingDto(item1))
                .thenReturn(itemDtoBookings1);

        Mockito
                .when(mokItemMapper.toItemBookingDto(item2))
                .thenReturn(itemDtoBookings2);

        Mockito
                .when(mokItemMapper.toItemBookingDto(item3))
                .thenReturn(itemDtoBookings3);

        Mockito
                .when(mokItemMapper.toItemBookingDto(item4))
                .thenReturn(itemDtoBookings4);

        Booking bookingLast = new Booking(1L, null, null, null, null, null);
        Mockito
                .when(mokBookingRepository.getLastBookingForItem(1L))
                .thenReturn(bookingLast);

        Mockito.when(mokBookingMapper.toItemBooking(bookingLast))
                .thenReturn(lastBookingDto);

        Booking bookingNext = new Booking(2L, null, null, null, null, null);
        Mockito
                .when(mokBookingRepository.getNextBookingForItem(1L))
                .thenReturn(bookingNext);

        Mockito.when(mokBookingMapper.toItemBooking(bookingNext))
                .thenReturn(nextBookingDto);

        Mockito
                .when(mokCommentRepository.findAllByItem_Id(1L))
                .thenReturn(List.of(new Comment(1L, "a", null, null, null)));

        Mockito
                .when(mokCommentMapper.toCommentDto(Mockito.any(Comment.class)))
                .thenReturn(comment);


        List<ItemDtoBookings> listToCompare = List.of(
                itemDtoBookings1, itemDtoBookings2, itemDtoBookings3, itemDtoBookings4);

        assertEquals(listToCompare, itemService.getAllUserItems(1L, 2, 10));
        Mockito.verify(mokItemRepository, Mockito.times(1))
                .findAllByOwner_IdOrderById(1L, PageRequest.of(2 / 10, 10));
        Mockito.verify(mokBookingRepository, Mockito.times(4))
                .getLastBookingForItem(Mockito.anyLong());
        Mockito.verify(mokBookingRepository, Mockito.times(4))
                .getNextBookingForItem(Mockito.anyLong());
        Mockito.verify(mokCommentRepository, Mockito.times(4))
                .findAllByItem_Id(Mockito.anyLong());
        Mockito.verify(mokCommentMapper, Mockito.times(1))
                .toCommentDto(Mockito.any(Comment.class));
    }

    @Test
    void searchItemTest() {
        Item item2 = new Item(2L, "2 Cупер", "2 Вещь", true, user, null);
        ItemDto itemDto2 = new ItemDto(item2.getId(),
                item2.getName(),
                item2.getDescription(),
                item2.getAvailable(),
                null);

        Mockito
                .when(mokItemRepository.find("вещь", PageRequest.of(2 / 10, 10)))
                .thenReturn(List.of(item1, item2));

        Mockito
                .when(mokItemMapper.toItemDto(item1))
                .thenReturn(itemDto1);

        Mockito
                .when(mokItemMapper.toItemDto(item2))
                .thenReturn(itemDto2);

        List<ItemDto> itemDtoList = List.of(itemDto1, itemDto2);

        assertEquals(itemDtoList, itemService.searchItems("Вещь", 2, 10));
        assertEquals(itemDtoList, itemService.searchItems(" ВЕЩЬ  ", 2, 10));
        assertEquals(List.of(), itemService.searchItems("", 2, 10));
        assertEquals(List.of(), itemService.searchItems(" ", 2, 10));
        Mockito.verify(mokItemRepository, Mockito.times(2))
                .find("вещь", PageRequest.of(2 / 10, 10));
        Mockito.verify(mokItemMapper, Mockito.times(4))
                .toItemDto(Mockito.any(Item.class));
    }


    @Test
    void createCommentTest() {
        CommentDto commentDto = new CommentDto(1L, "Ваще балдеж", "Vova", null);

        Mockito
                .when(mokItemRepository.existsById(1L))
                .thenReturn(true);
        Mockito
                .when(mokUserRepository.existsById(2L))
                .thenReturn(true);

        User user2 = new User(2L, "Vova", "vova@mail.ru");
        Booking lastBooking = new Booking(1L,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                item1,
                user2,
                BookingStatus.APPROVED);

        Mockito
                .when(mokBookingRepository.findLastBooker_IdAndItem_Id(2L, 1L))
                .thenReturn(Optional.of(lastBooking));

        Comment comment = new Comment(commentDto.getId(), commentDto.getText(), null, null, LocalDateTime.now());

        Mockito
                .when(mokCommentMapper.toComment(commentDto))
                .thenReturn(comment);

        comment.setAuthor(user2);
        comment.setItem(item1);

        Mockito
                .when(mokCommentRepository.save(comment))
                .thenReturn(comment);

        commentDto.setCreated(comment.getCreationDate());

        Mockito
                .when(mokCommentMapper.toCommentDto(comment))
                .thenReturn(commentDto);
        assertEquals(commentDto, itemService.addComment(1, 2, commentDto));
        Mockito.verify(mokItemRepository, Mockito.times(1))
                .existsById(1L);
        Mockito.verify(mokUserRepository, Mockito.times(1))
                .existsById(2L);
        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .findLastBooker_IdAndItem_Id(2L, 1L);
        Mockito.verify(mokCommentMapper, Mockito.times(1))
                .toComment(commentDto);
        Mockito.verify(mokCommentMapper, Mockito.times(1))
                .toCommentDto(comment);
    }

    @Test
    void commentCreateWhenItemNonexistent() {
        CommentDto commentDto = new CommentDto(1L, "Ваще балдеж", "Vova", null);

        Mockito
                .when(mokItemRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(StorageException.class, () -> itemService.addComment(1, 2, commentDto));
        Mockito.verify(mokItemRepository, Mockito.times(1))
                .existsById(1L);
        Mockito.verify(mokUserRepository, Mockito.never())
                .existsById(Mockito.anyLong());
        Mockito.verify(mokBookingRepository, Mockito.never())
                .findLastBooker_IdAndItem_Id(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void commentCreateWhenUserNonexistent() {
        CommentDto commentDto = new CommentDto(1L, "Ваще балдеж", "Vova", null);

        Mockito
                .when(mokItemRepository.existsById(1L))
                .thenReturn(true);

        Mockito
                .when(mokUserRepository.existsById(2L))
                .thenReturn(false);

        assertThrows(StorageException.class, () -> itemService.addComment(1, 2, commentDto));
        Mockito.verify(mokItemRepository, Mockito.times(1))
                .existsById(1L);
        Mockito.verify(mokUserRepository, Mockito.times(1))
                .existsById(2L);
        Mockito.verify(mokBookingRepository, Mockito.never())
                .findLastBooker_IdAndItem_Id(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void commentCreateWhenBookingNonexistent() {
        CommentDto commentDto = new CommentDto(1L, "Ваще балдеж", "Vova", null);

        Mockito
                .when(mokItemRepository.existsById(1L))
                .thenReturn(true);

        Mockito
                .when(mokUserRepository.existsById(2L))
                .thenReturn(true);

        Mockito
                .when(mokBookingRepository.findLastBooker_IdAndItem_Id(2L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(StorageException.class, () -> itemService.addComment(1, 2, commentDto));
        Mockito.verify(mokItemRepository, Mockito.times(1))
                .existsById(1L);
        Mockito.verify(mokUserRepository, Mockito.times(1))
                .existsById(2L);
        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .findLastBooker_IdAndItem_Id(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void createCommentWhenBookingNotFinished() {
        CommentDto commentDto = new CommentDto(1L, "Ваще балдеж", "Vova", null);

        Mockito
                .when(mokItemRepository.existsById(1L))
                .thenReturn(true);
        Mockito
                .when(mokUserRepository.existsById(2L))
                .thenReturn(true);

        User user2 = new User(2L, "Vova", "vova@mail.ru");
        Booking lastBooking = new Booking(1L,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1),
                item1,
                user2,
                BookingStatus.APPROVED);

        Mockito
                .when(mokBookingRepository.findLastBooker_IdAndItem_Id(2L, 1L))
                .thenReturn(Optional.of(lastBooking));

        assertThrows(ValidationException.class, () -> itemService.addComment(1, 2, commentDto));
        Mockito.verify(mokItemRepository, Mockito.times(1))
                .existsById(1L);
        Mockito.verify(mokUserRepository, Mockito.times(1))
                .existsById(2L);
        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .findLastBooker_IdAndItem_Id(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(mokCommentRepository, Mockito.never())
                .save(Mockito.any(Comment.class));
    }
}