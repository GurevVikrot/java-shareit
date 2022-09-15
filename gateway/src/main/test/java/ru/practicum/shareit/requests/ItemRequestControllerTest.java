package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestControllerTest {
    private final Map<String, Object> requesterDto = Map.of(
            "id", 2L,
            "name", "Vova",
            "email", "vova@mail.ru");
    private LocalDateTime now;
    private ItemRequestDto itemRequestDto;
    private Map<String, Object> requestDtoBody;
    private ResponseEntity<Object> itemRequestDtoEntity;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestClient mokRequestClient;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();
        itemRequestDto = new ItemRequestDto(0L, "Хочется", null, null, null);
        requestDtoBody = Map.of(
                "id", 1L,
                "description", "Хочется",
                "requester", requesterDto,
                "items", List.of(),
                "created", now);

        itemRequestDtoEntity = ResponseEntity.status(HttpStatus.OK).body(requestDtoBody);
    }

    @Test
    void postRequestTest() throws Exception {
        Mockito
                .when(mokRequestClient.createRequest(Mockito.any(ItemRequestDto.class), Mockito.anyLong()))
                .thenReturn(itemRequestDtoEntity);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is(requestDtoBody.get("description"))))
                .andExpect(jsonPath("$.requester", notNullValue()))
                .andExpect(jsonPath("$.requester.id", is(2)))
                .andExpect(jsonPath("$.requester.name", is(requesterDto.get("name"))))
                .andExpect(jsonPath("$.requester.email", is(requesterDto.get("email"))))
                .andExpect(jsonPath("$.items", notNullValue()))
                .andExpect(jsonPath("$.created", notNullValue()));
    }

    @Test
    void postRequestWithNotValideBodyTest() throws Exception {
        Mockito
                .when(mokRequestClient.createRequest(Mockito.any(ItemRequestDto.class), Mockito.anyLong()))
                .thenReturn(itemRequestDtoEntity);

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
                .when(mokRequestClient.getOwnerRequests(Mockito.anyLong()))
                .thenReturn(new ResponseEntity<>(List.of(requestDtoBody), HttpStatus.OK));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].description", is(requestDtoBody.get("description"))))
                .andExpect(jsonPath("$.[0].requester", notNullValue()))
                .andExpect(jsonPath("$.[0].requester.id", is(2)))
                .andExpect(jsonPath("$.[0].requester.name", is(requesterDto.get("name"))))
                .andExpect(jsonPath("$.[0].requester.email", is(requesterDto.get("email"))))
                .andExpect(jsonPath("$.[0].items", notNullValue()))
                .andExpect(jsonPath("$.[0].created", notNullValue()));
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
                .when(mokRequestClient.getAllRequestPagination(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong()))
                .thenReturn(new ResponseEntity<>(List.of(requestDtoBody), HttpStatus.OK));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].description", is(requestDtoBody.get("description"))))
                .andExpect(jsonPath("$.[0].requester", notNullValue()))
                .andExpect(jsonPath("$.[0].requester.id", is(2)))
                .andExpect(jsonPath("$.[0].requester.name", is(requesterDto.get("name"))))
                .andExpect(jsonPath("$.[0].requester.email", is(requesterDto.get("email"))))
                .andExpect(jsonPath("$.[0].items", notNullValue()))
                .andExpect(jsonPath("$.[0].created", notNullValue()));

        mvc.perform(get("/requests/all?from=0&size=1")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].description", is(requestDtoBody.get("description"))))
                .andExpect(jsonPath("$.[0].requester", notNullValue()))
                .andExpect(jsonPath("$.[0].requester.id", is(2)))
                .andExpect(jsonPath("$.[0].requester.name", is(requesterDto.get("name"))))
                .andExpect(jsonPath("$.[0].requester.email", is(requesterDto.get("email"))))
                .andExpect(jsonPath("$.[0].items", notNullValue()))
                .andExpect(jsonPath("$.[0].created", notNullValue()));

        mvc.perform(get("/requests/all?from=99&size=1000")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].description", is(requestDtoBody.get("description"))))
                .andExpect(jsonPath("$.[0].requester", notNullValue()))
                .andExpect(jsonPath("$.[0].requester.id", is(2)))
                .andExpect(jsonPath("$.[0].requester.name", is(requesterDto.get("name"))))
                .andExpect(jsonPath("$.[0].requester.email", is(requesterDto.get("email"))))
                .andExpect(jsonPath("$.[0].items", notNullValue()))
                .andExpect(jsonPath("$.[0].created", notNullValue()));
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
                .when(mokRequestClient.getRequest(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemRequestDtoEntity);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is(requestDtoBody.get("description"))))
                .andExpect(jsonPath("$.requester", notNullValue()))
                .andExpect(jsonPath("$.requester.id", is(2)))
                .andExpect(jsonPath("$.requester.name", is(requesterDto.get("name"))))
                .andExpect(jsonPath("$.requester.email", is(requesterDto.get("email"))))
                .andExpect(jsonPath("$.items", notNullValue()))
                .andExpect(jsonPath("$.created", notNullValue()));
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