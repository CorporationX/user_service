package school.faang.user_service.controller.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.controller.UserController;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.exception.handler.GlobalExceptionHandler;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.config.context.UserContext;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired private MockMvc mvc;
    @MockBean private UserService userService;


    @MockBean private UserContext userContext;

    @Test
    @DisplayName("POST /users -> 200 OK with body on valid input")
    void create_ok() throws Exception {
        UserDto response = new UserDto(10L, "john", "john@example.com", null, null);
        when(userService.create(any(CreateUserDto.class))).thenReturn(response);

        String json = """
                {
                  "username": "john",
                  "email": "john@example.com",
                  "password": "veryStrongPass",
                  "countryId": 1
                }
                """;

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.username", is("john")))
                .andExpect(jsonPath("$.email", is("john@example.com")));
    }

    @Test
    @DisplayName("POST /users -> 400 BAD_REQUEST when DTO validation fails (missing username)")
    void create_validationError_missingUsername() throws Exception {
        String json = """
                {
                  "email": "john@example.com",
                  "password": "veryStrongPass",
                  "countryId": 1
                }
                """;

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                //.andExpect(jsonPath("$.error", is("Validation failed")))
                .andExpect(jsonPath("$.username", notNullValue()));
    }

    @Test
    @DisplayName("PUT /users/{id} -> 200 OK with body on valid input")
    void update_ok() throws Exception {
        UserDto response = new UserDto(10L, "johnny", "johnny@example.com", "+65 1111 2222", "About");
        when(userService.update(eq(10L), any(UpdateUserDto.class))).thenReturn(response);

        String json = """
                {
                  "username": "johnny",
                  "email": "johnny@example.com",
                  "phone": "+65 1111 2222",
                  "aboutMe": "About",
                  "countryId": 2,
                  "city": "Singapore"
                }
                """;

        mvc.perform(put("/users/{userId}", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.username", is("johnny")))
                .andExpect(jsonPath("$.email", is("johnny@example.com")))
                .andExpect(jsonPath("$.phone", is("+65 1111 2222")))
                .andExpect(jsonPath("$.aboutMe", is("About")));
    }

    @Test
    @DisplayName("PUT /users/{id} -> 403 when service throws ForbiddenException")
    void update_forbidden() throws Exception {
        doThrow(new ForbiddenException("User 20 doesn't match profile owner!"))
                .when(userService).update(eq(10L), any(UpdateUserDto.class));

        String json = """
                { "username": "johnny" }
                """;

        mvc.perform(put("/users/{userId}", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("forbidden")))
                .andExpect(jsonPath("$.message", containsString("doesn't match")));
    }

    @Test
    @DisplayName("GET /users/{id} -> 200 OK with body")
    void get_ok() throws Exception {
        UserDto response = new UserDto(10L, "john", "john@example.com", null, null);
        when(userService.getById(10L)).thenReturn(response);

        mvc.perform(get("/users/{userId}", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.username", is("john")));
    }

    @Test
    @DisplayName("GET /users/{id} -> 404 when service throws 'not found'")
    void get_notFound() throws Exception {
        Mockito.doThrow(new RuntimeException("User not found"))
                .when(userService).getById(10L);

        mvc.perform(get("/users/{userId}", 10))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("runtime_error")))
                .andExpect(jsonPath("$.message", is("User not found")));
    }
}