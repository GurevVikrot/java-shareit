package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

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
    private final Map<String, Object> lastBookingDto = Map.of(
            "id", 1,
            "start", LocalDateTime.now().minusDays(2),
            "end", LocalDateTime.now().minusDays(1),
            "bookerId", 1L,
            "status", "APPROVED");
    private final Map<String, Object> nextBookingDto = Map.of(
            "id", 2,
            "start", LocalDateTime.now().plusDays(2),
            "end", LocalDateTime.now().plusDays(1),
            "bookerId", 2L,
            "status", "APPROVED");

    private Map<String, Object> itemDtoBody;
    private Map<String, Object> itemBookingDtoBody;
    private final Map<String, Object> comment = Map.of(
            "id", 1,
            "text", "Балдеж",
            "authorName", "Vova",
            "created", LocalDateTime.now().minusDays(1));
    private ItemDto itemDto;
    private ResponseEntity<Object> itemDtoEntity;
    private ResponseEntity<Object> itemBookingDtoEntity;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient mokItemClient;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void beforeEach() {
        itemDto = new ItemDto(0L, "Вещь", "Супер", true, null);

        itemDtoBody = Map.of(
                "id", 1L,
                "name", "Вещь",
                "description", "Супер",
                "available", true,
                "requestId", "null");
        itemDtoEntity = ResponseEntity.status(HttpStatus.OK).body(itemDtoBody);

        itemBookingDtoBody = Map.of(
                "id", 1L,
                "name", "Вещь",
                "description", "Супер",
                "available", true,
                "request", "null",
                "lastBooking", lastBookingDto,
                "nextBooking", nextBookingDto,
                "comments", List.of(comment, comment));
        itemBookingDtoEntity = ResponseEntity.status(HttpStatus.OK).body(itemBookingDtoBody);
    }

    @Test
    void createItemTest() throws Exception {
        Mockito
                .when(mokItemClient.createItem(Mockito.any(ItemDto.class), Mockito.anyLong()))
                .thenReturn(itemDtoEntity);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(itemDtoBody.get("name"))))
                .andExpect(jsonPath("$.description", is(itemDtoBody.get("description"))))
                .andExpect(jsonPath("$.available", is(itemDtoBody.get("available"))))
                .andExpect(jsonPath("$.requestId", is(itemDtoBody.get("requestId"))));
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

        Mockito.verify(mokItemClient, Mockito.never())
                .createItem(Mockito.any(ItemDto.class), Mockito.anyLong());
    }

    @Test
    void updateTest() throws Exception {
        Mockito.when(mokItemClient.updateItem(Mockito.any(ItemDto.class), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemDtoEntity);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(itemDtoBody.get("name"))))
                .andExpect(jsonPath("$.description", is(itemDtoBody.get("description"))))
                .andExpect(jsonPath("$.available", is(itemDtoBody.get("available"))))
                .andExpect(jsonPath("$.requestId", is(itemDtoBody.get("requestId"))));

        Mockito.verify(mokItemClient, Mockito.times(1))
                .updateItem(Mockito.any(ItemDto.class), Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void updateTestWithNullFields() throws Exception {
        itemDto.setName(null);
        itemDto.setDescription(null);
        itemDto.setAvailable(null);

        Mockito.when(mokItemClient.updateItem(Mockito.any(ItemDto.class), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemDtoEntity);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(itemDtoBody.get("name"))))
                .andExpect(jsonPath("$.description", is(itemDtoBody.get("description"))))
                .andExpect(jsonPath("$.available", is(itemDtoBody.get("available"))))
                .andExpect(jsonPath("$.requestId", is(itemDtoBody.get("requestId"))));

        Mockito.verify(mokItemClient, Mockito.times(1))
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

        Mockito.verify(mokItemClient, Mockito.never())
                .updateItem(Mockito.any(ItemDto.class), Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void getItemTest() throws Exception {
        Mockito
                .when(mokItemClient.getItem(1L, 1L))
                .thenReturn(itemBookingDtoEntity);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(itemBookingDtoBody.get("name"))))
                .andExpect(jsonPath("$.description", is(itemBookingDtoBody.get("description"))))
                .andExpect(jsonPath("$.available", is(itemBookingDtoBody.get("available"))))
                .andExpect(jsonPath("$.request", is(itemBookingDtoBody.get("request"))))
                .andExpect(jsonPath("$.lastBooking", notNullValue()))
                .andExpect(jsonPath("$.lastBooking.id", is(1)))
                .andExpect(jsonPath("$.lastBooking.start", notNullValue()))
                .andExpect(jsonPath("$.lastBooking.end", notNullValue()))
                .andExpect(jsonPath("$.lastBooking.bookerId", is(1)))
                .andExpect(jsonPath("$.lastBooking.status", is("APPROVED")))
                .andExpect(jsonPath("$.nextBooking", notNullValue()))
                .andExpect(jsonPath("$.nextBooking.id", is(2)))
                .andExpect(jsonPath("$.nextBooking.start", notNullValue()))
                .andExpect(jsonPath("$.nextBooking.end", notNullValue()))
                .andExpect(jsonPath("$.nextBooking.bookerId", is(2)))
                .andExpect(jsonPath("$.nextBooking.status", is("APPROVED")))
                .andExpect(jsonPath("$.comments", notNullValue()))
                .andExpect(jsonPath("$.comments", hasSize(2)))
                .andExpect(jsonPath("$.comments.[0].id", is(1)));

        Mockito.verify(mokItemClient, Mockito.times(1))
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

        Mockito.verify(mokItemClient, Mockito.never())
                .updateItem(Mockito.any(ItemDto.class), Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void getAllUserItemsTest() throws Exception {
        Mockito
                .when(mokItemClient.getAllUserItems(1L, 0, 10))
                .thenReturn(new ResponseEntity<>(List.of(itemBookingDtoBody), HttpStatus.OK));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].name", is(itemBookingDtoBody.get("name"))))
                .andExpect(jsonPath("$.[0].description", is(itemBookingDtoBody.get("description"))))
                .andExpect(jsonPath("$.[0].available", is(itemBookingDtoBody.get("available"))))
                .andExpect(jsonPath("$.[0].request", is(itemBookingDtoBody.get("request"))))
                .andExpect(jsonPath("$.[0].lastBooking", notNullValue()))
                .andExpect(jsonPath("$.[0].lastBooking.id", is(1)))
                .andExpect(jsonPath("$.[0].lastBooking.start", notNullValue()))
                .andExpect(jsonPath("$.[0].lastBooking.end", notNullValue()))
                .andExpect(jsonPath("$.[0].lastBooking.bookerId", is(1)))
                .andExpect(jsonPath("$.[0].lastBooking.status", is("APPROVED")))
                .andExpect(jsonPath("$.[0].nextBooking", notNullValue()))
                .andExpect(jsonPath("$.[0].nextBooking.id", is(2)))
                .andExpect(jsonPath("$.[0].nextBooking.start", notNullValue()))
                .andExpect(jsonPath("$.[0].nextBooking.end", notNullValue()))
                .andExpect(jsonPath("$.[0].nextBooking.bookerId", is(2)))
                .andExpect(jsonPath("$.[0].nextBooking.status", is("APPROVED")))
                .andExpect(jsonPath("$.[0].comments", notNullValue()))
                .andExpect(jsonPath("$.[0].comments", hasSize(2)))
                .andExpect(jsonPath("$.[0].comments.[0].id", is(1)));

        mvc.perform(get("/items?from=0&size=1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(mokItemClient, Mockito.times(1))
                .getAllUserItems(1L, 0, 10);
        Mockito.verify(mokItemClient, Mockito.times(1))
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
                .when(mokItemClient.searchItems(
                        Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong()))
                .thenReturn(new ResponseEntity<>(List.of(itemDtoBody), HttpStatus.OK));

        mvc.perform(get("/items/search?text= Вещь ")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].name", is(itemDtoBody.get("name"))))
                .andExpect(jsonPath("$.[0].description", is(itemDtoBody.get("description"))))
                .andExpect(jsonPath("$.[0].available", is(itemDtoBody.get("available"))))
                .andExpect(jsonPath("$.[0].requestId", is(itemDtoBody.get("requestId"))));

        mvc.perform(get("/items/search?text=Вещь&from=0&size=1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].name", is(itemDtoBody.get("name"))))
                .andExpect(jsonPath("$.[0].description", is(itemDtoBody.get("description"))))
                .andExpect(jsonPath("$.[0].available", is(itemDtoBody.get("available"))))
                .andExpect(jsonPath("$.[0].requestId", is(itemDtoBody.get("requestId"))));

        mvc.perform(get("/items/search?text=Вещь&from=0")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].name", is(itemDtoBody.get("name"))))
                .andExpect(jsonPath("$.[0].description", is(itemDtoBody.get("description"))))
                .andExpect(jsonPath("$.[0].available", is(itemDtoBody.get("available"))))
                .andExpect(jsonPath("$.[0].requestId", is(itemDtoBody.get("requestId"))));

        mvc.perform(get("/items/search?text=Вещь&size=1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].name", is(itemDtoBody.get("name"))))
                .andExpect(jsonPath("$.[0].description", is(itemDtoBody.get("description"))))
                .andExpect(jsonPath("$.[0].available", is(itemDtoBody.get("available"))))
                .andExpect(jsonPath("$.[0].requestId", is(itemDtoBody.get("requestId"))));

        mvc.perform(get("/items/search?text=&size=1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)));

        mvc.perform(get("/items/search?text= &size=1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)));
    }

    @Test
    void incorrectSearchTest() {
        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/items/search?text=Вещь&from=-1&size=1")
                                .header("X-Sharer-User-Id", "1")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/items/search?text=Вещь&from=0&size=0")
                                .header("X-Sharer-User-Id", "1")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/items/search?text=Вещь&from=0&size=-1")
                                .header("X-Sharer-User-Id", "1")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));
    }

    @Test
    void postCommentTest() throws Exception {
        Mockito
                .when(mokItemClient.addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(CommentDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(comment));

        CommentDto commentDto = new CommentDto(0, "text", "Vova", LocalDateTime.now());

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is(comment.get("text"))))
                .andExpect(jsonPath("$.authorName", is(comment.get("authorName"))))
                .andExpect(jsonPath("$.created", notNullValue()));

        commentDto = new CommentDto(0, "text", null, null);

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
        CommentDto commentDto = new CommentDto(0, null, null, null);
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        commentDto.setText("");
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        commentDto.setText(" ");
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        commentDto.setText("a".repeat(522));
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}