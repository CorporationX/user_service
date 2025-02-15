package school.faang.user_service.controller.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.GlobalExceptionHandler;
import school.faang.user_service.service.user.UserService;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UserDto dto = new UserDto();

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        dto.setId(1L);
        dto.setUsername("John");
        dto.setEmail("john@gmail.com");

        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Test must returned user by id")
    void getUser() throws Exception {
        Mockito.when(userService.getUserById(1L)).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("John"))
                .andExpect(jsonPath("$.email").value("john@gmail.com"));

        Mockito.verify(userService, Mockito.times(1)).getUserById(1L);
    }

    @Test
    @DisplayName("Test must returned users by id")
    void getUsersByIds() throws Exception {
        Mockito.when(userService.getUsersByIds(List.of(1L))).thenReturn(List.of(dto));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("John"))
                .andExpect(jsonPath("$[0].email").value("john@gmail.com"));

        Mockito.verify(userService, times(1)).getUsersByIds(List.of(1L));
    }
}