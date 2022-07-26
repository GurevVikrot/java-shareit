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
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestControllerTest {
    private final UserDto requesterDto = new UserDto(2L, "Vova", "vova@mail.ru");
    private LocalDateTime now;
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
                .andExpect(jsonPath("$.created", notNullValue()));
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
                .andExpect(jsonPath("$.[0].created", notNullValue()));
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
                .andExpect(jsonPath("$.[0].created", notNullValue()));

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
                .andExpect(jsonPath("$.[0].created", notNullValue()));

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
                .andExpect(jsonPath("$.[0].created", notNullValue()));
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
                .andExpect(jsonPath("$.created", notNullValue()));
    }
}