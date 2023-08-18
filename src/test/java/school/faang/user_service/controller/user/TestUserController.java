package school.faang.user_service.controller.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.user.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestUserController {
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;
    private UserDto userDto;
    private UserDto userCreated;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        userDto = UserDto.builder()
                .username("test")
                .email("test@test.com")
                .password("test")
                .build();
        userCreated = UserDto.builder()
                .id(1L)
                .username("test")
                .email("test@test.com")
                .password("test")
                .build();
    }

    @Test
    public void testCreateUser() throws Exception {
        Mockito.when(userService.createUser(userDto))
                .thenReturn(userCreated);

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userCreated)));
    }
}
