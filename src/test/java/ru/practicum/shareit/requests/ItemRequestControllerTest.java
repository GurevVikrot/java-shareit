package ru.practicum.shareit.requests;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestControllerTest {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
    private final UserDto requesterDto = new UserDto(2L, "Vova", "vova@mail.ru");
    private final User requester = new User(2L, "Vova", "vova@mail.ru");
    private final User user = new User(1L, "Vitya", "vitya@mail.ru");
    private final ItemDto itemDto = new ItemDto(1L, "Вещь", "Супер", true, 1L);
    private LocalDateTime now;
    private ItemRequest itemRequestFromDb;
    private final Item item = new Item(1L, "Вещь", "Супер", true, user, itemRequestFromDb);
    private ItemRequest itemRequestFromMapper;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDto itemRequestDtoFromService;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RequestService mokRequestService;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();
        itemRequestDto = new ItemRequestDto(0L, "Хочется", null, null, null);
        itemRequestDtoFromService = new ItemRequestDto(1L, "Хочется", requesterDto, List.of(), now);
    }

    @Test
    void postRequestTest() throws Exception {
        Mockito
                .when(mokRequestService.createRequest(Mockito.any(ItemRequestDto.class), Mockito.anyLong()))
                .thenReturn(itemRequestDtoFromService);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) itemRequestDtoFromService.getId())))
                .andExpect(jsonPath("$.description", is(itemRequestDtoFromService.getDescription())))
                .andExpect(jsonPath("$.requester", notNullValue()))
                .andExpect(jsonPath("$.requester.id", is((int) itemRequestDtoFromService.getRequester().getId())))
                .andExpect(jsonPath("$.requester.name", is(itemRequestDtoFromService.getRequester().getName())))
                .andExpect(jsonPath("$.requester.email", is(itemRequestDtoFromService.getRequester().getEmail())))
                .andExpect(jsonPath("$.items", notNullValue()))
                .andExpect(jsonPath("$.created", is(now.format(formatter))));
    }

    @Test
    void postRequestWithNotValideBodyTest() throws Exception {
        Mockito
                .when(mokRequestService.createRequest(Mockito.any(ItemRequestDto.class), Mockito.anyLong()))
                .thenReturn(itemRequestDtoFromService);

        itemRequestDto.setDescription("");
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        itemRequestDto.setDescription(" ");
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        itemRequestDto.setDescription(null);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postRequestWithIncorrectUserIdTest() {
        assertThrows(NestedServletException.class,
                () -> mvc.perform(post("/requests")
                                .content(mapper.writeValueAsString(itemRequestDto))
                                .header("X-Sharer-User-Id", "0")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(post("/requests")
                                .content(mapper.writeValueAsString(itemRequestDto))
                                .header("X-Sharer-User-Id", "-1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));
    }

    @Test
    void getOwnerRequestsTest() throws Exception {
        Mockito
                .when(mokRequestService.getOwnerRequests(Mockito.anyLong()))
                .thenReturn(List.of(itemRequestDtoFromService));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is((int) itemRequestDtoFromService.getId())))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDtoFromService.getDescription())))
                .andExpect(jsonPath("$.[0].requester", notNullValue()))
                .andExpect(jsonPath("$.[0].requester.id", is((int) itemRequestDtoFromService.getRequester().getId())))
                .andExpect(jsonPath("$.[0].requester.name", is(itemRequestDtoFromService.getRequester().getName())))
                .andExpect(jsonPath("$.[0].requester.email", is(itemRequestDtoFromService.getRequester().getEmail())))
                .andExpect(jsonPath("$.[0].items", notNullValue()))
                .andExpect(jsonPath("$.[0].created", is(now.format(formatter))));
    }

    @Test
    void getOwnerRequestsWithIncorrectUserId() {
        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/requests")
                                .header("X-Sharer-User-Id", "0")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/requests")
                                .content(mapper.writeValueAsString(itemRequestDto))
                                .header("X-Sharer-User-Id", "-1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));
    }

    @Test
    void getAllRequestsTest() throws Exception {
        Mockito
                .when(mokRequestService.getAllRequestPagination(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong()))
                .thenReturn(List.of(itemRequestDtoFromService));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is((int) itemRequestDtoFromService.getId())))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDtoFromService.getDescription())))
                .andExpect(jsonPath("$.[0].requester", notNullValue()))
                .andExpect(jsonPath("$.[0].requester.id", is((int) itemRequestDtoFromService.getRequester().getId())))
                .andExpect(jsonPath("$.[0].requester.name", is(itemRequestDtoFromService.getRequester().getName())))
                .andExpect(jsonPath("$.[0].requester.email", is(itemRequestDtoFromService.getRequester().getEmail())))
                .andExpect(jsonPath("$.[0].items", notNullValue()))
                .andExpect(jsonPath("$.[0].created", is(now.format(formatter))));

        mvc.perform(get("/requests/all?from=0&size=1")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is((int) itemRequestDtoFromService.getId())))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDtoFromService.getDescription())))
                .andExpect(jsonPath("$.[0].requester", notNullValue()))
                .andExpect(jsonPath("$.[0].requester.id", is((int) itemRequestDtoFromService.getRequester().getId())))
                .andExpect(jsonPath("$.[0].requester.name", is(itemRequestDtoFromService.getRequester().getName())))
                .andExpect(jsonPath("$.[0].requester.email", is(itemRequestDtoFromService.getRequester().getEmail())))
                .andExpect(jsonPath("$.[0].items", notNullValue()))
                .andExpect(jsonPath("$.[0].created", is(now.format(formatter))));

        mvc.perform(get("/requests/all?from=99&size=1000")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is((int) itemRequestDtoFromService.getId())))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDtoFromService.getDescription())))
                .andExpect(jsonPath("$.[0].requester", notNullValue()))
                .andExpect(jsonPath("$.[0].requester.id", is((int) itemRequestDtoFromService.getRequester().getId())))
                .andExpect(jsonPath("$.[0].requester.name", is(itemRequestDtoFromService.getRequester().getName())))
                .andExpect(jsonPath("$.[0].requester.email", is(itemRequestDtoFromService.getRequester().getEmail())))
                .andExpect(jsonPath("$.[0].items", notNullValue()))
                .andExpect(jsonPath("$.[0].created", is(now.format(formatter))));
    }

    @Test
    void getAllRequestsWithIncorrectValues() {
        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/requests/all?from=-1&size=1")
                                .header("X-Sharer-User-Id", "1")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/requests/all?from=0&size=0")
                                .header("X-Sharer-User-Id", "1")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/requests/all?from=0&size=-1")
                                .header("X-Sharer-User-Id", "1")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/requests/all?from=0&size=1")
                                .header("X-Sharer-User-Id", "0")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/requests/all?from=0&size=1")
                                .header("X-Sharer-User-Id", "-1")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));
    }

    @Test
    void getRequestTest() throws Exception {
        Mockito
                .when(mokRequestService.getRequest(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemRequestDtoFromService);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) itemRequestDtoFromService.getId())))
                .andExpect(jsonPath("$.description", is(itemRequestDtoFromService.getDescription())))
                .andExpect(jsonPath("$.requester", notNullValue()))
                .andExpect(jsonPath("$.requester.id", is((int) itemRequestDtoFromService.getRequester().getId())))
                .andExpect(jsonPath("$.requester.name", is(itemRequestDtoFromService.getRequester().getName())))
                .andExpect(jsonPath("$.requester.email", is(itemRequestDtoFromService.getRequester().getEmail())))
                .andExpect(jsonPath("$.items", notNullValue()))
                .andExpect(jsonPath("$.created", is(now.format(formatter))));
    }

    @Test
    void getRequestWithIncorrectValues() {
        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/requests/0")
                                .header("X-Sharer-User-Id", "2")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/requests/-1")
                                .header("X-Sharer-User-Id", "2")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/requests/1")
                                .header("X-Sharer-User-Id", "0")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/requests/1")
                                .header("X-Sharer-User-Id", "-1")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()));
    }
}