package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingClient mokBookingClient;

    @Autowired
    private MockMvc mvc;

    private RequestBookingDto requestBookingDto;
    private Map<String, Object> bodyToResponse;
    private ResponseEntity<Object> responseEntity;
    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = LocalDateTime.now().plusDays(3);
    private final UserDto bookerDto = new UserDto(2L, "Booker", "booker@mail.ru");
    private final ItemDto itemDto = new ItemDto(1L, "Вещь", "Супер", true, null);

    @Autowired
    private void setUpService(BookingClient bookingClient) {
        this.mokBookingClient = bookingClient;
    }

    @BeforeEach
    void beforeEach() {
        requestBookingDto = new RequestBookingDto(1L, start, end);
        bodyToResponse = Map.of(
                "id", 1L,
                "start", start,
                "end", end,
                "item", itemDto,
                "booker", bookerDto,
                "status", BookingState.WAITING);
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(bodyToResponse);
    }

    @Test
    void createBookingTest() throws Exception {
        Mockito
                .when(mokBookingClient.create(Mockito.any(RequestBookingDto.class), Mockito.anyLong()))
                .thenReturn(responseEntity);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(requestBookingDto))
                        .header("X-Sharer-User-Id", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(jsonPath("$.item.id", is((int) itemDto.getId())))
                .andExpect(jsonPath("$.item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.item.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.item.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.item.requestId", is(itemDto.getRequestId())))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.booker.id", is((int) bookerDto.getId())))
                .andExpect(jsonPath("$.booker.name", is(bookerDto.getName())))
                .andExpect(jsonPath("$.booker.email", is(bookerDto.getEmail())))
                .andExpect(jsonPath("$.status", is(bodyToResponse.get("status").toString())));
    }

    @Test
    void createBookingWithNotValidStart() throws Exception {
        requestBookingDto.setStart(LocalDateTime.now().minusDays(1));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(requestBookingDto))
                        .header("X-Sharer-User-Id", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        requestBookingDto.setStart(null);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(requestBookingDto))
                        .header("X-Sharer-User-Id", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito
                .verify(mokBookingClient, Mockito.never())
                .create(Mockito.any(RequestBookingDto.class), Mockito.anyLong());
    }

    @Test
    void createBookingWithNotValidEnd() throws Exception {
        requestBookingDto.setEnd(LocalDateTime.now());

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(requestBookingDto))
                        .header("X-Sharer-User-Id", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        requestBookingDto.setEnd(LocalDateTime.now().minusDays(1));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(requestBookingDto))
                        .header("X-Sharer-User-Id", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        requestBookingDto.setEnd(null);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(requestBookingDto))
                        .header("X-Sharer-User-Id", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito
                .verify(mokBookingClient, Mockito.never())
                .create(Mockito.any(RequestBookingDto.class), Mockito.anyLong());
    }

    @Test
    void createBookingWithNotValideItemId() throws Exception {
        requestBookingDto.setItemId(0);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(requestBookingDto))
                        .header("X-Sharer-User-Id", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        requestBookingDto.setItemId(-1);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(requestBookingDto))
                        .header("X-Sharer-User-Id", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito
                .verify(mokBookingClient, Mockito.never())
                .create(Mockito.any(RequestBookingDto.class), Mockito.anyLong());
    }

    @Test
    void createBookingWithNotValideUserId() {
        assertThrows(NestedServletException.class,
                () -> mvc.perform(post("/bookings")
                                .content(mapper.writeValueAsString(requestBookingDto))
                                .header("X-Sharer-User-Id", "0")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(post("/bookings")
                                .content(mapper.writeValueAsString(requestBookingDto))
                                .header("X-Sharer-User-Id", "-1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        Mockito
                .verify(mokBookingClient, Mockito.never())
                .create(Mockito.any(RequestBookingDto.class), Mockito.anyLong());
    }

    @Test
    void correctApproveBooking() throws Exception {
        Mockito
                .when(mokBookingClient.approveBooking(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(responseEntity);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(jsonPath("$.item.id", is((int) itemDto.getId())))
                .andExpect(jsonPath("$.item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.item.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.item.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.item.requestId", is(itemDto.getRequestId())))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.booker.id", is((int) bookerDto.getId())))
                .andExpect(jsonPath("$.booker.name", is(bookerDto.getName())))
                .andExpect(jsonPath("$.booker.email", is(bookerDto.getEmail())))
                .andExpect(jsonPath("$.status", is(bodyToResponse.get("status").toString())));

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "false")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(jsonPath("$.item.id", is((int) itemDto.getId())))
                .andExpect(jsonPath("$.item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.item.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.item.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.item.requestId", is(itemDto.getRequestId())))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.booker.id", is((int) bookerDto.getId())))
                .andExpect(jsonPath("$.booker.name", is(bookerDto.getName())))
                .andExpect(jsonPath("$.booker.email", is(bookerDto.getEmail())))
                .andExpect(jsonPath("$.status", is(bodyToResponse.get("status").toString())));
    }

    @Test
    void approveBookingWithIncorrectValues() throws Exception {
        Mockito
                .when(mokBookingClient.approveBooking(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(responseEntity);

        assertThrows(NestedServletException.class,
                () -> mvc.perform(patch("/bookings/0")
                                .header("X-Sharer-User-Id", "1")
                                .param("approved", "true")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(patch("/bookings/-1")
                                .header("X-Sharer-User-Id", "1")
                                .param("approved", "true")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(patch("/bookings/1")
                                .header("X-Sharer-User-Id", "0")
                                .param("approved", "true")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(patch("/bookings/1")
                                .header("X-Sharer-User-Id", "-1")
                                .param("approved", "true")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void correctGetBooking() throws Exception {
        Mockito
                .when(mokBookingClient.getBooking(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(responseEntity);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(jsonPath("$.item.id", is((int) itemDto.getId())))
                .andExpect(jsonPath("$.item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.item.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.item.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.item.requestId", is(itemDto.getRequestId())))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.booker.id", is((int) bookerDto.getId())))
                .andExpect(jsonPath("$.booker.name", is(bookerDto.getName())))
                .andExpect(jsonPath("$.booker.email", is(bookerDto.getEmail())))
                .andExpect(jsonPath("$.status", is(bodyToResponse.get("status").toString())));
    }

    @Test
    void getBookingWithIncorrectValues() {
        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/bookings/0")
                                .header("X-Sharer-User-Id", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/bookings/-0")
                                .header("X-Sharer-User-Id", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/bookings/1")
                                .header("X-Sharer-User-Id", "0")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/bookings/1")
                                .header("X-Sharer-User-Id", "-1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));
    }

    @Test
    void correctGetAllUserBookings() throws Exception {
        Mockito
                .when(mokBookingClient.getUserBookings(
                        Mockito.any(BookingState.class), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(new ResponseEntity<>(List.of(bodyToResponse), HttpStatus.OK));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].start", notNullValue()))
                .andExpect(jsonPath("$.[0].end", notNullValue()))
                .andExpect(jsonPath("$.[0].item", notNullValue()))
                .andExpect(jsonPath("$.[0].item.id", is((int) itemDto.getId())))
                .andExpect(jsonPath("$.[0].item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].item.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].item.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.[0].item.requestId", is(itemDto.getRequestId())))
                .andExpect(jsonPath("$.[0].booker", notNullValue()))
                .andExpect(jsonPath("$.[0].booker.id", is((int) bookerDto.getId())))
                .andExpect(jsonPath("$.[0].booker.name", is(bookerDto.getName())))
                .andExpect(jsonPath("$.[0].booker.email", is(bookerDto.getEmail())))
                .andExpect(jsonPath("$.[0].status", is(bodyToResponse.get("status").toString())));

        mvc.perform(get("/bookings")
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].start", notNullValue()))
                .andExpect(jsonPath("$.[0].end", notNullValue()))
                .andExpect(jsonPath("$.[0].item", notNullValue()))
                .andExpect(jsonPath("$.[0].item.id", is((int) itemDto.getId())))
                .andExpect(jsonPath("$.[0].item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].item.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].item.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.[0].item.requestId", is(itemDto.getRequestId())))
                .andExpect(jsonPath("$.[0].booker", notNullValue()))
                .andExpect(jsonPath("$.[0].booker.id", is((int) bookerDto.getId())))
                .andExpect(jsonPath("$.[0].booker.name", is(bookerDto.getName())))
                .andExpect(jsonPath("$.[0].booker.email", is(bookerDto.getEmail())))
                .andExpect(jsonPath("$.[0].status", is(bodyToResponse.get("status").toString())));

        mvc.perform(get("/bookings")
                        .param("from", "99")
                        .param("size", "999")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)));

        mvc.perform(get("/bookings")
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "REJECTED")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)));

        mvc.perform(get("/bookings")
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "WAITING")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)));

        mvc.perform(get("/bookings")
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "PAST")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)));

        mvc.perform(get("/bookings")
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "CURRENT")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)));

        mvc.perform(get("/bookings")
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "FUTURE")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)));
    }

    @Test
    void getAllUserBookingsWithIncorrectValues() throws Exception {
        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/bookings")
                                .param("from", "-1")
                                .param("size", "1")
                                .param("state", "FUTURE")
                                .header("X-Sharer-User-Id", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/bookings")
                                .param("from", "0")
                                .param("size", "0")
                                .param("state", "FUTURE")
                                .header("X-Sharer-User-Id", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/bookings")
                                .param("from", "0")
                                .param("size", "-1")
                                .param("state", "FUTURE")
                                .header("X-Sharer-User-Id", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        mvc.perform(get("/bookings")
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "CACACA")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/bookings")
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "null")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/bookings")
                                .param("from", "0")
                                .param("size", "1")
                                .param("state", "WAITING")
                                .header("X-Sharer-User-Id", "0")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/bookings")
                                .param("from", "0")
                                .param("size", "1")
                                .param("state", "WAITING")
                                .header("X-Sharer-User-Id", "-1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));
    }

    @Test
    void correctGetAllOwnerBookings() throws Exception {
        Mockito
                .when(mokBookingClient.getOwnerBookings(
                        Mockito.any(BookingState.class), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(new ResponseEntity<>(List.of(bodyToResponse), HttpStatus.OK));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].start", notNullValue()))
                .andExpect(jsonPath("$.[0].end", notNullValue()))
                .andExpect(jsonPath("$.[0].item", notNullValue()))
                .andExpect(jsonPath("$.[0].item.id", is((int) itemDto.getId())))
                .andExpect(jsonPath("$.[0].item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].item.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].item.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.[0].item.requestId", is(itemDto.getRequestId())))
                .andExpect(jsonPath("$.[0].booker", notNullValue()))
                .andExpect(jsonPath("$.[0].booker.id", is((int) bookerDto.getId())))
                .andExpect(jsonPath("$.[0].booker.name", is(bookerDto.getName())))
                .andExpect(jsonPath("$.[0].booker.email", is(bookerDto.getEmail())))
                .andExpect(jsonPath("$.[0].status", is(bodyToResponse.get("status").toString())));

        mvc.perform(get("/bookings/owner")
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].start", notNullValue()))
                .andExpect(jsonPath("$.[0].end", notNullValue()))
                .andExpect(jsonPath("$.[0].item", notNullValue()))
                .andExpect(jsonPath("$.[0].item.id", is((int) itemDto.getId())))
                .andExpect(jsonPath("$.[0].item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].item.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].item.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.[0].item.requestId", is(itemDto.getRequestId())))
                .andExpect(jsonPath("$.[0].booker", notNullValue()))
                .andExpect(jsonPath("$.[0].booker.id", is((int) bookerDto.getId())))
                .andExpect(jsonPath("$.[0].booker.name", is(bookerDto.getName())))
                .andExpect(jsonPath("$.[0].booker.email", is(bookerDto.getEmail())))
                .andExpect(jsonPath("$.[0].status", is(bodyToResponse.get("status").toString())));

        mvc.perform(get("/bookings/owner")
                        .param("from", "99")
                        .param("size", "999")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)));

        mvc.perform(get("/bookings/owner")
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "REJECTED")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)));

        mvc.perform(get("/bookings/owner")
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "WAITING")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)));

        mvc.perform(get("/bookings/owner")
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "PAST")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)));

        mvc.perform(get("/bookings/owner")
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "CURRENT")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)));

        mvc.perform(get("/bookings/owner")
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "FUTURE")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)));
    }

    @Test
    void getAllOwnerBookingsWithIncorrectValues() throws Exception {
        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/bookings/owner")
                                .param("from", "-1")
                                .param("size", "1")
                                .param("state", "FUTURE")
                                .header("X-Sharer-User-Id", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/bookings/owner")
                                .param("from", "0")
                                .param("size", "0")
                                .param("state", "FUTURE")
                                .header("X-Sharer-User-Id", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/bookings/owner")
                                .param("from", "0")
                                .param("size", "-1")
                                .param("state", "FUTURE")
                                .header("X-Sharer-User-Id", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        mvc.perform(get("/bookings/owner")
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "CACACA")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/bookings/owner")
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "null")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/bookings/owner")
                                .param("from", "0")
                                .param("size", "1")
                                .param("state", "WAITING")
                                .header("X-Sharer-User-Id", "0")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/bookings/owner")
                                .param("from", "0")
                                .param("size", "1")
                                .param("state", "WAITING")
                                .header("X-Sharer-User-Id", "-1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));
    }

}