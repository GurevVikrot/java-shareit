package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBookings;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemControllerTest {
    private final BookingDto lastBookingDto = new BookingDto(1,
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now().minusDays(1), 1L, BookingStatus.APPROVED);
    private final BookingDto nextBookingDto = new BookingDto(2,
            LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(3), 2L, BookingStatus.APPROVED);
    private final CommentDto comment = new CommentDto(1, "Балдеж", "Vova", lastBookingDto.getEnd());
    private ItemDto itemDto;
    private ItemDto itemDtoResponse;
    private ItemDtoBookings itemDtoBookings;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService mokItemService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private void setUpService(ItemService itemService) {
        this.mokItemService = itemService;
    }

    @BeforeEach
    void beforeEach() {
        itemDto = new ItemDto(0L, "Вещь", "Супер", true, null);
        itemDtoResponse = new ItemDto(1L, "Вещь", "Супер", true, null);
        itemDtoBookings = new ItemDtoBookings(1L,
                "Вещь",
                "Супер",
                true,
                null,
                lastBookingDto,
                nextBookingDto,
                List.of(comment, comment));
    }

    @Test
    void createItemTest() throws Exception {
        Mockito
                .when(mokItemService.createItem(Mockito.any(ItemDto.class), Mockito.anyLong()))
                .thenReturn(itemDtoResponse);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) itemDtoResponse.getId())))
                .andExpect(jsonPath("$.name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoResponse.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDtoResponse.getRequestId())));
    }

    @Test
    void createWithInvalideField() throws Exception {
        itemDto.setName("");
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        itemDto.setName(" ");
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        itemDto.setName(null);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        itemDto.setName("a".repeat(51));
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        itemDto.setName("Вещь");
        itemDto.setDescription("");
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        itemDto.setDescription(" ");
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        itemDto.setDescription(null);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        itemDto.setDescription("a".repeat(301));
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        itemDto.setDescription("Cупер");
        assertThrows(NestedServletException.class, () ->
                mvc.perform(post("/items")
                                .content(mapper.writeValueAsString(itemDto))
                                .header("X-Sharer-User-Id", "0")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class, () ->
                mvc.perform(post("/items")
                                .content(mapper.writeValueAsString(itemDto))
                                .header("X-Sharer-User-Id", "-1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        Mockito.verify(mokItemService, Mockito.never())
                .createItem(Mockito.any(ItemDto.class), Mockito.anyLong());
    }

    @Test
    void updateTest() throws Exception {
        Mockito.when(mokItemService.updateItem(Mockito.any(ItemDto.class), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemDtoResponse);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) itemDtoResponse.getId())))
                .andExpect(jsonPath("$.name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoResponse.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDtoResponse.getRequestId())));

        Mockito.verify(mokItemService, Mockito.times(1))
                .updateItem(Mockito.any(ItemDto.class), Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void updateTestWithNullFields() throws Exception {
        itemDto.setName(null);
        itemDto.setDescription(null);
        itemDto.setAvailable(null);

        Mockito.when(mokItemService.updateItem(Mockito.any(ItemDto.class), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemDtoResponse);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) itemDtoResponse.getId())))
                .andExpect(jsonPath("$.name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoResponse.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDtoResponse.getRequestId())));

        Mockito.verify(mokItemService, Mockito.times(1))
                .updateItem(Mockito.any(ItemDto.class), Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void updateTestWithIvalideIds() {
        assertThrows(NestedServletException.class,
                () -> mvc.perform(patch("/items/0")
                                .content(mapper.writeValueAsString(itemDto))
                                .header("X-Sharer-User-Id", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(patch("/items/-1")
                                .content(mapper.writeValueAsString(itemDto))
                                .header("X-Sharer-User-Id", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(patch("/items/1")
                                .content(mapper.writeValueAsString(itemDto))
                                .header("X-Sharer-User-Id", "0")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(patch("/items/1")
                                .content(mapper.writeValueAsString(itemDto))
                                .header("X-Sharer-User-Id", "-1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        Mockito.verify(mokItemService, Mockito.never())
                .updateItem(Mockito.any(ItemDto.class), Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void getItemTest() throws Exception {
        Mockito
                .when(mokItemService.getItem(1L, 1L))
                .thenReturn(itemDtoBookings);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) itemDtoBookings.getId())))
                .andExpect(jsonPath("$.name", is(itemDtoBookings.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoBookings.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoBookings.getAvailable())))
                .andExpect(jsonPath("$.request", is(itemDtoBookings.getRequest())))
                .andExpect(jsonPath("$.lastBooking", notNullValue()))
                .andExpect(jsonPath("$.lastBooking.id", is((int) lastBookingDto.getId())))
                .andExpect(jsonPath("$.lastBooking.start", notNullValue()))
                .andExpect(jsonPath("$.lastBooking.end", notNullValue()))
                .andExpect(jsonPath("$.lastBooking.bookerId", is((int) lastBookingDto.getBookerId())))
                .andExpect(jsonPath("$.lastBooking.status", is(lastBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.nextBooking", notNullValue()))
                .andExpect(jsonPath("$.nextBooking.id", is((int) nextBookingDto.getId())))
                .andExpect(jsonPath("$.nextBooking.start", notNullValue()))
                .andExpect(jsonPath("$.nextBooking.end", notNullValue()))
                .andExpect(jsonPath("$.nextBooking.bookerId", is((int) nextBookingDto.getBookerId())))
                .andExpect(jsonPath("$.nextBooking.status", is(nextBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.comments", notNullValue()))
                .andExpect(jsonPath("$.comments", hasSize(2)))
                .andExpect(jsonPath("$.comments.[0].id", is((int) comment.getId())));

        Mockito.verify(mokItemService, Mockito.times(1))
                .getItem(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void getWithIncorrectIds() {
        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/items/0")
                                .header("X-Sharer-User-Id", "1")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/items/-1")
                                .header("X-Sharer-User-Id", "1")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/items/1")
                                .header("X-Sharer-User-Id", "0")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/items/1")
                                .header("X-Sharer-User-Id", "-1")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        Mockito.verify(mokItemService, Mockito.never())
                .updateItem(Mockito.any(ItemDto.class), Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void getAllUserItemsTest() throws Exception {
        Mockito
                .when(mokItemService.getAllUserItems(1L, 0, 10))
                .thenReturn(List.of(itemDtoBookings));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is((int) itemDtoBookings.getId())))
                .andExpect(jsonPath("$.[0].name", is(itemDtoBookings.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDtoBookings.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDtoBookings.getAvailable())))
                .andExpect(jsonPath("$.[0].request", is(itemDtoBookings.getRequest())))
                .andExpect(jsonPath("$.[0].lastBooking", notNullValue()))
                .andExpect(jsonPath("$.[0].lastBooking.id", is((int) lastBookingDto.getId())))
                .andExpect(jsonPath("$.[0].lastBooking.start", notNullValue()))
                .andExpect(jsonPath("$.[0].lastBooking.end", notNullValue()))
                .andExpect(jsonPath("$.[0].lastBooking.bookerId", is((int) lastBookingDto.getBookerId())))
                .andExpect(jsonPath("$.[0].lastBooking.status", is(lastBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.[0].nextBooking", notNullValue()))
                .andExpect(jsonPath("$.[0].nextBooking.id", is((int) nextBookingDto.getId())))
                .andExpect(jsonPath("$.[0].nextBooking.start", notNullValue()))
                .andExpect(jsonPath("$.[0].nextBooking.end", notNullValue()))
                .andExpect(jsonPath("$.[0].nextBooking.bookerId", is((int) nextBookingDto.getBookerId())))
                .andExpect(jsonPath("$.[0].nextBooking.status", is(nextBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.[0].comments", notNullValue()))
                .andExpect(jsonPath("$.[0].comments", hasSize(2)))
                .andExpect(jsonPath("$.[0].comments.[0].id", is((int) comment.getId())));

        mvc.perform(get("/items?from=0&size=1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(mokItemService, Mockito.times(1))
                .getAllUserItems(1L, 0, 10);
        Mockito.verify(mokItemService, Mockito.times(1))
                .getAllUserItems(1L, 0, 1);
    }

    @Test
    void getAllUserItemsWithInvalidValues() {
        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/items")
                                .header("X-Sharer-User-Id", "0")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/items")
                                .header("X-Sharer-User-Id", "-1")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/items?from=-1&size=1")
                                .header("X-Sharer-User-Id", "1")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/items?from=0&size=0")
                                .header("X-Sharer-User-Id", "1")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/items?from=0&size=-1")
                                .header("X-Sharer-User-Id", "1")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));
    }

    @Test
    void correctSearchTest() throws Exception {
        Mockito
                .when(mokItemService.searchItems(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemDtoResponse));

        mvc.perform(get("/items/search?text= Вещь ")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is((int) itemDtoResponse.getId())))
                .andExpect(jsonPath("$.[0].name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDtoResponse.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDtoResponse.getAvailable())))
                .andExpect(jsonPath("$.[0].requestId", is(itemDtoResponse.getRequestId())));

        mvc.perform(get("/items/search?text=Вещь&from=0&size=1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is((int) itemDtoResponse.getId())))
                .andExpect(jsonPath("$.[0].name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDtoResponse.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDtoResponse.getAvailable())))
                .andExpect(jsonPath("$.[0].requestId", is(itemDtoResponse.getRequestId())));

        mvc.perform(get("/items/search?text=Вещь&from=0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is((int) itemDtoResponse.getId())))
                .andExpect(jsonPath("$.[0].name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDtoResponse.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDtoResponse.getAvailable())))
                .andExpect(jsonPath("$.[0].requestId", is(itemDtoResponse.getRequestId())));

        mvc.perform(get("/items/search?text=Вещь&size=1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is((int) itemDtoResponse.getId())))
                .andExpect(jsonPath("$.[0].name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDtoResponse.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDtoResponse.getAvailable())))
                .andExpect(jsonPath("$.[0].requestId", is(itemDtoResponse.getRequestId())));
    }

    @Test
    void incorrectSearchTest() {
        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/items/search?text=Вещь&from=-1&size=1")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/items/search?text=Вещь&from=0&size=0")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/items/search?text=Вещь&from=0&size=-1")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));
    }

    @Test
    void postCommentTest() throws Exception {
        Mockito
                .when(mokItemService.addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(CommentDto.class)))
                .thenReturn(comment);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) comment.getId())))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthorName())))
                .andExpect(jsonPath("$.created", notNullValue()));

        CommentDto commentDto = new CommentDto(0, "text", null, null);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Map<String, String> request = Map.of("text", "comment");
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void postCommentWithIvalideIds() {
        assertThrows(NestedServletException.class,
                () -> mvc.perform(post("/items/0/comment")
                                .content(mapper.writeValueAsString(comment))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "1")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(post("/items/-1/comment")
                                .content(mapper.writeValueAsString(comment))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "1")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(post("/items/1/comment")
                                .content(mapper.writeValueAsString(comment))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "0")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(post("/items/1/comment")
                                .content(mapper.writeValueAsString(comment))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "-1")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));
    }

    @Test
    void postCommentWithInvalidCommentFields() throws Exception {
        comment.setText(null);
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        comment.setText("");
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        comment.setText(" ");
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        comment.setText("a".repeat(522));
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}