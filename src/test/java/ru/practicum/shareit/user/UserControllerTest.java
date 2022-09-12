package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService mokUserService;

    @Autowired
    private MockMvc mvc;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userDto = new UserDto(0L, "Petya", "Petya@mail.ru");
    }

    @Test
    void createUserTest() throws Exception {
        Mockito
                .when(mokUserService.createUser(Mockito.any(UserDto.class)))
                .thenReturn(new UserDto(1L, userDto.getName(), userDto.getEmail()));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void notValidNameRequest() throws Exception {
        userDto.setName("");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        userDto.setName(" ");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        userDto.setName(null);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito
                .verify(mokUserService, Mockito.never())
                .createUser(Mockito.any(UserDto.class));
    }

    @Test
    void notValidEmailRequest() throws Exception {
        userDto.setEmail("");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        userDto.setEmail(" ");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        userDto.setEmail(null);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito
                .verify(mokUserService, Mockito.never())
                .createUser(Mockito.any(UserDto.class));
    }

    @Test
    void correctUpdateTest() throws Exception {
        Mockito
                .when(mokUserService.updateUser(Mockito.any(UserDto.class), Mockito.anyLong()))
                .thenReturn(new UserDto(1L, userDto.getName(), userDto.getEmail()));

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        Mockito
                .verify(mokUserService, Mockito.times(1))
                .updateUser(Mockito.any(UserDto.class), Mockito.anyLong());
    }

    @Test
    void updateWithNullFields() throws Exception {
        Mockito
                .when(mokUserService.updateUser(Mockito.any(UserDto.class), Mockito.anyLong()))
                .thenReturn(new UserDto(1L, userDto.getName(), userDto.getEmail()));

        userDto.setName(null);
        userDto.setEmail(null);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Petya")))
                .andExpect(jsonPath("$.email", is("Petya@mail.ru")));
    }

    @Test
    void updateWithIncorrectId() {
        // Проверка исключения т.к. mvc работает без контейнера ошибок springBoot.
        // https://github.com/spring-projects/spring-boot/issues/7321#issuecomment-261343803
        // Почему тогда валидация внутри DTO отрабатывает и возвращает ответ - загадка

        assertThrows(NestedServletException.class, () ->
                mvc.perform(patch("/users/0")
                                .content(mapper.writeValueAsString(userDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()));

        assertThrows(NestedServletException.class, () -> mvc.perform(patch("/users/-1")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)));

        Mockito
                .verify(mokUserService, Mockito.never())
                .updateUser(Mockito.any(UserDto.class), Mockito.anyLong());
    }

    @Test
    void getAllUserTest() throws Exception {
        UserDto userDto1 = new UserDto(1L, "Vitya", "vitya@mail.ru");
        UserDto userDto2 = new UserDto(2L, "Vova", "vova@mail.ru");
        UserDto userDto3 = new UserDto(3L, "Petya", "petya@mail.ru");

        Mockito
                .when(mokUserService.getAllUsers())
                .thenReturn(List.of(userDto1, userDto2, userDto3));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath(("$.[0].id"), is(1)))
                .andExpect(jsonPath(("$.[0].name"), is("Vitya")))
                .andExpect(jsonPath(("$.[0].email"), is("vitya@mail.ru")))
                .andExpect(jsonPath(("$.[1].id"), is(2)))
                .andExpect(jsonPath(("$.[1].name"), is("Vova")))
                .andExpect(jsonPath(("$.[1].email"), is("vova@mail.ru")))
                .andExpect(jsonPath(("$.[2].id"), is(3)))
                .andExpect(jsonPath(("$.[2].name"), is("Petya")))
                .andExpect(jsonPath(("$.[2].email"), is("petya@mail.ru")));
    }

    @Test
    void getAllUsersEmpty() throws Exception {
        Mockito
                .when(mokUserService.getAllUsers())
                .thenReturn(List.of());

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(0)));

        Mockito
                .verify(mokUserService, Mockito.times(1))
                .getAllUsers();
    }

    @Test
    void correctGetUserById() throws Exception {
        Mockito
                .when(mokUserService.getUser(1L))
                .thenReturn(new UserDto(1L, userDto.getName(), userDto.getEmail()));

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        Mockito
                .verify(mokUserService, Mockito.times(1))
                .getUser(1);
    }

    @Test
    void deleteUserTest() throws Exception {
        Mockito
                .when(mokUserService.deleteUser(1L))
                .thenReturn(true);

        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));

        Mockito
                .when(mokUserService.deleteUser(2L))
                .thenReturn(false);

        mvc.perform(delete("/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(false)));

        Mockito
                .verify(mokUserService, Mockito.times(1))
                .deleteUser(1);

        Mockito
                .verify(mokUserService, Mockito.times(1))
                .deleteUser(2);
    }

}